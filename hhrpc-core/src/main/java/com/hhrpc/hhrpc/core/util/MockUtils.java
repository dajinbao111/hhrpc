package com.hhrpc.hhrpc.core.util;

import com.hhrpc.hhrpc.core.api.RpcRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

public class MockUtils {
    public static Object mock(RpcRequest rpcRequest) {
        try {
            Class<?> aClass = Class.forName(rpcRequest.getServiceName());
            Method method = findMethod(aClass, rpcRequest.getMethodSign());
            return mock(method.getReturnType(), method.getGenericReturnType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object mock(Class<?> aClass, Type genericType) {
        if (Integer.class.equals(aClass) || Integer.TYPE.equals(aClass)) {
            return 1;
        } else if (Short.class.equals(aClass) || Short.TYPE.equals(aClass)) {
            return Short.valueOf("2");
        } else if (Character.class.equals(aClass) || Character.TYPE.equals(aClass)) {
            return '3';
        } else if (Long.class.equals(aClass) || Long.TYPE.equals(aClass)) {
            return 4L;
        } else if (Float.class.equals(aClass) || Float.TYPE.equals(aClass)) {
            return 5.1f;
        } else if (Double.class.equals(aClass) || Double.TYPE.equals(aClass)) {
            return 5.2d;
        } else if (Boolean.class.equals(aClass) || Boolean.TYPE.equals(aClass)) {
            return false;
        }
        if (String.class.equals(aClass)) {
            return "is_a_mock_string";
        }
        return createPOJO(aClass, genericType);
    }

    private static Object createPOJO(Class<?> aClass, Type genericType) {
        try {
            Object result = aClass.getConstructor().newInstance();
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object val = mock(field.getType(), field.getGenericType());
                field.set(result, val);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method findMethod(Class<?> aClass, String methodSign) {
        return Arrays.stream(aClass.getMethods())
                .filter(m -> HhRpcMethodUtils.createMethodSign(m).equals(methodSign))
                .findAny().orElse(null);
    }
}
