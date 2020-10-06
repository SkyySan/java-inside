package fr.umlv.javainside;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.spi.ResourceBundleControlProvider;
import java.util.stream.Collectors;

public class JSONPrinter {

    private static final Cache CACHE = new Cache();

    //@Target(ElementType.RECORD_COMPONENT)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JSONProperty{
        String value();
    }

    private static class Cache extends ClassValue<List<Function<Record,String>>>{

        protected List<Function<Record, String>> computeValue(Class<?> type){
            return Arrays.stream(type.getRecordComponents())
                    .<Function<Record,String>>map(component -> {
                        var prefix = "\"" + name(component) + "\" : ";
                        return record -> prefix + escape(invokeMethod(record, component));
                    })
                    .collect(Collectors.toList());
        }

    }
    private static String name(RecordComponent component){
        var property = component.getAnnotation(JSONProperty.class);
        if(property == null)
            return component.getName();
        return property.value();
    }

    private static String escape(Object o){
        return (o instanceof String) ? "\"" + o + "\"" : "" + o;
    }

    public static String toJSON(Record record){
        return CACHE.get(record.getClass())
                .stream()
                .map(fun -> fun.apply(record))
                .collect(Collectors.joining(", ","{","}"));
    }
    /*
    public static String toJSON(Record record){
        return "{ " +
                Arrays.stream(record.getClass()
                .getRecordComponents())
                .map(RecordComponent::getAccessor)
                .map(m -> "\"" + methodName(m) + "\" : " + invokeMethod(record, m))
                .map(Object::toString)
                .collect(Collectors.joining(", "))
                + " }" ;
    }

    private static String methodName(Method m) {
        var name = m.getAnnotation(JSONProperty.class);
        return (name != null) ? name.value() : m.getName();
    }
    */
    /*
    private static String name(RecordComponent component){
        var property = component.getAnnotation(JSONProperty.class);
        if(property == null)
            return component.getName();
        return property.value();
    }
    */
    private static Object invokeMethod(Record record, RecordComponent rc){
        try {
            final var invoke = rc.getAccessor().invoke(record);
                return invoke;
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
