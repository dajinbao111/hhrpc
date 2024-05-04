package com.hhrpc.hhrpc.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HhRpcMethodUtils {

    /**
     * 检查是否是本地方法
     * @param method
     * @return
     */
    public static boolean checkLocalMethod(Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    /**
     * 创建方法签名
     * @param method
     * @return
     */
    public static String createMethodSign(Method method) {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String args = "";
        if (parameterTypes != null || parameterTypes.length > 0) {
            args = Arrays.stream(parameterTypes).map(Class::getCanonicalName).collect(Collectors.joining("&"));
        }
        return String.format("%s#%s", methodName, args);
    }

    public static List<Field> findAnnotationFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Field> results = new ArrayList<>();
        while (Objects.nonNull(clazz)) {
            List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(annotationClass)).toList();
            if (!fields.isEmpty()) {
                results.addAll(fields);
            }
            clazz = clazz.getSuperclass();
        }
        return results;
    }
}
