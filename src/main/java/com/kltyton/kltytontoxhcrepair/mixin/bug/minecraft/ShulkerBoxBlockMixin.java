package com.kltyton.kltytontoxhcrepair.mixin.bug.minecraft;

import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = BlockBehaviour.BlockStateBase.class, priority = 0)
public abstract class ShulkerBoxBlockMixin {
    @Shadow
    public abstract Block getBlock();

    @Mutable
    @Shadow
    @Final
    private PushReaction pushReaction;

    @Inject(method = "getPistonPushReaction", at = @At("RETURN"), cancellable = true)
    private void onGetPistonPushReaction(CallbackInfoReturnable<PushReaction> cir) {
        if (this.pushReaction == PushReaction.DESTROY) {
            Block block = this.getBlock();
            if (block instanceof EntityBlock) cir.setReturnValue(PushReaction.BLOCK);
        }
    }
}
