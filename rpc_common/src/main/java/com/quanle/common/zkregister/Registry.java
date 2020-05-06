package com.quanle.common.zkregister;

import com.quanle.common.config.ServiceConfig;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author quanle
 * @date 2020/5/6 10:33 PM
 */
public interface Registry {
    /**
     * 将生产者接口注册到注册中心
     *
     * @param clazz        类
     * @param registryInfo 本机的注册信息
     */
    void register(Class<?> clazz, RegistryInfo registryInfo) throws Exception;

    /**
     * 为服务提供者抓取注册表
     *
     * @param clazz 类
     * @return 服务提供者所在的机器列表
     */
    List<RegistryInfo> fetchRegistry(Class<?> clazz, Method method) throws Exception;

    void updateData(String registryUrl, Class<?> clazz, Method method, RegistryInfo registryInfo) throws Exception;

    void unregisterService(String registryUrl, List<ServiceConfig> serviceConfigs, RegistryInfo registryInfo) throws Exception;
}
