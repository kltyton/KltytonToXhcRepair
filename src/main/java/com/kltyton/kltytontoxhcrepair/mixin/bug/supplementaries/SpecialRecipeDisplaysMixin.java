package com.kltyton.kltytontoxhcrepair.mixin.bug.supplementaries;

import net.mehvahdjukaar.supplementaries.common.items.crafting.SpecialRecipeDisplays;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(SpecialRecipeDisplays.class)
public class SpecialRecipeDisplaysMixin {
    @Inject(method = "createBubbleBlowerChargeRecipe", at = @At("HEAD"), cancellable = true, remap = false)
    private static void createBubbleBlowerChargeRecipe(CallbackInfoReturnable<List<CraftingRecipe>> cir) {
        cir.setReturnValue(null);
    }
}
