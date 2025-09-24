package com.kltyton.kltytontoxhcrepair.mixin.bug.bakery;

import com.chefmooon.ubesdelight.common.item.RollingPinItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.bakery.core.block.cake.BlankCakeBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Pseudo
@Mixin(BlankCakeBlock.class)
public class BlankCakeBlockMixin {
    @Shadow
    @Final
    public static BooleanProperty CUPCAKE;

    @Shadow
    @Final
    public static BooleanProperty COOKIE;

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Ldev/architectury/registry/registries/RegistrySupplier;get()Ljava/lang/Object;", ordinal = 49),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir, ItemStack itemStack, Item item, boolean isCake, boolean isCupcake) {
        if (isCupcake && item instanceof RollingPinItem) {
            world.setBlock(pos, state.setValue(CUPCAKE, false).setValue(COOKIE, true), 3);
            world.levelEvent(2001, pos, Block.getId(state));
            world.playSound(null, pos, SoundEvents.GENERIC_BIG_FALL, SoundSource.BLOCKS, 1.0F, 1.0F);
            cir.setReturnValue(InteractionResult.sidedSuccess(false));
            cir.cancel();
        }
    }

}