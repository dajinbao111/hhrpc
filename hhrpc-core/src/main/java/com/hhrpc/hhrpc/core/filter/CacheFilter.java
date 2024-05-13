package com.hhrpc.hhrpc.core.filter;

import com.google.gson.Gson;
import com.hhrpc.hhrpc.core.api.Filter;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CacheFilter implements Filter {

    private static final Map<String, Object> cache = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    @Override
    public Object preFilter(RpcRequest rpcRequest) {
        if (Objects.isNull(rpcRequest)) {
            return null;
        }
        return cache.get(gson.toJson(rpcRequest));
    }

    @Override
    public Object postFilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result) {
        if (Objects.isNull(rpcResponse) || Objects.isNull(result)) {
            return null;
        }
        return cache.putIfAbsent(gson.toJson(rpcRequest), result);
    }
}
