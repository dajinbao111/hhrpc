package com.hhrpc.hhrpc.core.consumer;

import com.hhrpc.hhrpc.core.api.RpcContent;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.util.HhRpcMethodUtils;
import com.hhrpc.hhrpc.core.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class HhRpcConsumerInvocationHandler implements InvocationHandler {

    private final String serviceName;
    private RpcContent rpcContent;
    private List<InstanceMeta> instanceMetaList;

    public HhRpcConsumerInvocationHandler(String serviceName, RpcContent rpcContent, List<InstanceMeta> instanceMetaList) {
        this.serviceName = serviceName;
        this.rpcContent = rpcContent;
        this.instanceMetaList = instanceMetaList;
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
            InstanceMeta instanceMeta = rpcContent.getLoadBalance().choose(rpcContent.getRouter().rout(instanceMetaList));
            RpcResponse<?> rpcResponse = rpcContent.getHttpInvoker().post(request, instanceMeta.toUrl());
            return TypeUtils.getRpcResponse(method, rpcResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
