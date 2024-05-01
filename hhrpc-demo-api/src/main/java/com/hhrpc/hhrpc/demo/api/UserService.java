package com.hhrpc.hhrpc.demo.api;

public interface UserService {

    User findById(Integer uid);

    User findById(Integer uid, String name);

    long findLongId(long uid);

    User findUser(User user);
}
