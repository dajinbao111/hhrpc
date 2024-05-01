package com.hhrpc.hhrpc.core.conf;

import com.hhrpc.hhrpc.core.api.LoadBalance;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.cluster.RoundRobinLoadBalance;
import com.hhrpc.hhrpc.core.consumer.ConsumerBootstrap;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ConsumerConf {

    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner screenConsumerServiceFields(ConsumerBootstrap consumerBootstrap) {
        return x -> {
            consumerBootstrap.screenConsumerServiceFields();
        };
    }

    @Bean
    public Router router() {
        return Router.DEFAULT;
    }

    @Bean
    public LoadBalance loadBalance() {
        return new RoundRobinLoadBalance();
    }
}
