package xyz.matt.replicate.core.people;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import xyz.matt.replicate.core.util.JsonUtil;

import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

public class PersonProducer {

    private final String topicName;
    private final Producer<String, String> producer;

    public PersonProducer(String topicName, Producer<String, String> producer) {
        this.topicName = topicName;
        this.producer = checkNotNull(producer);
    }

    public void emitPerson(Person person) throws ExecutionException, InterruptedException {
        final ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(topicName, person.getFirstName(), JsonUtil.toJsonNoException(person));

        producer.send(producerRecord).get();
    }
}
