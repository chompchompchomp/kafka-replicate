package xyz.matt.replicate.core.util;

import org.junit.Test;
import xyz.matt.replicate.core.people.Address;
import xyz.matt.replicate.core.people.Person;

import static org.junit.Assert.*;

public class JsonUtilTest {

    @Test
    public void testToJsonNoException() throws Exception {
        Address address = new Address()
                .withNumber(2509)
                .withStreet("Robbins");

        Person person = new Person()
                .withFirstName("matt")
                .withLastName("miller")
                .withFavoriteColor("green")
                .withAddress(address);

        System.out.println(JsonUtil.toJsonNoException(person));
    }
}