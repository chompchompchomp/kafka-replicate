package xyz.matt.replicate.core.people;

import com.google.common.collect.ImmutableList;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import xyz.matt.replicate.core.util.JsonUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkNotNull;

public class PersonCassandraReplicator implements Runnable{

    private final Consumer<String, String> consumer;
    private final PersonCassandraDao personCassandraDao;
    private final AtomicBoolean keepRunning;

    public PersonCassandraReplicator(Consumer<String, String> consumer, PersonCassandraDao personCassandraDao) {
        this.consumer = checkNotNull(consumer);
        this.personCassandraDao = checkNotNull(personCassandraDao);
        keepRunning = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        consumer.subscribe(ImmutableList.of("replicate"));
        while (keepRunning.get()) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> currentRecord : records) {
                final Person person = JsonUtil.createObjectFromJson(currentRecord.value(), Person.class);
                personCassandraDao.upsertPerson(person);
            }
            consumer.commitSync();
        }
    }

    public void stop() {
        keepRunning.getAndSet(false);
    }
}
