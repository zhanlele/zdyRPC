package com.quanle.server.service;

import com.quanle.service.UserService;

/**
 * @author quanle
 * @date 2020/4/28 10:19 PM
 */
public class UserServiceImpl implements UserService {

    @Override
    public String sayHello(String word) {
        System.out.println("调用成功--参数 "+word);
        return "调用成功--参数 "+word;
    }
}
