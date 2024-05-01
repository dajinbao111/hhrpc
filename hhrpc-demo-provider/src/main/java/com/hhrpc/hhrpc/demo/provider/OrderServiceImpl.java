package com.hhrpc.hhrpc.demo.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.demo.api.Order;
import com.hhrpc.hhrpc.demo.api.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@HhRpcProvider
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer oid) {
        return new Order(oid, "hh-order-" + System.currentTimeMillis());
    }

    @Override
    public long[] findLongArray(long[] arr) {
        return arr;
    }

    @Override
    public List<Order> findListOrder(List<Order> list) {
        return list;
    }

    @Override
    public Map<String, Order> findMapOrder(Map<String, Order> map) {
        return map;
    }
}
