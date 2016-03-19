package xyz.matt.replicate.core.people;

import com.google.common.collect.ImmutableSet;
import org.apache.kafka.clients.consumer.Consumer;
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

    static final int POOL_SIZE = 10;

    private final PersonConsumerFactory personConsumerFactory;
    private final PersonCassandraDao personCassandraDao;
    private final ExecutorService executorService;

    private final ImmutableSet<PersonCassandraReplicatorWorker> workers;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    public PersonReplicator(PersonConsumerFactory personConsumerFactory, PersonCassandraDao personCassandraDao) {
        this.personConsumerFactory = checkNotNull(personConsumerFactory);
        this.personCassandraDao = checkNotNull(personCassandraDao);
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
        if (running.get()) {
            stopAllWorkers();
            stopExecutor();
        }
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
