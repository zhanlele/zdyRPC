package com.quanle.service.impl;

import com.quanle.conf.ZkConfig;
import com.quanle.pojo.DataSourceProperty;
import com.quanle.service.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author quanle
 */
@Service
public class ConfigServiceImpl implements ConfigService {
    @Value("${zookeeper.url}")
    private String zookeeperUrl;

    @Override
    public DataSourceProperty getDbConfig() {
        ZkConfig zkConfig = new ZkConfig(zookeeperUrl);
        return zkConfig.getDbProperty();
    }

    @Override
    public void updateConfig(DataSourceProperty property) throws Exception {
        ZkConfig zkConfig = new ZkConfig(zookeeperUrl);
        zkConfig.updateDbProperty(property);
    }

}
