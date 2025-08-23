package com.kltyton.kltytontoxhcrepair.mixin.bug;

import net.mehvahdjukaar.supplementaries.common.items.PresentItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.nicguzzo.wands.menues.MagicBagMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(MagicBagMenu.class)
public class MagicBagMenuMixin {

    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void onClicked(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo ci) {
        MagicBagMenu self = (MagicBagMenu) (Object) this;

        // 玩家手上物品放入魔法袋槽位（36）
        if (slotIndex == 36 && actionType == ClickType.PICKUP) {
            ItemStack carried = player.containerMenu.getCarried();
            if (isBanned(carried)) {
                ci.cancel();
            }
        }

        // Shift+点击背包物品快速放入魔法袋
        if (actionType == ClickType.QUICK_MOVE && slotIndex < 36) {
            ItemStack sourceStack = self.getSlot(slotIndex).getItem();
            if (isBanned(sourceStack)) {
                ci.cancel();
            }
        }
    }

    @Unique
    private boolean isBanned(ItemStack stack) {
        return stack.getItem() instanceof PresentItem;
    }
}
