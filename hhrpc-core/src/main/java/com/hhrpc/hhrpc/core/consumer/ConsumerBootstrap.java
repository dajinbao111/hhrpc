package com.hhrpc.hhrpc.core.consumer;

import com.hhrpc.hhrpc.core.annotation.HhRpcConsumer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

public class ConsumerBootstrap implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private Map<String, Object> proxyMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void screenConsumerServiceFields() throws IllegalAccessException {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            // 获取这个bean中，所有带有@HhRpcClient注解的字段
            List<Field> fieldList = findServiceFields(bean.getClass());
            if (fieldList.isEmpty()) {
                continue;
            }
            for (Field field : fieldList) {
                String serviceName = field.getType().getCanonicalName();
                Object targetObj = null;
                if (proxyMap.containsKey(serviceName)) {
                    targetObj = proxyMap.get(serviceName);
                } else {
                    targetObj = createTargetObj(field.getType());
                    proxyMap.put(serviceName, targetObj);
                }
                field.setAccessible(true);
                field.set(bean, targetObj);
            }
        }
    }

    private Object createTargetObj(Class<?> clazz) {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new HhRpcConsumerInvocationHandler(clazz.getCanonicalName()));
    }


    private List<Field> findServiceFields(Class<?> clazz) {
        List<Field> results = new ArrayList<>();
        while (Objects.nonNull(clazz)) {
            List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(HhRpcConsumer.class)).toList();
            if (!fields.isEmpty()) {
                results.addAll(fields);
            }
            clazz = clazz.getSuperclass();
        }
        return results;
    }
}
