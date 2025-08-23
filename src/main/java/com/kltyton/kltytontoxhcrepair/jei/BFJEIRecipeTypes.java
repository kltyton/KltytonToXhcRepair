package com.kltyton.kltytontoxhcrepair.jei;

import mezz.jei.api.recipe.RecipeType;
import net.hecco.bountifulfares.recipe.MillingRecipe;

// net.hecco.bountifulfares.compat.jei.BFJEIRecipeTypes
public class BFJEIRecipeTypes {
    public static final RecipeType<MillingRecipe> MILLING =
            RecipeType.create("bountifulfares", "milling", MillingRecipe.class);
}

