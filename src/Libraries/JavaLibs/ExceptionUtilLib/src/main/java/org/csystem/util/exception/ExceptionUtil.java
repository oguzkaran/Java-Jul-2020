/*----------------------------------------------------------------------
FILE        : ExceptionUtil.java
AUTHOR      : Oğuz Karan
LAST UPDATE : 30.09.2020

ExceptionUtil class for exception managing

Copyleft (c) 1993 by C and System Programmers Association (CSD)
All Rights Free
-----------------------------------------------------------------------*/
package org.csystem.util.exception;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ExceptionUtil {
    private ExceptionUtil()
    {}

    private static <T extends RuntimeException> void throwException(String msg, Class<T> cls, Throwable ex)
    {
        try {
            throw cls.getConstructor(String.class, Throwable.class).newInstance(msg, ex);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new UnsupportedOperationException("Fault for exception class");
        }
    }

    public static <T extends RuntimeException> void doWorkForRunnable(IActionCallback actionCallback, String msg, Class<T> cls)
    {
        try {
            actionCallback.run();
        }
        catch (Throwable ex) {
            throwException(msg, cls, ex);
        }
    }

    public static <T extends RuntimeException> void doWorkForRunnable(IActionCallback actionCallback, Consumer<Throwable> consumer, String msg, Class<T> cls)
    {
        try {
            actionCallback.run();
        }
        catch (Throwable ex) {
            consumer.accept(ex);
            throwException(msg, cls, ex);
        }
    }

    public static <T extends RuntimeException, R> R doWorkFor(ISupplierCallback<R> supplier, String msg, Class<T> cls)
    {
        R result = null;

        try {
            result = supplier.get();
        }
        catch (Throwable ex) {
            throwException(msg, cls, ex);
        }

        return result;
    }

    public static <T extends RuntimeException, R> R doWorkFor(ISupplierCallback<R> supplier, Consumer<Throwable>  consumer, String msg, Class<T> cls)
    {
        R result = null;

        try {
            result = supplier.get();
        }
        catch (Throwable ex) {
            consumer.accept(ex);
            throwException(msg, cls, ex);
        }

        return result;
    }

    public static <R> R doWorkForRuntimeException(ISupplierCallback<R> supplier, String msg)
    {
        try {
            return supplier.get();
        }
        catch (Throwable ex) {
            throw new RuntimeException(msg, ex);
        }
    }

    public static void doWorkForRuntimeException(IActionCallback actionCallback, String msg)
    {
        try {
            actionCallback.run();
        }
        catch (Throwable ex) {
            throw new RuntimeException(msg, ex);
        }
    }

    public static <R> R subscribe(ISupplierCallback<R> supplier, Function<Throwable, R> function)
    {
        try {
            return supplier.get();
        }
        catch (Throwable ex) {
            return function.apply(ex);
        }
    }

    public static void subscribeRunnable(IActionCallback actionCallback, Consumer<Throwable> consumer)
    {
        try {
            actionCallback.run();
        }
        catch (Throwable ex) {
            consumer.accept(ex);
        }
    }
}
