package com.quanle.common;

import com.quanle.common.balance.impl.LatestLoadBalance;
import com.quanle.common.body.RpcRequest;
import com.quanle.common.config.ReferenceConfig;
import com.quanle.common.config.ServiceConfig;
import com.quanle.common.server.NettyClient;
import com.quanle.common.server.NettyClientHandlerAdapter;
import com.quanle.common.server.NettyServer;
import com.quanle.common.service.UserService;
import com.quanle.common.utils.ReflectionUtils;
import com.quanle.common.zkregister.Registry;
import com.quanle.common.zkregister.RegistryInfo;
import com.quanle.common.zkregister.ZookeeperRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author quanle
 * @date 2020/5/7 7:11 AM
 */
public class RpcContext {

    /**
     * 每个service接口的实例
     */
    private List<ServiceConfig> serviceConfigs;

    /**
     * 消费者需要消费的接口
     */
    private List<ReferenceConfig> referenceConfigs;

    /**
     * 注册中心
     */
    private Registry registry;

    /**
     * 接口方法对应method对象
     */
    private Map<String, Method> serviceMap = new ConcurrentHashMap<>();


    private String registryUrl;

    //创建线程池对象
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public RpcContext(
            String registryUrl, List<ServiceConfig> serviceConfigs,
            List<ReferenceConfig> referenceConfigs, int port) throws Exception {
        //保存服务提供者和消费者
        this.serviceConfigs = serviceConfigs == null ? new ArrayList<>() : serviceConfigs;
        this.referenceConfigs = referenceConfigs == null ? new ArrayList<>() : referenceConfigs;
        this.registryUrl = registryUrl;
        //实例化注册中心
        registry = new ZookeeperRegistry(registryUrl);
        InetAddress addr = InetAddress.getLocalHost();
        String hostname = addr.getHostName();
        String hostAddress = addr.getHostAddress();
        RegistryInfo registryInfo = new RegistryInfo();
        registryInfo.setHostname(hostname);
        registryInfo.setIp(hostAddress);
        registryInfo.setPort(port);
        registryInfo.setExpireTime(0L);
        registryInfo.setLastTime(0L);
        //将接口注册到注册中心
        doRegistry(registryInfo);
        //启动Netty服务器
        if (!this.serviceConfigs.isEmpty()) {
            new NettyServer().start(this.serviceConfigs, port);
        }
    }


    private void doRegistry(RegistryInfo registryInfo) throws Exception {
        for (ServiceConfig config : serviceConfigs) {
            Class<?> clazz = config.getClazz();
            registry.register(clazz, registryInfo);
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                String key = ReflectionUtils.buildKeyWithClassAndMethod(clazz, method);
                serviceMap.put(key, method);
            }
        }
    }


    /**
     * 获取服务
     *
     * @param clazz 类名
     * @return 服务类
     */
    public Object getService(Class<?> clazz) {
        String registryUrl = this.registryUrl;
        //借助JDK动态代理生成代理对象
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] {clazz},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        List<RegistryInfo> registryInfos = registry.fetchRegistry(clazz, method);
                        if (null == registryInfos || registryInfos.isEmpty()) {
                            throw new RuntimeException("没有服务提供者");
                        }
                        LatestLoadBalance loadbalancer = new LatestLoadBalance();
                        RegistryInfo registryInfo = loadbalancer.choose(registryInfos);
                        NettyClientHandlerAdapter handlerAdapter = new NettyClientHandlerAdapter();
                        new NettyClient().connect(registryInfo.getIp(), registryInfo.getPort(), handlerAdapter);
                        RpcRequest rpcRequest = new RpcRequest();
                        rpcRequest.setClassName(UserService.class.getName());
                        rpcRequest.setMethodName(method.getName());
                        rpcRequest.setRequestId(UUID.randomUUID().toString());
                        rpcRequest.setParameters(args);
                        rpcRequest.setRegistryUrl(registryUrl);
                        rpcRequest.setPort(registryInfo.getPort());
                        Class<?>[] parameterTypes = new Class[] {};
                        if (null != args || args.length != 0) {
                            parameterTypes = new Class[args.length];
                            for (int i = 0; i < args.length; i++) {
                                parameterTypes[i] = args[i].getClass();
                            }
                        }
                        rpcRequest.setParameterTypes(parameterTypes);
                        handlerAdapter.setPara(rpcRequest);
                        return executor.submit(handlerAdapter).get();
                    }
                });
    }
}
