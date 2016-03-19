package xyz.matt.replicate.core.people;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.matt.replicate.core.util.JsonUtil;

import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

public class PersonMessenger {
    private static final Logger logger = LoggerFactory.getLogger(PersonMessenger.class);

    private final String topicName;
    private final Producer<String, String> producer;

    public PersonMessenger(String topicName, Producer<String, String> producer) {
        this.topicName = topicName;
        this.producer = checkNotNull(producer);
    }

    public void emitPerson(Person person) throws ExecutionException, InterruptedException {
        final ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(topicName, person.getFirstName(), JsonUtil.toJsonNoException(person));

        RecordMetadata metaData = producer.send(producerRecord).get();
        logger.info("successfully emitted a person, partion={}, offset={}", metaData.partition(), metaData.offset());
    }
}
