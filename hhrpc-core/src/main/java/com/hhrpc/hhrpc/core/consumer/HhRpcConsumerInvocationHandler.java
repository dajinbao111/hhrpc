package com.hhrpc.hhrpc.core.consumer;

import com.hhrpc.hhrpc.core.api.*;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.util.HhRpcMethodUtils;
import com.hhrpc.hhrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
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
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setServiceName(serviceName);
        request.setArgs(args);
        request.setMethodSign(HhRpcMethodUtils.createMethodSign(method));
        if (HhRpcMethodUtils.checkLocalMethod(method)) {
            return null;
        }
        int retries = Integer.parseInt(rpcContent.getParameters().get("app.retries"));
        String url = null;
        Object result = null;
        while (retries-- > 0) {
            try {
                log.debug("==> reties {}", retries);
                List<Filter> filterList = rpcContent.getFilterList();
                for (Filter filter : filterList) {
                    result = filter.preFilter(request);
                    if (Objects.nonNull(result)) {
                        log.debug("===> filter: {}", result);
                        return result;
                    }
                }
                log.debug("===> invoke: {}", request);
                InstanceMeta instanceMeta = rpcContent.getLoadBalance().choose(rpcContent.getRouter().rout(instanceMetaList));
                url = instanceMeta.toUrl();
                RpcResponse<?> rpcResponse = rpcContent.getHttpInvoker().post(request, url);
                if (!rpcResponse.getStatus()) {
                    HhRpcExceptionEnum hhRpcExceptionEnum = HhRpcExceptionEnum.findHhRpcExceptionEnum(rpcResponse.getErrorCode());
                    throw new HhRpcException(hhRpcExceptionEnum.getErrorMessage());
                }
//        Object result = rpcResponse.getData();
//        result = TypeUtils.castFastJsonReturnObject(result, method);
                rpcResponse = TypeUtils.getRpcResponse(method, rpcResponse);
                result = rpcResponse.getData();
                log.debug("===> post result: {}", result);

                for (Filter filter : filterList) {
                    result = filter.postFilter(request, rpcResponse, result);
                    if (Objects.nonNull(result)) {
                        return result;
                    }
                }
                return result;
            } catch (Exception e) {
                if (!(e.getCause() instanceof SocketTimeoutException)) {
                    throw e;
                }
                log.error("===> SocketTimeoutException {}", url);
            }
        }
        return result;
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
