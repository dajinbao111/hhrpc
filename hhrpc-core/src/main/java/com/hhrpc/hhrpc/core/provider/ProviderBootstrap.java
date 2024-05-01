package com.hhrpc.hhrpc.core.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.util.HhRpcMethodUtils;
import com.hhrpc.hhrpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.util.Map;

public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 对象创建完，还没有初始化的时候，拿到这些类
    private MultiValueMap<String, ProviderMeta> serviceMap = new LinkedMultiValueMap<>();

    @PostConstruct
    public void buildProviders() {
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

    public RpcResponse invokeMethod(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = new RpcResponse();
        try {
            ProviderMeta providerMeta = fetchProviderMeta(rpcRequest);
            Method method = providerMeta.getMethod();
            Object[] args = TypeUtils.processArgs(rpcRequest.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object data = method.invoke(providerMeta.getServiceImpl(), args);
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

    private ProviderMeta fetchProviderMeta(RpcRequest rpcRequest) {
        return serviceMap.get(rpcRequest.getServiceName()).stream()
                .filter(item -> item.getMethodSign().equals(rpcRequest.getMethodSign()))
                .findAny().orElse(null);
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
