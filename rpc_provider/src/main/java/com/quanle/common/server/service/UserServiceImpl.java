package com.quanle.common.server.service;

import com.quanle.common.body.RpcRequest;
import com.quanle.common.coder.RpcDecoder;
import com.quanle.common.config.ServiceConfig;
import com.quanle.common.seriail.impl.JSONSerializer;
import com.quanle.common.server.handler.UserServerHandler;
import com.quanle.common.service.UserService;
import com.quanle.common.utils.ClassUtils;
import com.quanle.common.zkregister.RegistryInfo;
import com.quanle.common.zkregister.ZookeeperRegistry;

import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author quanle
 * @date 2020/4/28 10:19 PM
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public String sayHello(String word) {
        long time = ThreadLocalRandom.current().nextLong(1000, 3000);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("调用成功--参数 " + word);
        return "调用成功--参数 " + word;
    }


    public static void start(String registryUrl, Integer port) {
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


    public static void startServer(String hostName, int port) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new UserServerHandler());
                    }
                });
        serverBootstrap.bind(hostName, port).sync();
        System.out.println("服务启动");
    }
}
