package com.hhrpc.hhrpc.core.consumer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.util.HhRpcMethodUtils;
import com.hhrpc.hhrpc.core.util.TypeUtils;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HhRpcConsumerInvocationHandler implements InvocationHandler {

    private final String serviceName;

    public HhRpcConsumerInvocationHandler(String serviceName) {
        this.serviceName = serviceName;
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
        RpcResponse response = getRpcResponse(request, method.getReturnType(), method.getGenericReturnType());
        if (response.getStatus()) {
            return response.getData();
        } else {
            return null;
        }
    }

    private RpcResponse getRpcResponse(RpcRequest request, Class<?> returnType, Type genericReturnType) {
        Gson gson = new Gson();
        String requestData = gson.toJson(request);
        try {
            Response response = client.newCall(new Request.Builder()
                    .url("http://localhost:8080")
                    .post(RequestBody.create(requestData, MediaType.get("application/json; charset=utf-8")
                    )).build()).execute();
            String responseData = response.body().string();

            // 反序列化
            Class<?> realClass = TypeUtils.cast(returnType);
            if (List.class.isAssignableFrom(realClass)) {
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeToken<?> listTypeToken = TypeToken.getParameterized(List.class, actualTypeArguments);
                    TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, listTypeToken.getType());
                    return gson.fromJson(responseData, parameterized.getType());
                }
            }
            if (Map.class.isAssignableFrom(realClass)) {
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeToken<?> mapTypeToken = TypeToken.getParameterized(Map.class, actualTypeArguments);
                    TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, mapTypeToken.getType());
                    return gson.fromJson(responseData, parameterized.getType());
                }
            }
            TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, realClass);
            return gson.fromJson(responseData, parameterized.getType());
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
