package com.hhrpc.hhrpc.core.provider;

import com.google.common.base.Strings;
import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.util.HhRpcMethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class ProviderBootstrap implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    private Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 对象创建完，还没有初始化的时候，拿到这些类
    private MultiValueMap<String, ProviderMeta> serviceMap = new LinkedMultiValueMap<>();
    private String instance;

    @PostConstruct
    public void init() {
        String port = environment.getProperty("server.port");
        try {
            String hostAddress =InetAddress.getLocalHost().getHostAddress();
            this.instance = Strings.lenientFormat("%s_%s", hostAddress, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HhRpcProvider.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Class<?>[] interfaces = entry.getValue().getClass().getInterfaces();
            for (Class<?> anInterface : interfaces) {
                Method[] methods = anInterface.getMethods();
                for (Method method : methods) {
                    if (HhRpcMethodUtils.checkLocalMethod(method)) {
                        continue;
                    }
                    ProviderMeta providerMeta = new ProviderMeta();
                    providerMeta.setMethod(method);
                    providerMeta.setMethodSign(HhRpcMethodUtils.createMethodSign(method));
                    providerMeta.setServiceImpl(entry.getValue());
                    serviceMap.add(anInterface.getCanonicalName(), providerMeta);
                }
            }
        }
    }

    public void start() {
        // 启动zk
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        registerCenter.start();
        // 注册
        this.register();
    }

    private void register() {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        serviceMap.keySet().forEach(service -> {
            registerCenter.register(service, instance);
        });
    }

    private void unregister() {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        serviceMap.keySet().forEach(service -> {
            registerCenter.unregister(service, instance);
        });
    }

    @PreDestroy
    public void stop() {
        unregister();
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public MultiValueMap<String, ProviderMeta> getServiceMap() {
        return serviceMap;
    }
}
