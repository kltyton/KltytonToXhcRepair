package com.kltyton.kltytontoxhcrepair.mixin.bug.geckolib;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = Player.class, priority = 0)
public class ModelPartMixin {
    @Inject(method = "getItemBySlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EquipmentSlot;getType()Lnet/minecraft/world/entity/EquipmentSlot$Type;"), cancellable = true)
    public void getItemBySlot(EquipmentSlot equipmentSlot, CallbackInfoReturnable<ItemStack> cir) {
        if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
            ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor((Player) (Object) this, equipmentSlot);
            if (!cosmeticStack.isEmpty()) {
                cir.setReturnValue(cosmeticStack);
                cir.cancel();
            }
        }
    }
}
