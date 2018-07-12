package com.dyl.gw.support.task;

import com.diyiliu.plugin.cache.ICache;
import com.diyiliu.plugin.task.ITask;

/**
 * Description: PetInfoTask
 * Author: DIYILIU
 * Update: 2018-07-12 10:00
 */

public class PetInfoTask implements ITask {

    private ICache petCacheProvider;

    @Override
    public void execute() {

    }

    public void setPetCacheProvider(ICache petCacheProvider) {
        this.petCacheProvider = petCacheProvider;
    }
}
