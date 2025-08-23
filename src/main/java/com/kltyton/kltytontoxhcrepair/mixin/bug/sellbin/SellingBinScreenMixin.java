package com.kltyton.kltytontoxhcrepair.mixin.bug.sellbin;

import bigchadguys.sellingbin.screen.SellingBinScreen;
import bigchadguys.sellingbin.screen.handler.SellingBinScreenHandler;
import bigchadguys.sellingbin.world.data.SellingBinData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(SellingBinScreen.class)
public abstract class SellingBinScreenMixin extends AbstractContainerScreen<SellingBinScreenHandler> {
    public SellingBinScreenMixin(SellingBinScreenHandler abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
    @Inject(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V", shift = At.Shift.AFTER), cancellable = true)
    private void sellingbin$renderLabels(GuiGraphics context, int mouseX, int mouseY,
                                         CallbackInfo ci) {
        this.getMenu().getBlockEntity().ifPresent((bin) -> {
            long var10000;
            switch (bin.getMode()) {
                case BLOCK_BOUND -> var10000 = bin.getTimeLeft();
                case PLAYER_BOUND -> var10000 = (long) SellingBinData.CLIENT_TIME_LEFT.get(bin.getMaterial().getId());
                default -> throw new IncompatibleClassChangeError();
            }

            long timeLeft = var10000;
            if (timeLeft < 0L) {
                timeLeft = 0L;
            }

            long seconds = timeLeft / 20L % 60L;
            long minutes = timeLeft / 1200L % 60L;
            long hours = timeLeft / 72000L;
            String text = "";
            if (hours > 0L) {
                text = text + hours + ":";
            }

            text = text + (minutes <= 9L ? "0" : "") + minutes + ":";
            text = text + (seconds <= 9L ? "0" : "") + seconds;
            context.drawString(this.font, text, this.titleLabelX - 16 + this.imageWidth - this.font.width(text), this.titleLabelY - 20, 0xFFFFFF, false);
        });
        ci.cancel();
    }
}
