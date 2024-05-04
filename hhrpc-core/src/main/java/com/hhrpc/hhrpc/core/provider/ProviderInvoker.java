package com.hhrpc.hhrpc.core.provider;

import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.meta.ProviderMeta;
import com.hhrpc.hhrpc.core.util.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;

public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> serviceMap;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.serviceMap = providerBootstrap.getServiceMap();
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
}
