package com.kltyton.kltytontoxhcrepair.mixin.bug.bopbiomes;

import biomesoplenty.init.ModConfig;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiome;
import org.betterx.bclib.api.v2.levelgen.biomes.InternalBiomeAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InternalBiomeAPI.class)
public class InternalBiomeAPIMixin {
    @Inject(method = "registerBuiltinBiome", at = @At("HEAD"),remap = false, cancellable = true)
    private static void onInit(BCLBiome bclbiome, CallbackInfoReturnable<BCLBiome> cir) {
        ResourceKey<Biome> key = bclbiome.getBiomeKey();
        if (key != null && key.location().getNamespace().equals("biomesoplenty")) {
            if (!ModConfig.isBiomeEnabled(key)) {
                cir.setReturnValue(null);
                cir.cancel();
            }
        }
    }
}
