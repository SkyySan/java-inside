package fr.umlv.javainside;

import org.junit.jupiter.api.Test;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.util.List;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.methodType;
import static org.junit.jupiter.api.Assertions.*;

public class MethodHandleTests {

    @Test
    public void findStaticTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = lookup();
        var methodHandle = lookup.findStatic(Integer.class,"parseInt", methodType(int.class, String.class));
        assertEquals(methodType(int.class,String.class),methodHandle.type());
    }

    @Test
    public void findVirtualTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = lookup();
        var methodHandle = lookup.findVirtual(String.class,"toUpperCase", methodType(String.class));
        assertEquals(methodType(String.class,String.class),methodHandle.type());
    }

    @Test
    public void invokeExactStaticTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findStatic(Integer.class,"parseInt", methodType(int.class, String.class));
        assertEquals(123,(int)methodHandle.invokeExact("123"));
    }

    @Test
    public void invokeExactStaticWrongArgumentTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findStatic(Integer.class,"parseInt", methodType(int.class, String.class));
        assertThrows(WrongMethodTypeException.class, () -> methodHandle.invokeExact());
    }

    @Test
    public void invokeExactVirtualTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findVirtual(String.class,"toUpperCase", methodType(String.class));
        assertEquals("AAA",(String) methodHandle.invokeExact("aaa"));
    }

    @Test
    public void invokeExactVirtualWrongArgumentTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findVirtual(String.class,"toUpperCase", methodType(String.class));
        assertThrows(WrongMethodTypeException.class, () -> methodHandle.invokeExact());
    }

    @Test
    public void invokeStaticTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findStatic(Integer.class,"parseInt", methodType(int.class, String.class));
        assertAll(
                () -> assertEquals(123,(Integer) methodHandle.invoke("123")),
                () -> assertThrows(WrongMethodTypeException.class, () -> {var s = (String) methodHandle.invoke();})
        );
    }

    @Test
    public void invokeVirtualTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findVirtual(String.class,"toUpperCase", methodType(String.class));
        assertAll(
                () -> assertEquals("AAA",(Object) methodHandle.invoke("aaa")),
                () -> assertThrows(WrongMethodTypeException.class, () -> {var d = (Double) methodHandle.invoke();})
        );
    }

    @Test
    public void insertAndInvokeStaticTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findStatic(Integer.class,"parseInt", methodType(int.class, String.class));
        assertAll(
                () -> assertEquals(123, (Integer) MethodHandles.insertArguments(methodHandle,0,"123").invoke()),
                () -> assertThrows(WrongMethodTypeException.class, () -> {var s = (String) MethodHandles.insertArguments(methodHandle,0).invoke();})
        );
    }

    @Test
    public void bindToAndInvokeVirtualTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findVirtual(String.class,"toUpperCase", methodType(String.class));
        assertAll(
                () -> assertEquals("AAA", (String) methodHandle.bindTo("aaa").invoke()),
                () -> assertThrows(ClassCastException.class, () -> {var s = (String) methodHandle.bindTo(new Object()).invoke();})
        );
    }
    @Test
    public void dropAndInvokeStaticTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findStatic(Integer.class,"parseInt", methodType(int.class, String.class));
        assertAll(
                () -> assertEquals(123, (Integer) MethodHandles.dropArguments(methodHandle,0,String.class).invoke("0", "123")),
                () -> assertThrows(WrongMethodTypeException.class, () -> {var s = (String) MethodHandles.dropArguments(methodHandle,0,String.class).invoke("0");})
        );
    }

    @Test
    public void dropAndInvokeVirtualTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findVirtual(String.class,"toUpperCase", methodType(String.class));
        assertAll(
                () -> assertEquals("AAA", (String) MethodHandles.dropArguments(methodHandle,0,String.class).invoke("0", "aaa")),
                () -> assertThrows(WrongMethodTypeException.class, () -> {var s = (String) MethodHandles.dropArguments(methodHandle,0,String.class).invoke("0");})
        );
    }

    @Test
    public void asTypeAndInvokeExactStaticTest() throws Throwable {
        var lookup = lookup();
        var methodHandle = lookup.findStatic(Integer.class,"parseInt", methodType(int.class, String.class));
        assertAll(
                () -> assertEquals(123,(Integer) methodHandle.asType(methodType(Integer.class,String.class)).invokeExact("123")),
                () -> assertThrows(WrongMethodTypeException.class, () -> {var s = (String) methodHandle.asType(methodType(Integer.class,String.class)).invokeExact();})
        );
    }

    @Test
    public void invokeExactConstantTest() throws Throwable {
        var methodHandle = MethodHandles.constant(Integer.class,42);
        assertEquals(42, methodHandle.invoke());
    }

    private static MethodHandle match(String s) throws NoSuchMethodException, IllegalAccessException {
        var equals = MethodHandles.lookup().findVirtual(String.class, "equals", methodType(boolean.class, Object.class));
        var test = insertArguments(equals, 1, s);
        var target = dropArguments(constant(int.class, 1), 0 , String.class);
        var fallback = dropArguments(constant(int.class, -1), 0 , String.class);
        return guardWithTest(test,target,fallback);
    }

    @Test
    public void matchTest() throws Throwable {
        var m = match("hello");
        assertAll(
                () -> assertEquals(1, (int) m.invokeExact("hello")),
                () -> assertEquals(-1, (int) m.invokeExact("bad"))
        );
    }

    private static MethodHandle matchAll(List<String> list) throws NoSuchMethodException, IllegalAccessException {
        var lookup = lookup();
        var mh = lookup.findVirtual(String.class, "equals", methodType(boolean.class, Object.class));
        var target = dropArguments(constant(int.class, -1), 0 , String.class);
        var index = 0;
        for(var text:list){
            var test = insertArguments(mh, 1, text);
            var ok = dropArguments(constant(int.class, index), 0 , String.class);
            target = guardWithTest(test, ok, target);
            index++;
        }
        return target;
    }

    @Test
    public void matchAllTest() throws Throwable {
        var m = matchAll(List.of("hello", "java", "inside"));
        assertAll(
                () -> assertEquals(0, (int) m.invokeExact("hello")),
                () -> assertEquals(1, (int) m.invokeExact("java")),
                () -> assertEquals(2, (int) m.invokeExact("inside")),
                () -> assertEquals(-1, (int) m.invokeExact("bad"))
        );
    }
}