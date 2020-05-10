package com.quanle.service;


import com.quanle.pojo.DataSourceProperty;

public interface ConfigService {

    DataSourceProperty getDbConfig();

    void updateConfig(DataSourceProperty property) throws Exception;
}
