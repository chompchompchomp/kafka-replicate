package xyz.matt.replicate.core.people;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PeopleSpringConfig {
    private static final Logger logger = LoggerFactory.getLogger(PeopleSpringConfig.class);

    @Bean
    public PersonConsumerFactory personConsumerFactory() {
        ImmutableMap<String, Object> properties = new ImmutableMap.Builder<String, Object>()
                .put("bootstrap.servers", "localhost:9092")
                .put("group.id", "test")
                .put("enable.auto.commit", "false")
                .put("auto.commit.interval.ms", "1000")
                .put("session.timeout.ms", "30000")
                .put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
                .put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
                .build();

        return new PersonConsumerFactory(properties);
    }

    @Bean @Autowired
    public PersonMessenger personMessenger(Producer<String, String> personProducer) {
        return new PersonMessenger("replicate", personProducer);
    }
}
