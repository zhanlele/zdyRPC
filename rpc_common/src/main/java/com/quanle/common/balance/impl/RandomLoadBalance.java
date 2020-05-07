package com.quanle.common.balance.impl;

import com.quanle.common.balance.LoadBalance;
import com.quanle.common.zkregister.RegistryInfo;

import java.util.List;
import java.util.Random;

/**
 * @author quanle
 * @date 2020/5/4 11:54 AM
 */
public class RandomLoadBalance implements LoadBalance {

    @Override
    public RegistryInfo choose(List<RegistryInfo> registryInfos) {
        Random random = new Random();
        int index = random.nextInt(registryInfos.size());
        return registryInfos.get(index);
    }
}
