package com.hhrpc.hhrpc.core.cluster;

import com.hhrpc.hhrpc.core.api.LoadBalance;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalance<T> implements LoadBalance<T> {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public T choose(List<T> urls) {
        if (Objects.isNull(urls) || urls.isEmpty()) {
            return null;
        }
        if (urls.size() == 1) {
            return urls.get(0);
        }
        return urls.get((atomicInteger.getAndIncrement() & 0x7fffffff) % urls.size());
    }
}
