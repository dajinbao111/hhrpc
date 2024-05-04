package com.hhrpc.hhrpc.core.consumer;

import com.hhrpc.hhrpc.core.api.RpcContent;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.util.HhRpcMethodUtils;
import com.hhrpc.hhrpc.core.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class HhRpcConsumerInvocationHandler implements InvocationHandler {

    private final String serviceName;
    private RpcContent rpcContent;
    private List<String> providers;

    public HhRpcConsumerInvocationHandler(String serviceName, RpcContent rpcContent, List<String> providers) {
        this.serviceName = serviceName;
        this.rpcContent = rpcContent;
        this.providers = providers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setServiceName(serviceName);
        request.setArgs(args);
        request.setMethodSign(HhRpcMethodUtils.createMethodSign(method));
        if (HhRpcMethodUtils.checkLocalMethod(method)) {
            return null;
        }
        RpcResponse response = getRpcResponse(request, method);
        if (response.getStatus()) {
            return response.getData();
        } else {
            return null;
        }
    }

    private RpcResponse getRpcResponse(RpcRequest request, Method method) {
        try {
            String url = (String) rpcContent.getLoadBalance().choose(rpcContent.getRouter().rout(providers));
            RpcResponse<?> rpcResponse = rpcContent.getHttpInvoker().post(request, url);
            return TypeUtils.getRpcResponse(method, rpcResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
