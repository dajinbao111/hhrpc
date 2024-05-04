package com.hhrpc.hhrpc.core.api;

import com.hhrpc.hhrpc.core.meta.InstanceMeta;

import java.util.List;

public interface RegisterCenter {

    void start();

    void stop();

    void register(String service, InstanceMeta instanceMeta);

    void unregister(String service, InstanceMeta instanceMeta);

    List<InstanceMeta> findAll(String service);

    void subscribe(String service, EventListener eventListener);
}
