package com.quanle.conf;

import com.alibaba.druid.pool.DruidDataSource;
import com.quanle.pojo.DataSourceProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author quanle
 */
@Configuration
public class DataSourceConfiguration {
    @Value("${zookeeper.url}")
    private String zookeeperUrl;

    @Bean(name = "myDataSource")
    public DataSource dataSource() {
        ZkConfig zkConfig = new ZkConfig(zookeeperUrl);
        //读取节点配置信息
        DataSourceProperty dataSourceProperty = zkConfig.getDbProperty();
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(dataSourceProperty.getDriverClassName());
        dataSource.setUrl(dataSourceProperty.getUrl());
        dataSource.setUsername(dataSourceProperty.getUserName());
        dataSource.setPassword(dataSourceProperty.getPassWord());
        System.out.println("数据库连接池初始化完成>>>>>>>>>>>>>>>>>>>>>>");
        return dataSource;
    }
}
