package com.quanle.common.client;

import com.alibaba.fastjson.JSON;
import com.quanle.common.body.RpcRequest;
import com.quanle.common.coder.RpcEncoder;
import com.quanle.common.seriail.impl.JSONSerializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author quanle
 * @date 2020/4/28 9:26 PM
 */
public class RpcConsumer {

    //创建线程池对象
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static UserClientHandler userClientHandler;

    //1.创建一个代理对象 providerName：UserService#sayHello are you ok?
    public Object createProxy(final Class<?> serviceClass) {
        //借助JDK动态代理生成代理对象
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] {serviceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //（1）调用初始化netty客户端的方法
                        if (userClientHandler == null) {
                            initClient();
                        }
                        //封装
                        RpcRequest request = new RpcRequest();
                        String requestId = UUID.randomUUID().toString();
                        System.out.println(requestId);

                        String className = method.getDeclaringClass().getName();
                        String methodName = method.getName();

                        Class<?>[] parameterTypes = method.getParameterTypes();

                        request.setRequestId(requestId);
                        request.setClassName(className);
                        request.setMethodName(methodName);
                        request.setParameterTypes(parameterTypes);
                        request.setParameters(args);

//                        userClientHandler.setPara(JSON.toJSONString(request));
                        userClientHandler.setPara(request);
                        System.out.println("-------->setPara成功" + JSON.toJSONString(request));
                        // 去服务端请求数据
                        return executor.submit(userClientHandler).get();
                    }
                });


    }


    //2.初始化netty客户端
    public static void initClient() throws InterruptedException {
        userClientHandler = new UserClientHandler();

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(userClientHandler);
                    }
                });

        bootstrap.connect("127.0.0.1", 8991).sync();

    }
}
