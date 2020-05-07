package com.quanle.common.balance.impl;

import com.quanle.common.balance.LoadBalance;
import com.quanle.common.zkregister.RegistryInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author quanle
 * @date 2020/5/4 11:55 AM
 */
public class LatestLoadBalance implements LoadBalance {

    @Override
    public RegistryInfo choose(List<RegistryInfo> registryInfos) {
        Collections.shuffle(registryInfos);
        registryInfos.sort(Comparator.comparing(RegistryInfo::getExpireTime));
        return registryInfos.get(0);
    }
}
