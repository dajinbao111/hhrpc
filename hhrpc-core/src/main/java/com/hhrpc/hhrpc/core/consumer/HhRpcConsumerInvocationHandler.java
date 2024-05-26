package com.hhrpc.hhrpc.core.consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hhrpc.hhrpc.core.api.*;
import com.hhrpc.hhrpc.core.governance.SlidingTimeWindow;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.util.HhRpcMethodUtils;
import com.hhrpc.hhrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HhRpcConsumerInvocationHandler implements InvocationHandler {

    private final String serviceName;
    private RpcContent rpcContent;
    private List<InstanceMeta> providers;
    // 隔离的providers
    private Set<InstanceMeta> isolatedProviders = Sets.newHashSet();
    // 探活列表
    private List<InstanceMeta> halfOpenProviders = Lists.newArrayList();
    // 滑动
    private Map<String, SlidingTimeWindow> slidingTimeWindowMap = new HashMap<>();
    private final ScheduledExecutorService scheduledExecutorService;

    public HhRpcConsumerInvocationHandler(String serviceName, RpcContent rpcContent, List<InstanceMeta> providers) {
        this.serviceName = serviceName;
        this.rpcContent = rpcContent;
        this.providers = providers;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorService.scheduleAtFixedRate(this::halfOpen, 10, 30, TimeUnit.SECONDS);

    }

    public void halfOpen() {
        this.halfOpenProviders.clear();
        this.halfOpenProviders.addAll(isolatedProviders);
        log.debug("===> halfOpenProviders:{}, isolatedProviders:{}, providers:{}", halfOpenProviders, isolatedProviders, providers);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setServiceName(serviceName);
        request.setArgs(args);
        request.setMethodSign(HhRpcMethodUtils.createMethodSign(method));
        if (HhRpcMethodUtils.checkLocalMethod(method)) {
            return null;
        }
        int retries = Integer.parseInt(rpcContent.getParameters().get("app.retries"));
        String url = null;
        Object result = null;
        while (retries-- > 0) {
            try {
                log.debug("==> reties {}", retries);
                List<Filter> filterList = rpcContent.getFilterList();
                for (Filter filter : filterList) {
                    result = filter.preFilter(request);
                    if (Objects.nonNull(result)) {
                        log.debug("===> filter: {}", result);
                        return result;
                    }
                }
                RpcResponse<?> rpcResponse;
                InstanceMeta instanceMeta = null;
                try {
                    synchronized (halfOpenProviders) {
                        if (halfOpenProviders.isEmpty()) {
                            instanceMeta = rpcContent.getLoadBalance().choose(rpcContent.getRouter().rout(providers));
                        } else {
                            instanceMeta = halfOpenProviders.remove(0);
                            log.debug("===> try life url instance:{}", instanceMeta);
                        }
                    }
                    url = instanceMeta.toUrl();
                    log.debug("===> invoke: {}", request);
                    rpcResponse = rpcContent.getHttpInvoker().post(request, url);
                    if (!rpcResponse.getStatus()) {
                        HhRpcExceptionEnum hhRpcExceptionEnum = HhRpcExceptionEnum.findHhRpcExceptionEnum(rpcResponse.getErrorCode());
                        throw new HhRpcException(hhRpcExceptionEnum.getErrorMessage());
                    }
                } catch (Exception e) {
                    log.info("===> fault url : {}", url);
                    slidingTimeWindowMap.putIfAbsent(url, new SlidingTimeWindow());
                    SlidingTimeWindow slidingTimeWindow = slidingTimeWindowMap.get(url);
                    slidingTimeWindow.record(System.currentTimeMillis());
                    int sum = slidingTimeWindow.getSum();
                    log.debug("===> url fault count:{}", sum);
                    if (sum >= 10) {
                        isolated(instanceMeta);
                    }
                    throw e;
                }

                synchronized (providers) {
                    // 探活成功
                    if (providers.contains(instanceMeta)) {
                        isolatedProviders.remove(instanceMeta);
                        providers.add(instanceMeta);
                    }
                }

                rpcResponse = TypeUtils.getRpcResponse(method, rpcResponse);
                result = rpcResponse.getData();
                log.debug("===> post result: {}", result);

                for (Filter filter : filterList) {
                    result = filter.postFilter(request, rpcResponse, result);
                    if (Objects.nonNull(result)) {
                        return result;
                    }
                }
                return result;
            } catch (Exception e) {
                if (!(e.getCause() instanceof SocketTimeoutException)) {
                    throw e;
                }
                log.error("===> SocketTimeoutException {}", url);
            }
        }
        return result;
    }

    /**
     * 隔离
     *
     * @param instanceMeta
     */
    private void isolated(InstanceMeta instanceMeta) {
        log.debug("");
        providers.remove(instanceMeta);
        isolatedProviders.add(instanceMeta);
    }

    private RpcResponse getRpcResponse(RpcRequest request, Method method) {
        try {
            InstanceMeta instanceMeta = rpcContent.getLoadBalance().choose(rpcContent.getRouter().rout(providers));
            RpcResponse<?> rpcResponse = rpcContent.getHttpInvoker().post(request, instanceMeta.toUrl());
            return TypeUtils.getRpcResponse(method, rpcResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
