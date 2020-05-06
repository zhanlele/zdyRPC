package com.quanle.common.config;

/**
 * @author quanle
 * @date 2020/5/6 10:42 PM
 */
public class ReferenceConfig {
    private Class<?> clazz;

    public ReferenceConfig(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}
