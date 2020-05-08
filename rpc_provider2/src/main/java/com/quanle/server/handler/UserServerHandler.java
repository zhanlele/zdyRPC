package com.quanle.server.handler;

import com.quanle.common.body.RpcRequest;

import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author quanle
 * @date 2020/4/28 9:32 PM
 */
@Component
public class UserServerHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

    private static ApplicationContext applicationContext2;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        UserServerHandler.applicationContext2 = applicationContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InvocationTargetException,
            ClassNotFoundException {
        RpcRequest rpcRequest = (RpcRequest) msg;
//        RpcRequest rpcRequest = JSON.parseObject(JSON.toJSONString(msg), RpcRequest.class);
        Object handler = handler(rpcRequest);
        ctx.writeAndFlush("success");
    }

    private Object handler(RpcRequest request) throws ClassNotFoundException, InvocationTargetException {

        //使用Class.forName进行加载Class文件
        Class<?> clazz = Class.forName(request.getClassName());
        Object serviceBean = applicationContext2.getBean(clazz);

        Class<?> serviceClass = serviceBean.getClass();

        String methodName = request.getMethodName();

        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        //使用CGLB Reflect
        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);

        return fastMethod.invoke(serviceBean, parameters);
    }

}
