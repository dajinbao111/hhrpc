package com.hhrpc.hhrpc.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;
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
}
