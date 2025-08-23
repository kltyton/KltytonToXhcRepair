package com.kltyton.kltytontoxhcrepair.mixin;

import com.yuntang.registry.ModClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ModClient.class)
public abstract class ModClientMixin {

    @Inject(
            method = "onInitializeClient",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/MenuScreens;register(Lnet/minecraft/world/inventory/MenuType;Lnet/minecraft/client/gui/screens/MenuScreens$ScreenConstructor;)V", shift = At.Shift.AFTER), cancellable = true)
    private void redirectJoinRegister(CallbackInfo ci) {
        ci.cancel();
    }
}
