package com.hhrpc.hhrpc.demo.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.demo.api.User;
import com.hhrpc.hhrpc.demo.api.UserService;
import org.springframework.stereotype.Service;

@HhRpcProvider
@Service
public class UserServiceImpl implements UserService {

    @Override
    public User findById(Integer uid) {
        return new User(uid, "hh-" + System.currentTimeMillis());
    }

    @Override
    public User findById(Integer uid, String name) {
        return new User(uid, name);
    }

    @Override
    public long findLongId(long uid) {
        return uid;
    }

    @Override
    public User findUser(User user) {
        return user;
    }
}
