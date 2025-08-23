package com.kltyton.kltytontoxhcrepair.mixin.bug;

import net.mehvahdjukaar.supplementaries.common.items.PresentItem;
import net.minecraft.world.item.ItemStack;
import net.nicguzzo.wands.items.MagicBagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MagicBagItem.class)
public class MagicBagItemMixin {

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private static void onSetItem(ItemStack bag, ItemStack stack, CallbackInfo ci) {
        if (!stack.isEmpty() && isBanned(stack)) {
            ci.cancel(); // 禁止设置
        }
    }

    @Inject(method = "inc", at = @At("HEAD"), cancellable = true)
    private static void onInc(ItemStack bag, int count, CallbackInfoReturnable<Boolean> cir) {
        ItemStack current = MagicBagItem.getItem(bag);
        if (!current.isEmpty() && isBanned(current)) {
            cir.setReturnValue(false); // 禁止增加
        }
    }

    @Unique
    private static boolean isBanned(ItemStack stack) {
        return stack.getItem() instanceof PresentItem;
    }
}
