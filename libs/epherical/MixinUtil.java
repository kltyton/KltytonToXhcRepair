package com.kltyton.kltytontoxhcrepair.util;

import com.epherical.croptopia.items.CookingUtensil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class MixinUtil {
    public static void synthesisPan(CraftingContainer craftingContainer , Player player, ItemStack resultStack) {
        // 如果 resultStack 不为空，就按原来的判断
        if (!isPan(resultStack)) {
            return;
        }

        // 执行消耗
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack inSlot = craftingContainer.getItem(i);
            if (inSlot.getItem() instanceof CookingUtensil) {
                craftingContainer.setItem(i, ItemStack.EMPTY);
            }
        }
    }
    public static boolean isPan(ItemStack stack) {
        ResourceLocation COOKING_PAN = new ResourceLocation("candlelight", "cooking_pan");
        ResourceLocation SKILLET = new ResourceLocation("farmersdelight", "skillet");
        ResourceLocation FRYING_PAN = new ResourceLocation("croptopia", "frying_pan");
        if (stack.isEmpty()) return false;
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());

        return itemKey.equals(COOKING_PAN) ||
                itemKey.equals(SKILLET) || itemKey.equals(FRYING_PAN);
    }
}
