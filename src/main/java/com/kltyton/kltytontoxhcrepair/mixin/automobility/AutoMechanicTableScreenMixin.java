package com.kltyton.kltytontoxhcrepair.mixin;

import io.github.foundationgames.automobility.screen.AutoMechanicTableScreen;
import io.github.foundationgames.automobility.screen.AutoMechanicTableScreenHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayDeque;
import java.util.List;

@Mixin(AutoMechanicTableScreen.class)
public abstract class AutoMechanicTableScreenMixin extends AbstractContainerScreen<AutoMechanicTableScreenHandler> {

    @Shadow
    private long time;

    @Shadow
    protected void drawMissingIngredient(GuiGraphics graphics, Ingredient ing, int x, int y) {
    }
    public AutoMechanicTableScreenMixin(AutoMechanicTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }
    @Inject(method = "drawMissingIngredients", at = @At("HEAD"))
    private void onDrawMissingIngredients(GuiGraphics graphics, CallbackInfo ci) {

        SimpleContainer inputInv = this.menu.inputInv;
        ArrayDeque<Ingredient> missingIngs = new ArrayDeque<>(this.menu.missingIngredients);

        double mouseX = 0;
        double mouseY = 0;
        if (this.minecraft != null) {
            double scaleFactor = this.minecraft.getWindow().getGuiScale();
            mouseX = this.minecraft.mouseHandler.xpos() / scaleFactor;
            mouseY = this.minecraft.mouseHandler.ypos() / scaleFactor;
        }

        for (int i = 0; i < inputInv.getContainerSize(); ++i) {
            if (!missingIngs.isEmpty()) {
                int x = this.leftPos + 8 + i * 18;
                int y = this.topPos + 88;

                if (inputInv.getItem(i).isEmpty()) {
                    Ingredient ing = missingIngs.removeFirst();
                    this.drawMissingIngredient(graphics, ing, x, y);

                    if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                        ItemStack stack = ing.getItems()[Mth.floor((float)this.time / 30.0F) % ing.getItems().length];
                        graphics.renderTooltip(this.font, getTooltipFromContainerItem(stack), stack.getTooltipImage(), (int) mouseX, (int) mouseY);
                    }
                }
            }
        }
    }

    public @NotNull List<Component> getTooltipFromContainerItem(@NotNull ItemStack itemStack) {
        if (this.minecraft != null) {
            TooltipFlag flag = this.minecraft.options.advancedItemTooltips
                    ? TooltipFlag.Default.ADVANCED
                    : TooltipFlag.Default.NORMAL;
            return itemStack.getTooltipLines(this.minecraft.player, flag);
        }
        return List.of();
    }
}
