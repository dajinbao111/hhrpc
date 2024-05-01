package com.hhrpc.hhrpc.core.api;

import lombok.Data;

import java.util.List;

@Data
public class RpcContent {

    List<Filter> filterList;
    private Router router;
    private LoadBalance loadBalance;

    public RpcContent(Router router, LoadBalance loadBalance) {
        this.router = router;
        this.loadBalance = loadBalance;
    }
}
