package fr.umlv.javainside;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
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

    public static String toJSON(Record record){
        return "{ " +
                Arrays.stream(record.getClass()
                .getRecordComponents())
                .map(RecordComponent::getAccessor)
                .map(m -> "\"" + m.getName() + "\" : " + invokeMethod(record, m))
                .map(Object::toString)
                .collect(Collectors.joining(", "))
                + " }" ;
    }

    private static Object invokeMethod(Record record, Method m){
        try {
            final var invoke = m.invoke(record);
            if(m.getReturnType().getName().equals("java.lang.String"))
                return "\"" + invoke +"\"";
            else {
                return invoke;
            }
        } catch (IllegalAccessException e) {
            throw (IllegalAccessError) new IllegalAccessError().initCause(e);
        } catch (InvocationTargetException e) {
            var cause = e.getCause();
            if(cause instanceof RuntimeException re)
                throw re;
            if(cause instanceof Error error)
                throw error;
            throw new UndeclaredThrowableException(cause);
        }
    }
}
