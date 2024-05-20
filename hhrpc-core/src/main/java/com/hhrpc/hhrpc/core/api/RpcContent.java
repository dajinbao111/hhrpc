package com.hhrpc.hhrpc.core.api;

import com.hhrpc.hhrpc.core.consumer.HttpInvoker;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RpcContent {

    List<Filter> filterList;
    private Router<InstanceMeta> router;
    private LoadBalance<InstanceMeta> loadBalance;
    private HttpInvoker httpInvoker;
    private Map<String, String> parameters = new HashMap<>();

    public RpcContent(Router<InstanceMeta> router, LoadBalance<InstanceMeta> loadBalance, HttpInvoker httpInvoker) {
        this.router = router;
        this.loadBalance = loadBalance;
        this.httpInvoker = httpInvoker;
    }
}
