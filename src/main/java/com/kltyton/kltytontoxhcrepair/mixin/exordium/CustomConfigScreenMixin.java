package com.kltyton.fabricmixintest.mixin;

import dev.tr7zw.exordium.config.CustomConfigScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(value = CustomConfigScreen.class, priority = 0)
public class CustomConfigScreenMixin {
    @ModifyVariable(method = "getIntOption", at = @At("HEAD"), ordinal = 1, argsOnly = true, remap = false)
    public int getIntOption(int value) {
        return 512;
    }
}
