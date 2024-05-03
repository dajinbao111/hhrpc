package com.hhrpc.hhrpc.core.api;

import java.util.List;

public interface RegisterCenter {

    void start();

    void stop();

    void register(String service, String instance);

    void unregister(String service, String instance);

    List<String> findAll(String service);

    void subscribe(String service, EventListener eventListener);
}
