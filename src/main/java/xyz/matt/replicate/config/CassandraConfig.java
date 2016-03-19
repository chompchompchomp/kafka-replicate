package xyz.matt.replicate.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {

    @Bean
    public Session session() {
        final Cluster cluster = Cluster.builder()
                .addContactPoint("localhost")
                .withPort(9042)
                .build();

        return cluster.connect("replicate");
    }
}
