package com.hhrpc.hhrpc.demo.provider;

import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.conf.ProviderConf;
import com.hhrpc.hhrpc.core.provider.ProviderBootstrap;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@Import(ProviderConf.class)
public class HhrpcDemoProviderApplication {

    @Resource
    private ProviderBootstrap providerBootstrap;

    public static void main(String[] args) {
        SpringApplication.run(HhrpcDemoProviderApplication.class, args);
    }

    // HTTP + JSON 方式通信
    // RPC + JSON 方式通信

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public RpcResponse invoke(@RequestBody RpcRequest rpcRequest) {
        return providerBootstrap.invokeMethod(rpcRequest);
    }

}
