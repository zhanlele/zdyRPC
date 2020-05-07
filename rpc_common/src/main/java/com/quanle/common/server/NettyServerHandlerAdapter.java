package com.quanle.common.server;

import com.quanle.common.body.RpcRequest;
import com.quanle.common.config.ServiceConfig;
import com.quanle.common.zkregister.RegistryInfo;
import com.quanle.common.zkregister.ZookeeperRegistry;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author quanle
 * @date 2020/5/7 7:00 AM
 */
public class NettyServerHandlerAdapter extends ChannelInboundHandlerAdapter {

    /**
     * 接口对应的实现类
     */
    private Map<Class<?>, Object> interfaceMap = new ConcurrentHashMap<>();


    public NettyServerHandlerAdapter(List<ServiceConfig> serverConfigs) {
        for (ServiceConfig config : serverConfigs) {
            interfaceMap.put(config.getClazz(), config.getInstance());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest rpcRequest = (RpcRequest) msg;
        Object result = null;
        Exception exception = null;
        Class<?> clazz = null;
        Method method = null;
        long startTime = System.currentTimeMillis();
        try {
            String className = rpcRequest.getClassName();
            String methodName = rpcRequest.getMethodName();
            clazz = Class.forName(className);
            Object obj = interfaceMap.get(clazz);
            method = clazz.getMethod(methodName, rpcRequest.getParameterTypes());
            result = method.invoke(obj, rpcRequest.getParameters());
        } catch (Exception e) {
            exception = e;
            throw new RuntimeException(e);
        } finally {
            ctx.writeAndFlush(result);
            long endTime = System.currentTimeMillis();
            long expireTime = endTime - startTime;
            InetAddress addr = InetAddress.getLocalHost();
            String hostname = addr.getHostName();
            String hostAddress = addr.getHostAddress();
            ZookeeperRegistry zookeeperRegistry = new ZookeeperRegistry();
            RegistryInfo registryInfo = new RegistryInfo();
            registryInfo.setHostname(hostname);
            registryInfo.setIp(hostAddress);
            registryInfo.setPort(rpcRequest.getPort());
            registryInfo.setExpireTime(expireTime);
            registryInfo.setLastTime(endTime);
            zookeeperRegistry.updateData(rpcRequest.getRegistryUrl(), clazz, method, registryInfo);
        }
    }
}
