package com.quanle.common.zkregister;

import com.alibaba.fastjson.JSONArray;
import com.quanle.common.config.ServiceConfig;
import com.quanle.common.utils.ReflectionUtils;
import com.quanle.common.utils.StringUtils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quanle
 * @date 2020/5/6 10:35 PM
 */
public class ZookeeperRegistry implements Registry {

    private CuratorFramework client;

    private static final String PATH = "/zdyRpc";

    public ZookeeperRegistry() {

    }

    public ZookeeperRegistry(String connectString) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        try {
            Stat stat = client.checkExists().forPath(PATH);
            //初始化client
            if (null == stat) {
                client.create().creatingParentsIfNeeded().forPath(PATH);
            }
            //强制关闭，清除节点
            Runtime.getRuntime().addShutdownHook(new Thread(() -> client.close()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(Class<?> clazz, RegistryInfo registryInfo) throws Exception {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            //类名+方法名+参数确定接口的唯一标识
            String key = ReflectionUtils.buildKeyWithClassAndMethod(clazz, declaredMethod);
            Integer port = registryInfo.getPort();
            String ip = registryInfo.getIp();
            Map<String, String> map = new LinkedHashMap<>();
            map.put("ip", ip);
            map.put("port", port.toString());
            String hostKey = StringUtils.map2String(map);
            String path = PATH + "/" + hostKey + "/" + key;
            Stat stat = client.checkExists().forPath(path);
            List<RegistryInfo> registryInfos;
            if (null == stat) {
                //接口没有注册，创建节点
                registryInfos = new ArrayList<>();
                registryInfos.add(registryInfo);
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(path, JSONArray.toJSONBytes(registryInfos));
            } else {
                //接口被注册了,取出数据并将要注册的数据保存进去
                byte[] bytes = client.getData().forPath(path);
                String data = new String(bytes, StandardCharsets.UTF_8);
                registryInfos = JSONArray.parseArray(data, RegistryInfo.class);
                if (!registryInfos.contains(registryInfo)) {
                    registryInfos.add(registryInfo);
                    client.setData().forPath(path, JSONArray.toJSONBytes(registryInfos));
                }
            }
        }

    }

    @Override
    public List<RegistryInfo> fetchRegistry(Class<?> clazz, Method method) throws Exception {
        String key = ReflectionUtils.buildKeyWithClassAndMethod(clazz, method);
        String path = PATH + "/" + key;
        Stat stat = client.checkExists().forPath(path);
        if (null == stat) {
            return new ArrayList<>();
        }
        byte[] bytes = client.getData().forPath(path);
        String data = new String(bytes, StandardCharsets.UTF_8);
        return JSONArray.parseArray(data, RegistryInfo.class);
    }

    @Override
    public void updateData(String registryUrl, Class<?> clazz, Method method, RegistryInfo registryInfo) throws
            Exception {
        //类名+方法名+参数确定接口的唯一标识
        String key = ReflectionUtils.buildKeyWithClassAndMethod(clazz, method);
        String path = PATH + "/" + key;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(registryUrl)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        Stat stat = client.checkExists().forPath(path);
        if (null != stat) {
            //接口被注册了,取出数据并将要注册的数据保存进去
            byte[] bytes = client.getData().forPath(path);
            String data = new String(bytes, StandardCharsets.UTF_8);
            List<RegistryInfo> registryInfos = JSONArray.parseArray(data, RegistryInfo.class);
            List<RegistryInfo> updateInfos = new ArrayList<>();
            for (RegistryInfo info : registryInfos) {
                //Zookeeper记录每个服务端的最后一次响应时间，有效时间为5秒，5s内如果该服务端没有新的请求，响应时间清零或失效
                if (null != info.getLastTime() && 0L != info.getLastTime()) {
                    if (System.currentTimeMillis() - info.getLastTime() > 5000L) {
                        info.setExpireTime(0L);
                        info.setLastTime(0L);
                    }
                }
                if (info.getHostname().equals(registryInfo.getHostname())
                        && info.getIp().equals(registryInfo.getIp())
                        && info.getPort().equals(registryInfo.getPort())) {
                    info.setExpireTime(registryInfo.getExpireTime());
                    info.setLastTime(registryInfo.getExpireTime());
                }
                updateInfos.add(info);
            }
            client.setData().forPath(path, JSONArray.toJSONBytes(updateInfos));
        }

    }

    @Override
    public void unregisterService(
            String registryUrl,
            List<ServiceConfig> serviceConfigs,
            RegistryInfo registryInfo) throws Exception {
        System.out.println("服务器关闭，剔除下线的服务端节点，节点ip：" + registryInfo.getIp() + ",节点端口：" + registryInfo.getPort());
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(registryUrl)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        for (ServiceConfig serviceConfig : serviceConfigs) {
            Class<?> clazz = serviceConfig.getClazz();
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                //类名+方法名+参数确定接口的唯一标识
                String key = ReflectionUtils.buildKeyWithClassAndMethod(clazz, method);
                String path = PATH + "/" + key;
                Stat stat = client.checkExists().forPath(path);
                if (null != stat) {
                    //接口被注册了,取出数据并将要注册的数据保存进去
                    byte[] bytes = client.getData().forPath(path);
                    String data = new String(bytes, StandardCharsets.UTF_8);
                    List<RegistryInfo> registryInfos = JSONArray.parseArray(data, RegistryInfo.class);
                    List<RegistryInfo> updateInfos = new ArrayList<>();
                    for (RegistryInfo info : registryInfos) {
                        if (info.getHostname().equals(registryInfo.getHostname())
                                && info.getIp().equals(registryInfo.getIp())
                                && info.getPort().equals(registryInfo.getPort())) {
                            continue;
                        }
                        updateInfos.add(info);
                    }
                    client.setData().forPath(path, JSONArray.toJSONBytes(updateInfos));
                }
            }
        }
    }
}
