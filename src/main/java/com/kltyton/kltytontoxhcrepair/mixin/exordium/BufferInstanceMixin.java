package com.kltyton.kltytontoxhcrepair.mixin.exordium;

import com.kltyton.kltytontoxhcrepair.Kltytontoxhcrepair;
import com.kltyton.kltytontoxhcrepair.util.IBufferedComponent;
import dev.tr7zw.exordium.components.BufferComponent;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.render.BufferedComponent;
import dev.tr7zw.exordium.util.NMSHelper;
import dev.tr7zw.exordium.versionless.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Pseudo
@Mixin(value = BufferInstance.class)
public class BufferInstanceMixin<T> {
    @Mutable
    @Shadow
    @Final
    private BufferedComponent buffer;

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
    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void exo$applyCrosshairBlendStart(ResourceLocation id, BufferComponent<T> component, Supplier<Config.ComponentSettings> settings, CallbackInfo ci) {
        if (id.equals(NMSHelper.getResourceLocation("minecraft", "crosshair"))) {
            ((IBufferedComponent) this.buffer).setCrosshair(true);
        }
    }
}
