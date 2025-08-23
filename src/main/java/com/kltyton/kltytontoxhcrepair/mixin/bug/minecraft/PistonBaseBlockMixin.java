package com.kltyton.kltytontoxhcrepair.mixin.bug.minecraft;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PistonBaseBlock.class, priority = 0)
public class PistonBaseBlockMixin {
    @Redirect(method = "isPushable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", ordinal = 0))
    private static boolean isPushable(BlockState instance, Block block) {
        return instance.is(block) || instance.getBlock() instanceof ShulkerBoxBlock;
    }
}
