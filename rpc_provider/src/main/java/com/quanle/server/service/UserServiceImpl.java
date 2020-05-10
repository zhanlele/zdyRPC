package com.quanle.server.service;

import com.quanle.common.body.RpcRequest;
import com.quanle.common.coder.RpcDecoder;
import com.quanle.common.seriail.impl.JSONSerializer;
import com.quanle.common.service.UserService;
import com.quanle.server.handler.UserServerHandler;

import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.internal.ThreadLocalRandom;

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
