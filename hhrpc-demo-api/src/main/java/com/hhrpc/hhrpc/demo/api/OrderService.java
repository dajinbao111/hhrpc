package com.hhrpc.hhrpc.demo.api;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order findById(Integer oid);

    long[] findLongArray(long[] arr);

    List<Order> findListOrder(List<Order> list);

    Map<String, Order> findMapOrder(Map<String, Order> map);
}
