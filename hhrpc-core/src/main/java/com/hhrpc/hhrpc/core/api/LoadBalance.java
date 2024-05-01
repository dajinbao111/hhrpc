package com.hhrpc.hhrpc.core.api;

import java.util.List;
import java.util.Objects;

public interface LoadBalance<T> {

    T choose(List<T> urls);

    LoadBalance DEFAULT = (urls) -> {
        if (Objects.isNull(urls) || urls.isEmpty()) {
            return null;
        }
        return urls.get(0);
    };
}
