package com.hhrpc.hhrpc.core.consumer;

import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;

public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest request, String url);
}
