package com.hhrpc.hhrpc.core.provider;

import com.hhrpc.hhrpc.core.api.HhRpcExceptionEnum;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.meta.ProviderMeta;
import com.hhrpc.hhrpc.core.util.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> serviceMap;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.serviceMap = providerBootstrap.getServiceMap();
    }

    public RpcResponse<Object> invokeMethod(RpcRequest rpcRequest) {
        RpcResponse<Object> result = new RpcResponse<>();
        result.setStatus(false);
        try {
            ProviderMeta providerMeta = fetchProviderMeta(rpcRequest);
            Method method = providerMeta.getMethod();
            Object[] args = TypeUtils.processArgs(rpcRequest.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object data = method.invoke(providerMeta.getServiceImpl(), args);
            result.setStatus(true);
            result.setData(data);
            return result;
        } catch (InvocationTargetException e) {
            result.setErrorCode(HhRpcExceptionEnum.X002.getErrorCode());
        } catch (IllegalAccessException e) {
            result.setErrorCode(HhRpcExceptionEnum.Z001.getErrorCode());
        }
        return result;
    }

    private ProviderMeta fetchProviderMeta(RpcRequest rpcRequest) {
        return serviceMap.get(rpcRequest.getServiceName()).stream()
                .filter(item -> item.getMethodSign().equals(rpcRequest.getMethodSign()))
                .findAny().orElse(null);
    }
}
