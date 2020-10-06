package fr.umlv.javainside;

import org.junit.jupiter.api.Test;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;

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

    private record Person(@JSONPrinter.JSONProperty("first-name") String firstName, @JSONPrinter.JSONProperty("last-name")String lastName) {
        public Person {
            requireNonNull(firstName);
            requireNonNull(lastName);
        }
    }

    @Test
    public void toJSON(){
        var person = new Person("John", "Doe");
        assertEquals(IncompleteJSONParser.parse("{ \"first-name\": \"John\", \"last-name\": \"Doe\"}"),IncompleteJSONParser.parse(JSONPrinter.toJSON(person)));
        var alien = new Alien(100, "Saturn");
        assertEquals(IncompleteJSONParser.parse("{ \"age\": 100, \"planet\": \"Saturn\"}"),IncompleteJSONParser.parse(JSONPrinter.toJSON(alien)));
    }
}