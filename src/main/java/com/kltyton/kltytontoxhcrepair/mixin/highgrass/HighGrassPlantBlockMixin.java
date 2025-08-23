package com.kltyton.kltytontoxhcrepair.mixin;

import biomesoplenty.block.HighGrassPlantBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HighGrassPlantBlock.class)
public class HighGrassPlantBlockMixin {

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void modifyEntityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        // 修改减速因子
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 1.0D, 1.0D));
        ci.cancel();
    }
}

