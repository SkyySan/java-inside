package fr.umlv.javainside;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;

import static java.lang.invoke.MethodType.methodType;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

public class MethodHandleTests {

    @Test
    public void findStaticTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodHandle = lookup.findStatic(Integer.class,"parseInt", methodType(int.class, String.class));
        assertEquals(methodType(int.class,String.class),methodHandle.type());
    }

    @Test
    public void findVirtualTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodHandle = lookup.findVirtual(String.class,"toUpperCase", methodType(String.class));
        assertEquals(methodType(String.class,String.class),methodHandle.type());
    }

    @Test
    public void invokeExactStaticTest() throws Throwable {
        var lookup = MethodHandles.lookup();
        var methodHandle = lookup.findStatic(Integer.class,"parseInt", methodType(int.class, String.class));
        assertEquals(123,(int)methodHandle.invokeExact("123"));
    }

    @Test
    public void invokeExactStaticWrongArgumentTest() throws Throwable {
        var lookup = MethodHandles.lookup();
        var methodHandle = lookup.findStatic(Integer.class,"parseInt", methodType(int.class, String.class));
        assertThrows(WrongMethodTypeException.class, methodHandle::invokeExact);

    }

    @Test
    public void invokeExactVirtualTest() throws Throwable {
        var lookup = MethodHandles.lookup();
        var methodHandle = lookup.findVirtual(String.class,"toUpperCase", methodType(String.class));
        assertEquals("AAA",(String) methodHandle.invokeExact("aaa"));
    }

    @Test
    public void invokeExactVirtualWrongArgumentTest() throws Throwable {
        var lookup = MethodHandles.lookup();
        var methodHandle = lookup.findVirtual(String.class,"toUpperCase", methodType(String.class));
        assertThrows(WrongMethodTypeException.class, methodHandle::invokeExact);

    }
}