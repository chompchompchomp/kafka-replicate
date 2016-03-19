package xyz.matt.replicate.core.people;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class PersonController {

    private final PersonCassandraDao personCassandraDao;
    private final PersonMessenger personMessenger;

    @Autowired
    public PersonController(PersonCassandraDao personCassandraDao, PersonMessenger personMessenger) {
        this.personCassandraDao = personCassandraDao;
        this.personMessenger = personMessenger;
    }

    @RequestMapping(value = "/people/{personName}", method = RequestMethod.GET)
    public Person getPerson(@PathVariable("personName") String personName) {
        return personCassandraDao.getPerson(personName);
    }

    @RequestMapping(value = "/people/{personName}", method = RequestMethod.PUT)
    public void putPerson(@PathVariable("personName") String personName, @RequestBody Person person) throws ExecutionException, InterruptedException {
        personMessenger.emitPerson(person);
    }
}
