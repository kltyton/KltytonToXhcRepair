package com.kltyton.kltytontoxhcrepair;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Kltytontoxhcrepair implements ModInitializer {
    public static final TagKey<Block> FARMLAND = BlockTags.create(String.valueOf(new ResourceLocation("farm_and_charm:farmland")));
    public static final Logger LOGGER = LogManager.getLogger("KltytonToXhcRepair");
/*    public static final MenuType<KltytonCruncherInventoryMenu> CRUNCHER_MENU_TYPE = Registry.register(
            BuiltInRegistries.MENU,
            new ResourceLocation("species", "cruncher"),
            new ExtendedScreenHandlerType<>(KltytonCruncherInventoryMenu::create)
    );*/

    @Override
    public void onInitialize() {
        // 注册资源包
        ResourceManagerHelper.registerBuiltinResourcePack(
                new ResourceLocation("kltytontoxhcrepair", "fufus_override_pack"),
                FabricLoader.getInstance().getModContainer("kltytontoxhcrepair").orElseThrow(),
                Component.literal("fufus_override_pack"),
                ResourcePackActivationType.ALWAYS_ENABLED // 永远启用
        );

    }
}
