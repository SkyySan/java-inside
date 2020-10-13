package fr.umlv.javainside;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoggerTest {

    @Test
    public void ofTest(){
        class Foo{}
        var logger = Logger.of(Foo.class, String::toUpperCase);
        assertNotNull(logger);
    }

    @Test
    public void logTest(){

    }
}