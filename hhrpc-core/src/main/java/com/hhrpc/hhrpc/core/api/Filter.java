package com.hhrpc.hhrpc.core.api;

public interface Filter {

    /**
     * 前置过滤器
     * @param rpcRequest
     * @return
     */
    Object preFilter(RpcRequest rpcRequest);

    /**
     * 后置过滤器
     * @param rpcRequest
     * @param rpcResponse
     * @return
     */
    Object postFilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result);

    Filter DEFAULT = new Filter() {
        @Override
        public Object preFilter(RpcRequest rpcRequest) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result) {
            return result;
        }
    };
}
