package com.quanle.common.server;

import com.quanle.common.server.service.UserServiceImpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author quanle
 * @date 2020/4/29 12:04 AM
 */
@SpringBootApplication
public class RpcServerBootstrap {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(RpcServerBootstrap.class,args);
        UserServiceImpl.startServer("127.0.0.1",8991);
    }
}
