package com.hhrpc.hhrpc.core.consumer;

import com.google.common.base.Strings;
import com.hhrpc.hhrpc.core.annotation.HhRpcConsumer;
import com.hhrpc.hhrpc.core.api.LoadBalance;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.api.RpcContent;
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
import java.util.stream.Collectors;

public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {
    private ApplicationContext applicationContext;
    private Environment environment;
    private Map<String, Object> proxyMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void screenConsumerServiceFields() throws IllegalAccessException {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        Router router = applicationContext.getBean(Router.class);
        LoadBalance loadBalance = applicationContext.getBean(LoadBalance.class);
        RpcContent rpcContent = new RpcContent(router, loadBalance);
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
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
        List<String> nodes = registerCenter.findAll(serviceName);
        List<String> providers = createProviders(nodes);
        registerCenter.subscribe(serviceName, (event) -> {
            providers.clear();
            providers.addAll(createProviders(event.getData()));
        });
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new HhRpcConsumerInvocationHandler(serviceName, rpcContent, providers));
    }

    private List<String> createProviders(List<String> nodes) {
        return nodes.stream().map(node -> Strings.lenientFormat("http://%s", node.replace("_", ":"))).collect(Collectors.toList());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
