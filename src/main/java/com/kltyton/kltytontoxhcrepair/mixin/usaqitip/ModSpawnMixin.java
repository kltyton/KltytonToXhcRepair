package com.kltyton.kltytontoxhcrepair.mixin;

import com.yuntang.registry.ModSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModSpawn.class)
public class ModSpawnMixin {
    @Inject(method = "registerEntitySpawns", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/biome/v1/BiomeSelectors;includeByKey([Lnet/minecraft/resources/ResourceKey;)Ljava/util/function/Predicate;", ordinal = 7, shift = At.Shift.AFTER), cancellable = true)
    private static void registerEntitySpawns(CallbackInfo ci) {
        ci.cancel();
    }
}
