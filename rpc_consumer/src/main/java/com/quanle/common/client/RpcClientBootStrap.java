package com.quanle.common.client;

import com.quanle.common.service.UserService;

/**
 * @author quanle
 * @date 2020/4/28 9:25 PM
 */
public class RpcClientBootStrap {
//    public static final String providerName = "UserService#sayHello#";

    public static void main(String[] args) throws InterruptedException {
        RpcConsumer rpcConsumer = new RpcConsumer();
        UserService proxy = (UserService) rpcConsumer.createProxy(UserService.class);
        while (true) {
            System.out.println("============>>>>>");
            Thread.sleep(2000);
            proxy.sayHello("are you ok?");
            System.out.println("已响应");
        }
    }
}
