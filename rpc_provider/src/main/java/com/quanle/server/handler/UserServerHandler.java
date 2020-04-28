package com.quanle.server.handler;

import com.alibaba.fastjson.JSON;
import com.quanle.body.RpcRequest;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author quanle
 * @date 2020/4/28 9:32 PM
 */
public class UserServerHandler extends ChannelInboundHandlerAdapter {


    private ApplicationContext applicationContext;

    public UserServerHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RpcRequest rpcRequest = JSON.parseObject(JSON.toJSONString(msg), RpcRequest.class);
        try {
            String className = rpcRequest.getClassName();
            String methodName = rpcRequest.getMethodName();
            Class<?> clazz = Class.forName(className);
            Object obj = applicationContext.getBean(clazz);
            Method method = clazz.getMethod(methodName, rpcRequest.getParameterTypes());
            Object result = method.invoke(obj, rpcRequest.getParameters());
            ctx.writeAndFlush(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
