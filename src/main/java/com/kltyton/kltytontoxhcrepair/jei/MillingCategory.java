package com.kltyton.kltytontoxhcrepair.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.hecco.bountifulfares.block.BFBlocks;
import net.hecco.bountifulfares.recipe.MillingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MillingCategory implements IRecipeCategory<MillingRecipe> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("farm_and_charm","textures/gui/mincer.png");
    private final IDrawable background;
    private final IDrawable icon;

    public MillingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(BFBlocks.GRISTMILL.asItem())
        );
    }

    @Override public @NotNull RecipeType<MillingRecipe> getRecipeType() { return BFJEIRecipeTypes.MILLING; }
    @Override public @NotNull Component getTitle() { return Component.translatable("jei.bountifulfares.milling"); }
    @Override @SuppressWarnings("removal") public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder b, MillingRecipe r, IFocusGroup f) {
        b.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.INPUT, 50, 35)
                .addIngredients(r.getIngredients().get(0));
        b.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT, 110, 35)
                .addItemStack(r.getResultItem(null));
    }
}

