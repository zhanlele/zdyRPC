package com.quanle.common.server.handler;

/**
 * @author quanle
 * @date 2020/5/7 8:24 AM
 */

import com.quanle.common.RpcContext;
import com.quanle.common.config.ServiceConfig;
import com.quanle.common.utils.ClassUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

@Component
public class InitRpc {
    @Value("${rpc.port}")
    private int port;

    @Value("${zookeeper.url}")
    private String zookeeperUrl;

    @PostConstruct
    public void init() throws Exception {
        List<Class<?>> clazzs = ClassUtils.getClassByAnnotation(Service.class, "com.quanle");
        if (!clazzs.isEmpty()) {
            List<ServiceConfig> serviceConfigList = new ArrayList<>(clazzs.size());
            for (Class<?> clazz : clazzs) {
                Object obj = clazz.getDeclaredConstructor().newInstance();
                ServiceConfig serviceConfig = new ServiceConfig(clazz.getInterfaces()[0], obj);
                serviceConfigList.add(serviceConfig);
            }
            new RpcContext(zookeeperUrl, serviceConfigList, null, port);
        }
    }
}
