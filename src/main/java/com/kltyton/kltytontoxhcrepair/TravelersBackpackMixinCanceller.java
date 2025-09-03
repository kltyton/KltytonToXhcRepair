package com.kltyton.kltytontoxhcrepair;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;
import java.util.Set;

public class TravelersBackpackMixinCanceller implements MixinCanceller {
    // 要禁用的 mixin 全类名集合
    private static final Set<String> CANCEL_LIST = Set.of(
            "com.tiviacz.travelersbackpack.mixin.InventoryScreenMixin",
            "software.bernie.geckolib.mixins.fabric.MixinHumanoidArmorLayer"
    );
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return CANCEL_LIST.contains(mixinClassName);
    }
}
