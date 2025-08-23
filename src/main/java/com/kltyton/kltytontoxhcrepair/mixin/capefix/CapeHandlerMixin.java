package com.kltyton.kltytontoxhcrepair.mixin.capefix;

import nl.enjarai.cicada.util.CapeHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = CapeHandler.class, priority = 0)
public class CapeHandlerMixin {
    @Inject(method = "loadCape", at = @At("HEAD"), remap = false, cancellable = true)
    private static void loadCape(CallbackInfo ci) {
        ci.cancel();
    }
}
