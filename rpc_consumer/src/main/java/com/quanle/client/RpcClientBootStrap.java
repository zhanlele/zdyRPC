package com.quanle.client;

import com.quanle.common.RpcContext;
import com.quanle.common.config.ReferenceConfig;
import com.quanle.common.service.UserService;

import java.util.Collections;

/**
 * @author quanle
 * @date 2020/4/28 9:25 PM
 */
public class RpcClientBootStrap {

    public static void main(String[] args) throws Exception {
        String connectionString = "192.168.3.3:2181";
        ReferenceConfig config = new ReferenceConfig(UserService.class);
        RpcContext context = new RpcContext(connectionString, null, Collections.singletonList(config),
                8991);
        UserService userService = (UserService) context.getService(UserService.class);
        System.out.println(userService.sayHello("are you ok?"));
        System.out.println(userService.sayHello("are you ok?"));


    }
}
