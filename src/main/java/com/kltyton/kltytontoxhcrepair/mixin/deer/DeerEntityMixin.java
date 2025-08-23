package com.kltyton.kltytontoxhcrepair.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.wildernature.entity.DeerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DeerEntity.class)
public class DeerEntityMixin {

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void modifyTemptGoal(CallbackInfo info) {
        DeerEntity deer = (DeerEntity) (Object) this;
        GoalSelector goalSelector = deer.goalSelector;

        TagKey<Item> deerFoodTag = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("kltytontoxhcrepair:deer_food"));
        Ingredient deerFoodIngredient = Ingredient.of(deerFoodTag);

        goalSelector.addGoal(2, new TemptGoal(deer, 1.2D, deerFoodIngredient, false));
    }

    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void isFood(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        TagKey<Item> deerFoodTag = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("kltytontoxhcrepair:deer_food"));
        if (itemStack.is(deerFoodTag)) {
            cir.setReturnValue(true);
        }
    }
}
