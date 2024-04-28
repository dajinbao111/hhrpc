package com.hhrpc.hhrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {

    private String serviceName;
    private String methodName;
    private Object[] args;
}
