package com.hhrpc.hhrpc.core.register;

import com.google.common.base.Strings;
import com.hhrpc.hhrpc.core.api.Event;
import com.hhrpc.hhrpc.core.api.EventListener;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Objects;

public class ZkRegisterCenter implements RegisterCenter {

    private CuratorFramework client;
    private TreeCache treeCache;

    @Override
    public void start() {
        final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3, 1000);
        this.client = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .connectString("localhost:2181")
                .namespace("hhrpc")
                .build();
        this.client.start();
    }

    @Override
    public void stop() {
        this.client.close();
        if (Objects.nonNull(treeCache)) {
            treeCache.close();
        }
    }

    @Override
    public void register(String service, String instance) {
        String servicePath = Strings.lenientFormat("/%s", service);
        try {
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            String instancePath = Strings.lenientFormat("/%s/%s", service, instance);
            if (Objects.isNull(client.checkExists().forPath(instancePath))) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, String instance) {
        String servicePath = Strings.lenientFormat("/%s", service);
        try {
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                return;
            }
            String instancePath = Strings.lenientFormat("/%s/%s", service, instance);
            if (Objects.nonNull(client.checkExists().forPath(instancePath))) {
                client.delete().quietly().forPath(instancePath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> findAll(String service) {
        String servicePath = Strings.lenientFormat("/%s", service);
        try {
            return client.getChildren().forPath(servicePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(String service, EventListener eventListener) {
        String servicePath = Strings.lenientFormat("/%s", service);
        treeCache = TreeCache.newBuilder(client, servicePath)
                .setCacheData(true)
                .setMaxDepth(2)
                .build();
        treeCache.getListenable().addListener((curatorFramework, treeCacheEvent) -> {
            System.out.println("====> subscribe");
            List<String> nodes = findAll(service);
            eventListener.fire(new Event(nodes));
        });
        try {
            treeCache.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
