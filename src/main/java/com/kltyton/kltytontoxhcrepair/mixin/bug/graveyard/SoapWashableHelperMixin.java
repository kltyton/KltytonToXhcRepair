package com.kltyton.kltytontoxhcrepair.mixin.bug.graveyard;

import net.mehvahdjukaar.supplementaries.common.utils.SoapWashableHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoapWashableHelper.class)
public class SoapWashableHelperMixin {
    @Inject(method = "tryChangingColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    private static void setBlock(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Container container) {
            NonNullList<ItemStack> drops = NonNullList.create();
            for (int i = 0; i < container.getContainerSize(); i++) {
                drops.add(container.getItem(i));
                container.setItem(i, ItemStack.EMPTY);
            }
        }
    }
}
