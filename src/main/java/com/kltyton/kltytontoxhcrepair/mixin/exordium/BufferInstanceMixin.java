package com.kltyton.kltytontoxhcrepair.mixin.exordium;

import dev.tr7zw.exordium.components.BufferInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = BufferInstance.class)
public class BufferInstanceMixin<T> {
    @Inject(method = "renderBuffer", at = @At("HEAD"), remap = false, cancellable = true)
    public void renderBuffer(Object context, GuiGraphics guiGraphics, CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getInstance().screen instanceof Screen) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
    @Inject(method = "postRender", at = @At("HEAD"), remap = false, cancellable = true)
    public void postRender(Object context, GuiGraphics guiGraphics, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof Screen) ci.cancel();
    }
    @Inject(method = "enabled", at = @At("HEAD"), remap = false, cancellable = true)
    public void enabled(CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getInstance().screen instanceof Screen) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
