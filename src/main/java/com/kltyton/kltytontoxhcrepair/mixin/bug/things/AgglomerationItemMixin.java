package com.kltyton.kltytontoxhcrepair.mixin.bug.things;

import artifacts.item.wearable.WearableArtifactItem;
import com.glisco.things.items.trinkets.AgglomerationItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AgglomerationItem.class)
public abstract class AgglomerationItemMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE",
            target = "Ldev/emi/trinkets/api/Trinket;tick(Lnet/minecraft/world/item/ItemStack;Ldev/emi/trinkets/api/SlotReference;Lnet/minecraft/world/entity/LivingEntity;)V"))
    public void tick(Trinket instance, ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (stack.getItem() instanceof WearableArtifactItem art) {
            art.wornTick(entity, stack);
        }
        instance.tick(stack, slot, entity);
    }
    @Redirect(method = "onUnequip", at = @At(value = "INVOKE",
            target = "Ldev/emi/trinkets/api/Trinket;onUnequip(Lnet/minecraft/world/item/ItemStack;Ldev/emi/trinkets/api/SlotReference;Lnet/minecraft/world/entity/LivingEntity;)V"))
    public void onUnequip(Trinket instance, ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (stack.getItem() instanceof WearableArtifactItem art) {
            art.onUnequip(entity, stack);
        }
        instance.onUnequip(stack, slot, entity);
    }
    @Redirect(method = "onEquip", at = @At(value = "INVOKE",
            target = "Ldev/emi/trinkets/api/Trinket;onEquip(Lnet/minecraft/world/item/ItemStack;Ldev/emi/trinkets/api/SlotReference;Lnet/minecraft/world/entity/LivingEntity;)V"))
    public void onEquip(Trinket instance, ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (stack.getItem() instanceof WearableArtifactItem art) {
            art.onEquip(entity, stack);
        }
        instance.onEquip(stack, slot, entity);
    }
}
