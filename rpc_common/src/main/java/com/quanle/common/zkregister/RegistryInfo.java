package com.quanle.common.zkregister;

import java.util.Objects;

/**
 * @author quanle
 * @date 2020/5/6 10:30 PM
 */
public class RegistryInfo {

    private String hostname;
    private String ip;
    private Integer port;
    /**
     * 响应时间
     */
    private Long expireTime;
    /**
     * 上一次响应时间撮
     */
    private Long lastTime;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Long getExpireTime() {
        return null == expireTime ? 0L : expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Long getLastTime() {
        return null == lastTime ? 0L : lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        RegistryInfo that = (RegistryInfo) o;
        return Objects.equals(hostname, that.hostname) &&
                Objects.equals(ip, that.ip) &&
                Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, ip, port);
    }
}
