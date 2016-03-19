package xyz.matt.replicate.core.people;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Preconditions;
import xyz.matt.replicate.core.util.JsonUtil;

public class PersonCassandraDao {

    static final String INSERT_PERSON_CQL = "INSERT INTO person (first_name, revision_id, json) VALUES (?, ?, ?)";
    private final PreparedStatement insertPerson;

    static final String SELECT_PERSON_CQL = "SELECT * FROM person WHERE first_name = ? LIMIT 1";
    private final PreparedStatement selectPerson;

    private final Session session;

    public PersonCassandraDao(Session session) {
        this.session = Preconditions.checkNotNull(session);
        insertPerson = session.prepare(INSERT_PERSON_CQL);
        selectPerson = session.prepare(SELECT_PERSON_CQL);
    }

    public void upsertPerson(Person person) {
        final Statement statement = insertPerson
                .bind(person.getFirstName(), UUIDs.timeBased(), JsonUtil.toJsonNoException(person));

        session.execute(statement);
    }

    public Person getPerson(String personName) {
        final Statement statement = selectPerson.bind(personName);
        final Row row = session.execute(statement).one();

        return JsonUtil.createObjectFromJson(row.getString("json"), Person.class);
    }
}
