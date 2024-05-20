package com.hhrpc.hhrpc.core.consumer.http;

import com.google.gson.Gson;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.consumer.HttpInvoker;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpInvoker implements HttpInvoker {

    private final OkHttpClient client;

    public OkHttpInvoker() {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest request, String url) {
        Gson gson = new Gson();
        String requestData = gson.toJson(request);
        try {
            Response response = client.newCall(new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestData, MediaType.get("application/json; charset=utf-8")
                    )).build()).execute();

            return gson.fromJson(response.body().string(), RpcResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
