package xyz.matt.replicate.core.people;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class PersonController {

    private final PersonCassandraDao personCassandraDao;
    private final PersonProducer personProducer;

    @Autowired
    public PersonController(PersonCassandraDao personCassandraDao, PersonProducer personProducer) {
        this.personCassandraDao = personCassandraDao;
        this.personProducer = personProducer;
    }

    @RequestMapping(value = "/people/{personName}", method = RequestMethod.GET)
    public Person getPerson(@PathVariable("personName") String personName) {
        return personCassandraDao.getPerson(personName);
    }

    @RequestMapping(value = "/people/{personName}", method = RequestMethod.PUT)
    public void putPerson(@PathVariable("personName") String personName, @RequestBody Person person) throws ExecutionException, InterruptedException {
        personProducer.emitPerson(person);
    }
}
