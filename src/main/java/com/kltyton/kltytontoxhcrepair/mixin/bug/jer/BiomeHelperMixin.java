package com.kltyton.kltytontoxhcrepair.mixin.bug.jer;

import jeresources.api.util.BiomeHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(BiomeHelper.class)
public class BiomeHelperMixin {
    @Inject(method = "getAllBiomes", at = @At("HEAD"),remap = false , cancellable = true)
    private static void getAllBiomes(CallbackInfoReturnable<List<Biome>> cir) {
        List<Biome> biomes = new ArrayList<>();
        try {
            VanillaRegistries.createLookup()
                    .lookupOrThrow(Registries.BIOME)
                    .listElements()
                    .map(Holder.Reference::value)
                    .forEach(biomes::add);
        } catch (IllegalStateException ignored) {
            // Fabric-API 0.83+ 会因为 unreferenced key 抛异常，直接跳过
        }
        cir.setReturnValue(biomes);
        cir.cancel();
    }

    /* 根据 ResourceKey 拿单个群系，失败返回 null */
    @Inject(method = "getBiome", at = @At("HEAD"), cancellable = true)
    private static void getBiome(ResourceKey<Biome> key, CallbackInfoReturnable<Biome> cir) {
        try {
            cir.setReturnValue(VanillaRegistries.createLookup()
                    .lookupOrThrow(Registries.BIOME)
                    .getOrThrow(key)
                    .value());
            cir.cancel();
        } catch (IllegalStateException | NullPointerException e) {
            cir.setReturnValue(null);cir.setReturnValue(null);
            cir.cancel();
        }
    }

    @Inject(method = "getBiomes", at = @At("HEAD"), cancellable = true)
    private static void getBiomes(ResourceKey<Biome> category, CallbackInfoReturnable<List<Biome>> cir) {
        List<Biome> biomes = new ArrayList<>();
        try {
            VanillaRegistries.createLookup()
                    .lookupOrThrow(Registries.BIOME)
                    .listElements()
                    .forEach(entry -> {
                        if (entry.key().equals(category)) {
                            biomes.add(entry.value());
                        }
                    });
        } catch (IllegalStateException ignored) {
            // 同上，忽略未引用键
        }
        cir.setReturnValue(biomes);
        cir.cancel();
    }
}
