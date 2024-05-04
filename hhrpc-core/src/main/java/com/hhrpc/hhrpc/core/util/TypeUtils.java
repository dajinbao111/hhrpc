package com.hhrpc.hhrpc.core.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TypeUtils {

    public static Class<?> cast(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }
        if (Integer.class.isAssignableFrom(clazz) || Integer.TYPE.equals(clazz)) {
            return Integer.class;
        } else if (Short.class.isAssignableFrom(clazz) || Short.TYPE.equals(clazz)) {
            return Short.class;
        } else if (Character.class.isAssignableFrom(clazz) || Character.TYPE.equals(clazz)) {
            return Character.class;
        } else if (Long.class.isAssignableFrom(clazz) || Long.TYPE.equals(clazz)) {
            return Long.class;
        } else if (Boolean.class.isAssignableFrom(clazz) || Boolean.TYPE.equals(clazz)) {
            return Boolean.class;
        } else if (Double.class.isAssignableFrom(clazz) || Double.TYPE.equals(clazz)) {
            return Double.class;
        } else if (Float.class.isAssignableFrom(clazz) || Float.TYPE.equals(clazz)) {
            return Float.class;
        }
        return clazz;
    }

    private static Object cast(Object origin, Class<?> paramterType, Type genericParameterType) {
        if (Objects.isNull(origin)) {
            return null;
        }
        if (Integer.class.isAssignableFrom(paramterType) || Integer.TYPE.equals(paramterType)) {
            return Integer.valueOf(origin.toString());
        } else if (Short.class.isAssignableFrom(paramterType) || Short.TYPE.equals(paramterType)) {
            return Short.valueOf(origin.toString());
        } else if (Character.class.isAssignableFrom(paramterType) || Character.TYPE.equals(paramterType)) {
            return Character.valueOf(origin.toString().charAt(0));
        } else if (Long.class.isAssignableFrom(paramterType) || Long.TYPE.equals(paramterType)) {
            return Long.valueOf(origin.toString());
        } else if (Boolean.class.isAssignableFrom(paramterType) || Boolean.TYPE.equals(paramterType)) {
            return Boolean.valueOf(origin.toString());
        } else if (Double.class.isAssignableFrom(paramterType) || Double.TYPE.equals(paramterType)) {
            return Double.valueOf(origin.toString());
        } else if (Float.class.isAssignableFrom(paramterType) || Float.TYPE.equals(paramterType)) {
            return Float.valueOf(origin.toString());
        }

        Gson gson = new Gson();
        String json = gson.toJson(origin);
        if (List.class.isAssignableFrom(paramterType)) {
            if (genericParameterType instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeToken<?> parameterized = TypeToken.getParameterized(List.class, actualTypeArguments);
                return gson.fromJson(json, parameterized.getType());
            }
        }
        if (Map.class.isAssignableFrom(paramterType)) {
            if (genericParameterType instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeToken<?> parameterized = TypeToken.getParameterized(Map.class, actualTypeArguments);
                return gson.fromJson(json, parameterized.getType());
            }
        }
        return gson.fromJson(json, paramterType);
    }

    public static Object[] processArgs(Object[] args, Class<?>[] parameterTypes, Type[] genericParameterTypes) {
        if (args == null || args.length == 0) {
            return args;
        }
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = cast(args[i], parameterTypes[i], genericParameterTypes[i]);
        }
        return result;
    }

    public static RpcResponse getRpcResponse(Method method, Response response) throws IOException {
        Gson gson = new Gson();
        String responseData = response.body().string();
        // 反序列化
        Class<?> realClass = TypeUtils.cast(method.getReturnType());
        TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, realClass);
        Type genericReturnType = method.getGenericReturnType();
        if (List.class.isAssignableFrom(realClass)) {
            if (genericReturnType instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeToken<?> listTypeToken = TypeToken.getParameterized(List.class, actualTypeArguments);
                parameterized = TypeToken.getParameterized(RpcResponse.class, listTypeToken.getType());
            }
        }
        if (Map.class.isAssignableFrom(realClass)) {
            if (genericReturnType instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeToken<?> mapTypeToken = TypeToken.getParameterized(Map.class, actualTypeArguments);
                parameterized = TypeToken.getParameterized(RpcResponse.class, mapTypeToken.getType());
            }
        }
        return gson.fromJson(responseData, parameterized.getType());
    }
}
