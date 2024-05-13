package com.hhrpc.hhrpc.demo.consumer;

import com.hhrpc.hhrpc.core.util.MockUtils;
import com.hhrpc.hhrpc.demo.api.User;
import org.junit.jupiter.api.Test;

public class BeanTest {

    @Test
    public void mockUserTest() {
        Object user = MockUtils.mock(User.class, null);
        System.out.println(user);
    }
}
