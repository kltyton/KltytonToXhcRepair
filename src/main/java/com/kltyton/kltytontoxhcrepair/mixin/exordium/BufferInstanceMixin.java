package com.kltyton.fabricmixintest.mixin;

import dev.tr7zw.exordium.components.BufferInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = BufferInstance.class)
public class BufferInstanceMixin {
    @Inject(method = "renderBuffer", at = @At("HEAD"), remap = false, cancellable = true)
    public void renderBuffer(Object context, DrawContext guiGraphics, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().currentScreen instanceof AbstractInventoryScreen) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
    @Inject(method = "postRender", at = @At("HEAD"), remap = false, cancellable = true)
    public void postRender(Object context, DrawContext guiGraphics, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen instanceof AbstractInventoryScreen) ci.cancel();
    }
    @Inject(method = "enabled", at = @At("HEAD"), remap = false, cancellable = true)
    public void enabled(CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().currentScreen instanceof AbstractInventoryScreen) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
