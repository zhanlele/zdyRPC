package com.quanle.common.server;

import com.quanle.common.body.RpcRequest;
import com.quanle.common.coder.RpcDecoder;
import com.quanle.common.config.ServiceConfig;
import com.quanle.common.seriail.impl.JSONSerializer;

import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author quanle
 * @date 2020/5/7 6:58 AM
 */
public class NettyServer {
    /**
     * 启动netty
     *
     * @param serverConfigs 注册的service接口
     * @param port          端口
     * @throws InterruptedException 异常
     */
    public void start(List<ServiceConfig> serverConfigs, int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(workerGroup, bossGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new NettyServerHandlerAdapter(serverConfigs));
                    }
                });
        serverBootstrap.bind(port).sync();
        System.out.println("启动Netty Server，端口为：" + port);
    }
}
