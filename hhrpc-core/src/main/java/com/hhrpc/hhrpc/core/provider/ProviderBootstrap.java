package com.hhrpc.hhrpc.core.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 对象创建完，还没有初始化的时候，拿到这些类
    private Map<String, Object> serviceMap = new HashMap<>();

    @PostConstruct
    public void buildProviders() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HhRpcProvider.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Class<?>[] anInterfaces = entry.getValue().getClass().getInterfaces();
            for (Class<?> anInterface : anInterfaces) {
                serviceMap.put(anInterface.getName(), entry.getValue());
            }
        }
    }

    public RpcResponse invokeMethod(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = new RpcResponse();
        String serviceName = rpcRequest.getServiceName();
        Object providerObj = serviceMap.get(serviceName);
        Method method = findMethod(providerObj, rpcRequest.getMethodName());
        try {
            Object data = method.invoke(providerObj, rpcRequest.getArgs());
            rpcResponse.setStatus(true);
            rpcResponse.setData(data);
            return rpcResponse;
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setStatus(false);
            rpcResponse.setData(null);
            return rpcResponse;
        }
    }

    private Method findMethod(Object providerObj, String methodName) {
        Method[] methods = providerObj.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}
