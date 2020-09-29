package fr.umlv.javainside;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class JSONPrinter {
    /*
    public static String toJSON(Person person) {
        return """
      {
        "firstName": "%s",
        "lastName": "%s"
      }
      """.formatted(person.firstName(), person.lastName());
    }

    public static String toJSON(Alien alien) {
        return """
      {
        "age": %s,
        "planet": "%s"
      }
      """.formatted(alien.age(), alien.planet());
    }
    */

    public static String toJSON(Record record) {
        return Arrays.stream(record.getClass().getRecordComponents()).map(Object::toString).collect(Collectors.joining(","));
    }
    public static void main(String[] args) {
        var person = new Person("John", "Doe");
        System.out.println(toJSON(person));
        var alien = new Alien(100, "Saturn");
        System.out.println(toJSON(alien));
    }
}
