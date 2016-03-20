package xyz.matt.replicate.core.people;

import com.google.common.collect.ImmutableList;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.matt.replicate.core.util.JsonUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkNotNull;

public class PersonCassandraReplicatorWorker implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(PersonCassandraReplicatorWorker.class);

    private final Consumer<String, String> consumer;
    private final PersonCassandraDao personCassandraDao;
    private final AtomicBoolean keepRunning;

    public PersonCassandraReplicatorWorker(Consumer<String, String> consumer, PersonCassandraDao personCassandraDao) {
        this.consumer = checkNotNull(consumer);
        this.personCassandraDao = checkNotNull(personCassandraDao);
        keepRunning = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        logger.info("Starting person-cassandra-replicator-worker");
        consumer.subscribe(ImmutableList.of("replicate"));
        while (keepRunning.get()) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> currentRecord : records) {
                final Person person = JsonUtil.createObjectFromJson(currentRecord.value(), Person.class);
                logger.info("Attempting to write person to cassandra.");
                personCassandraDao.upsertPerson(person);
                logger.info("Success write person to cassandra, person-first-name={}", person.getFirstName());
            }
            consumer.commitSync();
        }
    }

    public void stop() {
        logger.info("Stopping person-cassandra-replicator-worker");
        keepRunning.getAndSet(false);
    }
}
