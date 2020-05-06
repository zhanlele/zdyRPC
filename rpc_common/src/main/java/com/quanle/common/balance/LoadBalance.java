package com.quanle.common.balance;

import com.quanle.common.zkregister.RegistryInfo;

import java.util.List;

/**
 * @author quanle
 * @date 2020/5/4 11:54 AM
 */
public interface LoadBalance {
    RegistryInfo choose(List<RegistryInfo> registryInfos);
}
