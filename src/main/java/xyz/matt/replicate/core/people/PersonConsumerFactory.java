package xyz.matt.replicate.core.people;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Map;

public class PersonConsumerFactory {

    private final ImmutableMap<String, Object> configuration;

    public PersonConsumerFactory(Map<String, Object> kafkaConsumerProperties) {
        configuration = ImmutableMap.copyOf(kafkaConsumerProperties);
    }

    public Consumer<String, String> createPersonConsumer() {
        return new KafkaConsumer<>(configuration);
    }
}
