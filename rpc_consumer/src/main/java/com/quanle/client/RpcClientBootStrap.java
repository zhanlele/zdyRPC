package com.quanle.client;

import com.quanle.service.UserService;

/**
 * @author quanle
 * @date 2020/4/28 9:25 PM
 */
public class RpcClientBootStrap {
    public static final String providerName = "UserService#sayHello#";

    public static void main(String[] args) throws InterruptedException {
        RpcConsumer rpcConsumer = new RpcConsumer();
        UserService proxy = (UserService) rpcConsumer.createProxy(UserService.class, providerName);
        while (true) {
            Thread.sleep(2000);
            System.out.println(proxy.sayHello("are you ok?"));
        }
    }
}
