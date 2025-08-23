package com.kltyton.kltytontoxhcrepair.mixin.farmland;

import com.kltyton.kltytontoxhcrepair.Kltytontoxhcrepair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.BuddingBushBlock;

@Pseudo
@Mixin(BuddingBushBlock.class)
public class BuddingBushBlockMixin {
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    private void injectMayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.is(Kltytontoxhcrepair.FARMLAND)) {
            cir.setReturnValue(true);
        }
    }
}
