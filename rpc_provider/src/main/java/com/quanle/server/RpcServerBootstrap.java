package com.quanle.server;

import com.quanle.common.config.ServiceConfig;
import com.quanle.common.utils.ClassUtils;
import com.quanle.common.zkregister.RegistryInfo;
import com.quanle.common.zkregister.ZookeeperRegistry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author quanle
 * @date 2020/4/29 12:04 AM
 */
@SpringBootApplication
public class RpcServerBootstrap implements CommandLineRunner {

    /*public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(RpcServerBootstrap.class,args);
        UserServiceImpl.startServer("192.168.3.3",8991);
    }*/


    @Value("${rpc.port}")
    private int port;

    @Value("${zookeeper.url}")
    private String registryUrl;

    public static void main(String[] args) {
        SpringApplication.run(RpcServerBootstrap.class, args);
//        UserServiceImpl.start(registryUrl, port);
    }

    @Override
    public void run(String... args) {
        //服务器关闭后，Zookeeper注册列表会自动剔除下线的服务端节点，客户端与下线的服务端断开连接
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            List<Class<?>> clazzs = ClassUtils.getClassByAnnotation(Service.class, "com.quanle");
            List<ServiceConfig> serviceConfigList = new ArrayList<>(clazzs.size());
            if (!clazzs.isEmpty()) {
                for (Class<?> clazz : clazzs) {
                    Object obj = null;
                    try {
                        obj = clazz.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    ServiceConfig serviceConfig = new ServiceConfig(clazz.getInterfaces()[0], obj);
                    serviceConfigList.add(serviceConfig);
                }
            }
            InetAddress addr;
            try {
                addr = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            String hostname = addr.getHostName();
            String hostAddress = addr.getHostAddress();
            ZookeeperRegistry zookeeperRegistry = new ZookeeperRegistry();
            RegistryInfo registryInfo = new RegistryInfo();
            registryInfo.setHostname(hostname);
            registryInfo.setIp(hostAddress);
            registryInfo.setPort(port);
            try {
                zookeeperRegistry.unregisterService(registryUrl, serviceConfigList, registryInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
