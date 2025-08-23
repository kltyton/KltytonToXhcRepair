package com.kltyton.kltytontoxhcrepair.mixin.bug.owo;

import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsItemGroups;
import com.github.mim1q.minecells.registry.MineCellsItems;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MineCellsItemGroups.class)
public abstract class MineCellsItemGroupsMixin {
    @Mutable
    @Shadow
    @Final
    public static OwoItemGroup MINECELLS;

    @Mutable
    @Shadow
    @Final
    public static ItemGroupTab GENERAL_TAB;

    @Mutable
    @Shadow
    @Final
    public static ItemGroupTab COMBAT_TAB;

    @Mutable
    @Shadow
    @Final
    public static ItemGroupTab SPAWN_EGGS_TAB;

    @Shadow
    public static ItemGroupButton linkButton(Icon icon, String name, String url) {return null;}

    @Shadow
    @Final
    private static ResourceLocation DISCORD_ICON;

    @Shadow
    @Final
    private static ResourceLocation KOFI_ICON;

    @Shadow
    @Final
    private static ResourceLocation BACKGROUND;

    @Shadow
    private static Component getTabTitle(String componentName) {return null;}


    @Shadow
    @Final
    private static ResourceLocation TABS;

    @Shadow
    private static ItemStack stack(ItemLike item) {return null;}

    @Inject(method = "generalStacks", at = @At("TAIL"))
    private static void onGeneralStacks(CreativeModeTab.ItemDisplayParameters ctx, CreativeModeTab.Output stacks, CallbackInfo ci) {
        stacks.acceptAll(List.of(stack(MineCellsItems.ASSASSINS_DAGGER), stack(MineCellsItems.BLOOD_SWORD), stack(MineCellsItems.BROADSWORD), stack(MineCellsItems.BALANCED_BLADE), stack(MineCellsItems.CROWBAR), stack(MineCellsItems.NUTCRACKER), stack(MineCellsItems.CURSED_SWORD), stack(MineCellsItems.HATTORIS_KATANA), stack(MineCellsItems.TENTACLE), stack(MineCellsItems.FROST_BLAST), stack(MineCellsItems.SPITE_SWORD), stack(MineCellsItems.FLINT), stack(MineCellsItems.PHASER)));
        stacks.acceptAll(MineCellsItems.BOWS.stream().map(MineCellsItemGroupsMixin::stack).toList());
        stacks.accept(stack(MineCellsItems.ICE_ARROW));
        stacks.acceptAll(MineCellsItems.CROSSBOWS.stream().map(MineCellsItemGroupsMixin::stack).toList());
        stacks.accept(stack(MineCellsItems.EXPLOSIVE_BOLT));
        stacks.acceptAll(MineCellsItems.OTHER_RANGED.stream().map(MineCellsItemGroupsMixin::stack).toList());
        stacks.acceptAll(MineCellsItems.SHIELDS.stream().map(MineCellsItemGroupsMixin::stack).toList());
        stacks.acceptAll(MineCellsEntities.getSpawnEggStacks());
    }
}
