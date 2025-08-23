package com.kltyton.kltytontoxhcrepair.mixin.jei;

import dev.ftb.mods.ftbxmodcompat.ftbquests.jei.FTBQuestsJEIIntegration;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.core.util.LoggedTimer;
import mezz.jei.library.config.EditModeConfig;
import mezz.jei.library.focus.FocusFactory;
import mezz.jei.library.ingredients.subtypes.SubtypeManager;
import mezz.jei.library.load.registration.RuntimeRegistration;
import mezz.jei.library.recipes.RecipeManager;
import mezz.jei.library.runtime.JeiHelpers;
import mezz.jei.library.runtime.JeiRuntime;
import mezz.jei.library.startup.JeiStarter;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;


@Pseudo
@Mixin(JeiStarter.class)
public class JeiStarterMixin {
    @Inject(
            method = "start",
            at = @At(
            value = "INVOKE",
                    target = "Lmezz/jei/core/util/LoggedTimer;stop()V",
                    shift = At.Shift.AFTER,
                    ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    public void start(CallbackInfo ci, Minecraft minecraft, LoggedTimer totalTime, IColorHelper colorHelper, IIngredientFilterConfig ingredientFilterConfig, SubtypeManager subtypeManager, IIngredientManager ingredientManager, FocusFactory focusFactory, Path configDir, EditModeConfig editModeConfig, JeiHelpers jeiHelpers, RecipeManager recipeManager, IRecipeTransferManager recipeTransferManager, LoggedTimer timer, IScreenHelper screenHelper, RuntimeRegistration runtimeRegistration, JeiRuntime jeiRuntime) {
        if (jeiRuntime != null) {
            FTBQuestsJEIIntegration.runtime = jeiRuntime;
        }
    }

}
