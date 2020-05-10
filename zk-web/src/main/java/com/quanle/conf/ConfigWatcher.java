package com.quanle.conf;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.quanle.pojo.DataSourceProperty;
import com.quanle.util.ApplicationContextUtils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import java.nio.charset.StandardCharsets;

import javax.sql.DataSource;

/**
 * @author quanle
 */
public class ConfigWatcher implements TreeCacheListener {

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        TreeCacheEvent.Type eventType = treeCacheEvent.getType();
        if (eventType == TreeCacheEvent.Type.NODE_UPDATED) {
            System.out.println("节点数据变更，更新数据");
            refreshBean(treeCacheEvent);
        }
    }

    private void refreshBean(TreeCacheEvent treeCacheEvent) {
        String data = new String(treeCacheEvent.getData().getData(), StandardCharsets.UTF_8);
        System.out.println("变更后的数据>>" + data);
        DataSourceProperty dataSourceProperty = JSON.parseObject(data, DataSourceProperty.class);
        DruidDataSource dataSource = (DruidDataSource) ApplicationContextUtils.getBean(DataSource.class);
        try {
            dataSource.restart();
            dataSource.setDriverClassName(dataSourceProperty.getDriverClassName());
            dataSource.setUrl(dataSourceProperty.getUrl());
            dataSource.setUsername(dataSourceProperty.getUserName());
            dataSource.setPassword(dataSourceProperty.getPassWord());
            dataSource.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
