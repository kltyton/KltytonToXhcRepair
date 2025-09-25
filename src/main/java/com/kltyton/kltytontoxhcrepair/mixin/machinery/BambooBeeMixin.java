package com.kltyton.kltytontoxhcrepair.mixin.machinery;

import immersive_machinery.Common;
import immersive_machinery.entity.BambooBee;
import immersive_machinery.entity.MachineEntity;
import immersive_machinery.entity.NavigatingMachine;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BambooBee.class)
public abstract class BambooBeeMixin extends NavigatingMachine {
    @Shadow
    protected abstract boolean match(ItemStack stack, ItemStack other);

    @Shadow(remap = false)
    private BambooBee.Configuration configuration;

    @Shadow(remap = false)
    protected abstract boolean isEmpty(List<ItemStack> slots);

    public BambooBeeMixin(EntityType<? extends MachineEntity> entityType, Level world, boolean canExplodeOnCrash, boolean isFlying, int pathAccuracy) {
        super(entityType, world, canExplodeOnCrash, isFlying, pathAccuracy);
    }
    /**
     * @author Kltyton
     * @reason 修改过滤逻辑(启用黑名单)
     */
    @Overwrite
    private boolean filter(ItemStack stack) {
        List<ItemStack> slots = this.getSlots(Common.SLOT_FILTER);
        if (this.isEmpty(slots)) {
            // 空过滤槽 → 默认允许
            return true;
        }

        boolean match = slots.stream().anyMatch((other) -> this.match(stack, other));

        if (this.configuration.blacklist) {
            // 黑名单模式：匹配的被禁止
            return !match;
        } else {
            // 白名单模式：只有匹配的允许
            return match;
        }
    }

}


