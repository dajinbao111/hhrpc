package com.hhrpc.hhrpc.core.consumer;

import com.google.gson.Gson;
import com.hhrpc.hhrpc.core.api.RpcContent;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.util.HhRpcMethodUtils;
import com.hhrpc.hhrpc.core.util.TypeUtils;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        Gson gson = new Gson();
        String requestData = gson.toJson(request);
        try {
            String url = (String) rpcContent.getLoadBalance().choose(rpcContent.getRouter().rout(providers));
            Response response = client.newCall(new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestData, MediaType.get("application/json; charset=utf-8")
                    )).build()).execute();
            return TypeUtils.getRpcResponse(method, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build();


}
