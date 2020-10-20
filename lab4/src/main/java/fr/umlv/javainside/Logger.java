package fr.umlv.javainside;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Objects;
import java.util.function.Consumer;

import static java.lang.invoke.MethodType.methodType;

public interface Logger {
    public void log(String message);

    public static Logger of(Class<?> declaringClass, Consumer<? super String> consumer) throws NoSuchMethodException, IllegalAccessException {
        Objects.requireNonNull(declaringClass);
        Objects.requireNonNull(consumer);
        var mh = createLoggingMethodHandle(declaringClass, consumer);
        return new Logger() {
            @Override
            public void log(String message) {
                try {
                    mh.invokeExact(message);
                } catch(RuntimeException | Error e) {
                    throw e;
                } catch(Throwable t) {
                    throw new UndeclaredThrowableException(t);
                }
            }
        };
    }

    public static Logger lambdaOf(Class<?> declaringClass, Consumer<? super String> consumer) throws NoSuchMethodException, IllegalAccessException {
        Objects.requireNonNull(declaringClass);
        Objects.requireNonNull(consumer);
        var mh = createLoggingMethodHandle(declaringClass, consumer);
        return (message) -> {
            try {
                mh.invokeExact(message);
            } catch(RuntimeException | Error e) {
                throw e;
            } catch(Throwable t) {
                throw new UndeclaredThrowableException(t);
            }
        };
    }

    public static Logger recordOf(Class<?> declaringClass, Consumer<? super String> consumer) throws NoSuchMethodException, IllegalAccessException {
        Objects.requireNonNull(declaringClass);
        Objects.requireNonNull(consumer);
        var mh = createLoggingMethodHandle(declaringClass, consumer);
        return (message) -> {
            try {
                mh.invokeExact(message);
            } catch(RuntimeException | Error e) {
                throw e;
            } catch(Throwable t) {
                throw new UndeclaredThrowableException(t);
            }
        };
    }

    class Impl{
        private static final MethodHandle ACCEPT;
        static {
            var lookup = MethodHandles.lookup();
            try {
                ACCEPT = lookup.findVirtual(Consumer.class,"accept", methodType(void.class, Object.class));
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }

        private static final ClassValue<MutableCallSite> ENABLE_CALLSITES = new ClassValue<>() {
            protected MutableCallSite computeValue(Class<?> type) {
                return new MutableCallSite(MethodHandles.constant(boolean.class, true));
            }
        };

        public static void enable(Class<?> declaringClass, boolean enable) {
            ENABLE_CALLSITES.get(declaringClass).setTarget(MethodHandles.constant(boolean.class, enable));
        }
    }

    private static MethodHandle createLoggingMethodHandle(Class<?> declaringClass, Consumer<? super String> consumer) throws NoSuchMethodException, IllegalAccessException {
        var target = Impl.ACCEPT.bindTo(consumer);
        target = target.asType(methodType(void.class, String.class));

        var test = Impl.ENABLE_CALLSITES.get(declaringClass).dynamicInvoker();
        var fallback = MethodHandles.empty(methodType(void.class, String.class));

        return MethodHandles.guardWithTest(test, target, fallback);
    }
}
