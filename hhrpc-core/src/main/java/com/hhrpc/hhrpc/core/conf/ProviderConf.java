package com.hhrpc.hhrpc.core.conf;

import com.hhrpc.hhrpc.core.provider.ProviderBootstrap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderConf {

    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }


}
