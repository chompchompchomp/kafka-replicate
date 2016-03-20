package xyz.matt.replicate.core.people;

import com.google.common.collect.ImmutableSet;
import org.apache.kafka.clients.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class PersonReplicator {
    private static final Logger logger = LoggerFactory.getLogger(PersonReplicator.class);

    static final int POOL_SIZE = 2;

    private final ExecutorService executorService;

    private final ImmutableSet<PersonCassandraReplicatorWorker> workers;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    public PersonReplicator(PersonConsumerFactory personConsumerFactory, PersonCassandraDao personCassandraDao) {
        checkNotNull(personConsumerFactory);
        checkNotNull(personCassandraDao);

        this.executorService = Executors.newFixedThreadPool(POOL_SIZE);
        Set<PersonCassandraReplicatorWorker> tempWorkers = new HashSet<>();
        for (int i=0; i < POOL_SIZE; i++) {
            final Consumer<String, String> personConsumer = personConsumerFactory.createPersonConsumer();
            tempWorkers.add(new PersonCassandraReplicatorWorker(personConsumer, personCassandraDao));
        }
        workers = ImmutableSet.copyOf(tempWorkers);
    }

    @PostConstruct
    public synchronized void start() {
        logger.info("Starting person-replicator");
        if (! running.get()) {
            startAllWorkers();
            running.getAndSet(true);
        }
    }

    private void startAllWorkers() {
        for (PersonCassandraReplicatorWorker currentWorker : workers) {
            executorService.submit(currentWorker);
        }
    }

    @PreDestroy
    public void stop() {
        logger.info("Stopping person-replicator");
        if (running.get()) {
            stopAllWorkers();
            stopExecutor();
        }
        logger.info("Stopped person-replicator");
    }

    private void stopExecutor() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void stopAllWorkers() {
        for (PersonCassandraReplicatorWorker currentWorker : workers) {
            currentWorker.stop();
        }
    }
}
