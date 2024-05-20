package com.hhrpc.hhrpc.core.api;

import com.google.common.base.MoreObjects;
import lombok.Data;

@Data
public class RpcResponse<T> {

    private Boolean status;
    private T data;
    private HhRpcException ex;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(RpcResponse.class)
                .add("status", status)
                .add("data", data)
                .add("ex", ex)
                .toString();
    }
}
