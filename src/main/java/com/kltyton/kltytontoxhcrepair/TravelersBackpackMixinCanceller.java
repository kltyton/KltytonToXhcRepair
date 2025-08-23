package com.kltyton.fabricmixintest.client;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class TravelersBackpackMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        // 取消 TravelersBackpack 的 InventoryScreenMixin
        return "com.tiviacz.travelersbackpack.mixin.InventoryScreenMixin".equals(mixinClassName);
    }
}
