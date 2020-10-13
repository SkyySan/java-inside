package fr.umlv.javainside;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoggerTest {

    @Test
    public void ofTest() throws NoSuchMethodException, IllegalAccessException {
        class Foo{}
        var logger = Logger.of(Foo.class, __ -> {});
        assertNotNull(logger);
    }

    @Test
    public void ofError(){
        class Foo{}
        assertAll(
                ()-> assertThrows(NullPointerException.class,()-> Logger.of(null, __-> {})),
                ()-> assertThrows(NullPointerException.class,()-> Logger.of(Foo.class, null))
        );
    }

    @Test
    public void logTest() throws NoSuchMethodException, IllegalAccessException {
        class Foo{}
        var logger = Logger.of(Foo.class, message -> {
            assertEquals("hello", message);
        });
        logger.log("hello");
    }

    @Test
    public void logWithNullAsValue() throws NoSuchMethodException, IllegalAccessException {
        class Foo{}
        var logger = Logger.of(Foo.class, Assertions::assertNull);
        logger.log(null);
    }

    @Test
    public void lambdaOfTest() throws NoSuchMethodException, IllegalAccessException {
        class Foo{}
        var logger = Logger.lambdaOf(Foo.class, __ -> {});
        assertNotNull(logger);
    }
}