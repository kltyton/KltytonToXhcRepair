package com.kltyton.kltytontoxhcrepair.mixin.farmland;

import com.kltyton.kltytontoxhcrepair.Kltytontoxhcrepair;
import net.minecraft.world.level.block.state.BlockState;;
import net.satisfy.farm_and_charm.core.block.crops.TomatoCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TomatoCropBlock.class)
public class TomatoCropBlockMixin {
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    private void injectMayPlaceOn(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.is(Kltytontoxhcrepair.FARMLAND)) {
            cir.setReturnValue(true);
        }
    }
}
