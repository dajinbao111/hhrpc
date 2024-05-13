package com.hhrpc.hhrpc.core.filter;

import com.hhrpc.hhrpc.core.api.Filter;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.util.MockUtils;

public class MockFilter implements Filter {
    @Override
    public Object preFilter(RpcRequest rpcRequest) {
        return MockUtils.mock(rpcRequest);
    }

    @Override
    public Object postFilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result) {
        return null;
    }
}
