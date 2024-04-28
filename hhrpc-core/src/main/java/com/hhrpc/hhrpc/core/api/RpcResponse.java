package com.hhrpc.hhrpc.core.api;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RpcResponse<T> {

    private Boolean status;
    private T data;
}
