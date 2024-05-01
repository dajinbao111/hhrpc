package com.hhrpc.hhrpc.core.cluster;

import com.hhrpc.hhrpc.core.api.LoadBalance;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RandomLoadBalance<T> implements LoadBalance<T> {

    private Random random = new Random(System.currentTimeMillis());

    @Override
    public T choose(List<T> urls) {
        if (Objects.isNull(urls) || urls.isEmpty()) {
            return null;
        }
        if (urls.size() == 1) {
            return urls.get(0);
        }
        return urls.get(random.nextInt(urls.size()));
    }
}
