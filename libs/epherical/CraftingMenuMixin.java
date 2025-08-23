package com.kltyton.kltytontoxhcrepair.mixin;

import com.kltyton.kltytontoxhcrepair.util.MixinUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {

    @Inject(
            method = "quickMoveStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/CraftingMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0
            )
    )
    private void onShiftTakeResult(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        // 只有目标槽是 0（结果槽）时才处理
        if (index != 0) return;

        CraftingMenu self = (CraftingMenu) (Object) this;
        Slot resultSlot = self.getSlot(0);
        if (!resultSlot.hasItem()) return;

        ItemStack resultStack = resultSlot.getItem().copy();
        MixinUtil.synthesisPan(self.craftSlots, player, resultStack);
    }
}