package com.kltyton.kltytontoxhcrepair.mixin.bug.minecraft;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Pseudo
@Mixin(TagLoader.class)
public class TagLoaderMixin {
    @Inject(method = "load", at = @At("RETURN"))
    private void kltytonToXhcRepair$stripVisceralHeap(ResourceManager resourceManager,
                                           CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {

        Map<ResourceLocation, List<TagLoader.EntryWithSource>> tags = cir.getReturnValue();
        final ResourceLocation visceralHeap = new ResourceLocation("biomesoplenty", "visceral_heap");

        // 遍历所有 biome 标签
        for (List<TagLoader.EntryWithSource> entries : tags.values()) {
            entries.removeIf(entryWithSource -> {
                TagEntry entry = entryWithSource.entry();
                return !entry.tag && entry.id.equals(visceralHeap);
            });
        }
    }
}
