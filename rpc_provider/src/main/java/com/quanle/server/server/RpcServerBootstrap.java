package com.quanle.server.server;

import com.quanle.body.RpcRequest;
import com.quanle.coder.RpcEncoder;
import com.quanle.seriail.impl.JSONSerializer;
import com.quanle.server.handler.UserServerHandler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author quanle
 * @date 2020/4/29 12:04 AM
 */
@Component
public class RpcServerBootstrap implements InitializingBean, ApplicationContextAware {

    @Value("${rpc.server.hostName}")
    private String hostName;

    @Value("${rpc.server.port}")
    private int port;

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        startServer(hostName, port);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    //hostName:ip地址  port:端口号
    public void startServer(String hostName, int port) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new UserServerHandler(applicationContext));
                    }
                });
        serverBootstrap.bind(hostName, port).sync();


    }
}
