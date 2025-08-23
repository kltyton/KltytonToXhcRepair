package com.kltyton.kltytontoxhcrepair.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.hecco.bountifulfares.block.BFBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class BFJEIPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation("bountifulfares", "jei_plugin");

    @Override public @NotNull ResourceLocation getPluginUid() { return UID; }

    @Override
    public void registerCategories(IRecipeCategoryRegistration r) {
        r.addRecipeCategories(new MillingCategory(r.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration r) {
        net.minecraft.world.item.crafting.RecipeManager rm = getClientRecipeManager();
        var list = rm.getAllRecipesFor(net.hecco.bountifulfares.recipe.MillingRecipe.Type.INSTANCE);
        r.addRecipes(BFJEIRecipeTypes.MILLING, list);
        // （可选）打点看数量
        com.mojang.logging.LogUtils.getLogger().info("[JEI] 加载 {} milling recipes", list.size());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration r) {
        r.addRecipeCatalyst(new ItemStack(BFBlocks.GRISTMILL.asItem()), BFJEIRecipeTypes.MILLING);
    }

    private static net.minecraft.world.item.crafting.RecipeManager getClientRecipeManager() {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level != null) return mc.level.getRecipeManager();
        // 保险：在打开世界前，客户端连接里也有 RecipeManager（JEI 某些时机会用到）
        if (mc.getConnection() != null) return mc.getConnection().getRecipeManager();
        throw new IllegalStateException("尚无可用的客户端 RecipeManager");
    }
}

