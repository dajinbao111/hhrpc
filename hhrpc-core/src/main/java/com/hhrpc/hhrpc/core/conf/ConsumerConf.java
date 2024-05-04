package com.hhrpc.hhrpc.core.conf;

import com.hhrpc.hhrpc.core.api.LoadBalance;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.cluster.RoundRobinLoadBalance;
import com.hhrpc.hhrpc.core.consumer.ConsumerBootstrap;
import com.hhrpc.hhrpc.core.consumer.HttpInvoker;
import com.hhrpc.hhrpc.core.consumer.http.OkHttpInvoker;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.register.ZkRegisterCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ConsumerConf {

    @Value("${hhrpc.providers}")
    private String urls;

    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner screenConsumerServiceFields(ConsumerBootstrap consumerBootstrap) {
        return x -> {
            consumerBootstrap.start();
        };
    }

    @Bean
    public Router<InstanceMeta> router() {
        return Router.DEFAULT;
    }

    @Bean
    public LoadBalance<InstanceMeta> loadBalance() {
        return new RoundRobinLoadBalance();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegisterCenter registerCenter() {
        return new ZkRegisterCenter();
    }

    @Bean
    public HttpInvoker httpInvoker() {
        return new OkHttpInvoker();
    }
}
