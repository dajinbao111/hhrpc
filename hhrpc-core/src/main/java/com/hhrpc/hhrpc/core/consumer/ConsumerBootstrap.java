package com.hhrpc.hhrpc.core.consumer;

import com.google.common.collect.Lists;
import com.hhrpc.hhrpc.core.annotation.HhRpcConsumer;
import com.hhrpc.hhrpc.core.api.*;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;
import com.hhrpc.hhrpc.core.util.HhRpcMethodUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {
    private ApplicationContext applicationContext;
    private Environment environment;
    private Map<String, Object> proxyMap = new HashMap<>();

    private String app;
    private String namespace;
    private String env;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void start() throws IllegalAccessException {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        Router router = applicationContext.getBean(Router.class);
        LoadBalance loadBalance = applicationContext.getBean(LoadBalance.class);
        HttpInvoker httpInvoker = applicationContext.getBean(HttpInvoker.class);
        List<Filter> filterList = Lists.newArrayList(applicationContext.getBeansOfType(Filter.class).values());
        RpcContent rpcContent = new RpcContent(router, loadBalance, httpInvoker);
        rpcContent.setFilterList(filterList);
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        app = environment.getProperty("app.id");
        namespace = environment.getProperty("app.namespace");
        env = environment.getProperty("app.env");
        int retries = Integer.parseInt(environment.getProperty("app.retries"));
        rpcContent.getParameters().put("app.retries", String.valueOf(retries));
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            // 获取这个bean中，所有带有@HhRpcConsumer注解的字段
            List<Field> fieldList = HhRpcMethodUtils.findAnnotationFields(bean.getClass(), HhRpcConsumer.class);
            if (fieldList.isEmpty()) {
                continue;
            }
            for (Field field : fieldList) {
                String serviceName = field.getType().getCanonicalName();
                Object targetObj;
                if (proxyMap.containsKey(serviceName)) {
                    targetObj = proxyMap.get(serviceName);
                } else {
                    targetObj = createProxyObject(field.getType(), rpcContent, registerCenter);
                    proxyMap.put(serviceName, targetObj);
                }
                field.setAccessible(true);
                field.set(bean, targetObj);
            }
        }
    }

    private Object createProxyObject(Class<?> clazz, RpcContent rpcContent, RegisterCenter registerCenter) {
        String serviceName = clazz.getCanonicalName();
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .namespace(namespace)
                .app(app)
                .env(env)
                .name(serviceName).build();
        List<InstanceMeta> instanceMetaList = registerCenter.findAll(serviceMeta);
        registerCenter.subscribe(serviceMeta, (event) -> {
            instanceMetaList.clear();
            instanceMetaList.addAll(event.getData());
        });
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new HhRpcConsumerInvocationHandler(serviceName, rpcContent, instanceMetaList));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
