package com.hhrpc.hhrpc.core.register;

import com.hhrpc.hhrpc.core.api.RegisterCenter;

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
    public void register(String service, String instance) {

    }

    @Override
    public void unregister(String service, String instance) {

    }

    @Override
    public List<String> findAll(String service) {
        return provider;
    }

    @Override
    public void subscribe(String service) {

    }
}
