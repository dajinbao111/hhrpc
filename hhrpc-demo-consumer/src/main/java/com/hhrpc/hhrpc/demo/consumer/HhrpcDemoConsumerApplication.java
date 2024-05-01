package com.hhrpc.hhrpc.demo.consumer;

import com.hhrpc.hhrpc.core.annotation.HhRpcConsumer;
import com.hhrpc.hhrpc.core.conf.ConsumerConf;
import com.hhrpc.hhrpc.demo.api.Order;
import com.hhrpc.hhrpc.demo.api.OrderService;
import com.hhrpc.hhrpc.demo.api.User;
import com.hhrpc.hhrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Import(ConsumerConf.class)
public class HhrpcDemoConsumerApplication {

    @HhRpcConsumer
    private UserService userService;
    @HhRpcConsumer
    private OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(HhrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return (x) -> {
            User user = this.userService.findById(1120);
            System.out.println(user);

            Order order = orderService.findById(221);
            System.out.println(order);

            System.out.println(orderService.toString());

            System.out.println(userService.findById(221, "hhrpc"));

            System.out.println(userService.findUser(new User(1, "a")));

            System.out.println(Arrays.toString(orderService.findLongArray(new long[]{1l, 2l, 3l, 4l})));

            System.out.println(orderService.findListOrder(Arrays.asList(new Order(1, "1"), new Order(2, "2"))));

            Map<String, Order> map = new HashMap<>();
            map.put("001", new Order(111, "map001"));
            map.put("002", new Order(222, "map002"));
            System.out.println(orderService.findMapOrder(map));
        };
    }
}
