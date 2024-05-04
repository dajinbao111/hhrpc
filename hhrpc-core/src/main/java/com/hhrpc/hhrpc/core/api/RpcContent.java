package com.hhrpc.hhrpc.core.api;

import com.hhrpc.hhrpc.core.consumer.HttpInvoker;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

@Data
public class RpcContent {

    List<Filter> filterList;
    private Router<InstanceMeta> router;
    private LoadBalance<InstanceMeta> loadBalance;
    private HttpInvoker httpInvoker;

    public RpcContent(Router<InstanceMeta> router, LoadBalance<InstanceMeta> loadBalance, HttpInvoker httpInvoker) {
        this.router = router;
        this.loadBalance = loadBalance;
        this.httpInvoker = httpInvoker;
    }
}
