package com.quanle.conf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.quanle.constants.DbConstant;
import com.quanle.pojo.DataSourceProperty;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;

/**
 * @author quanle
 */
public class ZkConfig {

    private CuratorFramework client;

    public ZkConfig(String connectionString) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
    }

    public void initWatch() throws Exception {
        TreeCache watcher = new TreeCache(client, DbConstant.CONF_NODE);
        watcher.start();
        watcher.getListenable().addListener(new ConfigWatcher());
    }

    public DataSourceProperty getDbProperty() {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        try {
            Stat stat = client.checkExists().forPath(DbConstant.CONF_NODE);
            //如果zk的数据库配置节点没数据就默认初始化一些数据到节点
            if (null == stat) {
                dataSourceProperty.setDriverClassName(DbConstant.DRIVER_CLASS_NAME);
                dataSourceProperty.setUrl(DbConstant.DB_URL);
                dataSourceProperty.setUserName(DbConstant.USERNAME);
                dataSourceProperty.setPassWord(DbConstant.PASSWORD);
                //初始化数据库配置信息到zk节点
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(DbConstant.CONF_NODE, JSON.toJSONBytes(dataSourceProperty));
            } else {
                //接口被注册了,取出数据并将要注册的数据保存进去
                byte[] bytes = client.getData().forPath(DbConstant.CONF_NODE);
                String data = new String(bytes, StandardCharsets.UTF_8);
                System.out.println("zookeeper获取数据库配置信息>>" + data);
                dataSourceProperty = JSON.parseObject(data, DataSourceProperty.class);
            }
            if (null == dataSourceProperty.getDriverClassName()
                    || null == dataSourceProperty.getUrl()
                    || null == dataSourceProperty.getUserName()
                    || null == dataSourceProperty.getPassWord()) {
                dataSourceProperty.setDriverClassName(DbConstant.DRIVER_CLASS_NAME);
                dataSourceProperty.setUrl(DbConstant.DB_URL);
                dataSourceProperty.setUserName(DbConstant.USERNAME);
                dataSourceProperty.setPassWord(DbConstant.PASSWORD);
                //初始化数据库配置信息到zk节点
                client.setData().forPath(DbConstant.CONF_NODE, JSON.toJSONBytes(dataSourceProperty));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSourceProperty;
    }


    public void updateDbProperty(DataSourceProperty property) throws Exception {
        client.setData().forPath(DbConstant.CONF_NODE, JSONArray.toJSONBytes(property));

    }
}
