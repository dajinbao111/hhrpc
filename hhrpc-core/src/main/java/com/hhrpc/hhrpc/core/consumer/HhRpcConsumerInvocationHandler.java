package com.hhrpc.hhrpc.core.consumer;

import com.hhrpc.hhrpc.core.api.Filter;
import com.hhrpc.hhrpc.core.api.RpcContent;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.util.HhRpcMethodUtils;
import com.hhrpc.hhrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Slf4j
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
        List<Filter> filterList = rpcContent.getFilterList();
        for (Filter filter : filterList) {
            Object result = filter.preFilter(request);
            if (Objects.nonNull(result)) {
                log.debug("===> filter: {}", result);
                return result;
            }
        }
        log.debug("===> invoke: {}", request);
        RpcResponse response = getRpcResponse(request, method);
        log.debug("===> post result: {}", response);
        if (!response.getStatus()) {
            return null;
        }
        for (Filter filter : filterList) {
            Object result = filter.postFilter(request, response, response.getData());
            if (Objects.nonNull(result)) {
                return result;
            }
        }
        return response.getData();
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
