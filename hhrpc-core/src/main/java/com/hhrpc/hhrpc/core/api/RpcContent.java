package com.hhrpc.hhrpc.core.api;

import com.hhrpc.hhrpc.core.consumer.HttpInvoker;
import lombok.Data;

import java.util.List;

@Data
public class RpcContent {

    List<Filter> filterList;
    private Router router;
    private LoadBalance loadBalance;
    private HttpInvoker httpInvoker;

    public RpcContent(Router router, LoadBalance loadBalance, HttpInvoker httpInvoker) {
        this.router = router;
        this.loadBalance = loadBalance;
        this.httpInvoker = httpInvoker;
    }
}
