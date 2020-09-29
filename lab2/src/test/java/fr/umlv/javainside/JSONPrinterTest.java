package fr.umlv.javainside;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

public class JSONPrinterTest {

    private record Alien(int age, String planet) {
        public Alien {
            if (age < 0) {
                throw new IllegalArgumentException("negative age");
            }
            requireNonNull(planet);
        }
    }

    private record Person(String firstName, String lastName) {
        public Person {
            requireNonNull(firstName);
            requireNonNull(lastName);
        }
    }

    @Test
    public void toJSON(){
        var person = new Person("John", "Doe");
        assertEquals(IncompleteJSONParser.parse("{ \"firstName\": \"John\", \"lastName\": \"Doe\"}"),IncompleteJSONParser.parse(JSONPrinter.toJSON(person)));
        var alien = new Alien(100, "Saturn");
        assertEquals(IncompleteJSONParser.parse("{ \"age\": 100, \"planet\": \"Saturn\"}"),IncompleteJSONParser.parse(JSONPrinter.toJSON(alien)));
    }
}