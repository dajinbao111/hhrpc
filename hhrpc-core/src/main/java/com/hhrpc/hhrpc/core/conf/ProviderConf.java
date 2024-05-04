package com.hhrpc.hhrpc.core.conf;

import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.provider.ProviderBootstrap;
import com.hhrpc.hhrpc.core.register.ZkRegisterCenter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ProviderConf {

    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean//(initMethod = "start", destroyMethod = "stop")
    public RegisterCenter registerCenter() {
        return new ZkRegisterCenter();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner runStart(ProviderBootstrap providerBootstrap) {
        return x -> {
            providerBootstrap.start();
        };
    }
}
