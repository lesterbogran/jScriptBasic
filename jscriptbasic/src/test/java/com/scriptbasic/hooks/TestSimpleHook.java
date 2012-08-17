package com.scriptbasic.hooks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.mockito.Mockito;

import com.scriptbasic.interfaces.ExtendedInterpreter;

public class TestSimpleHook {

    @Test
    public void testExMethods() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Method[] methods = SimpleHook.class.getDeclaredMethods();
        SimpleHook simpleHook = new SimpleHook();
        ExtendedInterpreter interpreter = Mockito
                .mock(ExtendedInterpreter.class);
        simpleHook.setInterpreter(interpreter);
        NullHook nullHook = new NullHook();
        simpleHook.setNext(nullHook);
        simpleHook.init();
        for (Method method : methods) {
            String name = method.getName();
            if (!name.equals("setNext") && !name.equals("setInterpreter")) {
                method.setAccessible(true);
                int paramsNum = method.getParameterTypes().length;
                Object[] obj = new Object[paramsNum];
                method.invoke(simpleHook, obj);
            }
        }
    }
}
