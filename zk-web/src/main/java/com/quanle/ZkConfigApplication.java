package com.quanle;

import com.quanle.conf.ZkConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZkConfigApplication implements ApplicationRunner {
    @Value("${zookeeper.url}")
    private String zookeeperUrl;

    public static void main(String[] args) {
        SpringApplication.run(ZkConfigApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new ZkConfig(zookeeperUrl).initWatch();
    }
}
