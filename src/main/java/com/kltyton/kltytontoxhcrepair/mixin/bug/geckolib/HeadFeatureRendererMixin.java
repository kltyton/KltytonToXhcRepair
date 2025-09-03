package com.kltyton.kltytontoxhcrepair.mixin.bug.geckolib;

import com.bawnorton.mixinsquared.TargetHandler;
import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.dragonloot.init.ItemInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(value = CustomHeadLayer.class, priority = 1500)
public class HeadFeatureRendererMixin {
    @TargetHandler(
            mixin = "net.dragonloot.mixin.client.HeadFeatureRendererMixin",
            name = "render"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack getItemBySlot(LivingEntity instance, EquipmentSlot equipmentSlot) {
        ItemStack itemStack = instance.getItemBySlot(equipmentSlot);
        if (!itemStack.isEmpty() && itemStack.getItem() == ItemInit.DRAGON_HELMET) {
            ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor(instance, equipmentSlot);
            if (!cosmeticStack.isEmpty() && (itemStack.isEmpty() || !itemStack.is(CosmeticArmor.ALWAYS_VISIBLE))) {
                return cosmeticStack;
            } else {
                return itemStack;
            }
        }
        return itemStack;
    }
}
