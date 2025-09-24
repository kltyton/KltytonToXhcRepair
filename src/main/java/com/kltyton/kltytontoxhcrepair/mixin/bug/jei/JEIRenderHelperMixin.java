package com.kltyton.kltytontoxhcrepair.mixin.bug.jei;


import com.mojang.datafixers.util.Either;
import mezz.jei.fabric.platform.RenderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.appleskin.client.TooltipOverlayHandler;

import java.util.List;

@Mixin(RenderHelper.class)
public class JEIRenderHelperMixin {
    @Inject(at = @At("HEAD"), method = "renderTooltip", require = 0)
    private void renderFoodPre(GuiGraphics guiGraphics, List<Either<FormattedText, TooltipComponent>> elements, int x, int y, Font font, ItemStack stack, CallbackInfo ci) {
        for (int i = 0; i < elements.size(); i++) {
            var element = elements.get(i);
            var maybeLeft = element.left();
            if (maybeLeft.isEmpty()) continue;
            var left = maybeLeft.get();
            if (left instanceof TooltipOverlayHandler.FoodOverlayTextComponent) {
                var tooltipData = ((TooltipOverlayHandler.FoodOverlayTextComponent) left).foodOverlay;
                elements.set(i, Either.right(tooltipData));
            }
        }
    }
}
