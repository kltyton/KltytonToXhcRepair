package com.kltyton.kltytontoxhcrepair.mixin.bug.supplementaries;

import net.mehvahdjukaar.supplementaries.common.block.blocks.SackBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SackBlock.class)
public class SackBlockMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/mehvahdjukaar/supplementaries/common/block/blocks/SackBlock;canFall(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/LevelAccessor;)Z"))
    public boolean canFall(BlockPos pos, LevelAccessor world) {
        return false;
    }
}
