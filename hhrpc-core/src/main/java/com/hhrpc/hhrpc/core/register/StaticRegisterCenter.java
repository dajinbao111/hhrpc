package com.hhrpc.hhrpc.core.register;

import com.hhrpc.hhrpc.core.api.EventListener;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;

import java.util.List;

public class StaticRegisterCenter implements RegisterCenter {

    private List<String> provider;

    public StaticRegisterCenter(List<String> provider) {
        this.provider = provider;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void register(ServiceMeta serviceMeta, InstanceMeta instanceMeta) {

    }

    @Override
    public void unregister(ServiceMeta serviceMeta, InstanceMeta instanceMeta) {

    }

    @Override
    public List<InstanceMeta> findAll(ServiceMeta serviceMeta) {
        return null;
    }

    @Override
    public void subscribe(ServiceMeta serviceMeta, EventListener eventListener) {

    }
}
