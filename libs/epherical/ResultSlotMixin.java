package com.kltyton.kltytontoxhcrepair.mixin;

import com.kltyton.kltytontoxhcrepair.util.MixinUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin {

    @Final
    @Shadow
    private CraftingContainer craftSlots;

    @Inject(
            method = "onTake",
            at = @At(
                    "RETURN"
            )
    )
    private void onAfterGetRemainingItems(Player player, ItemStack resultStack, CallbackInfo ci) {
        CraftingContainer craftingContainer = this.craftSlots;
        MixinUtil.synthesisPan(craftingContainer, player, resultStack);
    }
}
