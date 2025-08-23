package com.kltyton.kltytontoxhcrepair.mixin.travelersBackpackMixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.network.ServerboundRetrieveBackpackPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Pseudo
@Mixin(value = InventoryScreen.class, priority = 900)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
    public InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(
            at = {@At("TAIL")},
            method = {"render"}
    )
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (Minecraft.getInstance().screen instanceof InventoryScreen && ComponentUtils.getComponentOptional(player).isPresent() && ComponentUtils.getComponentOptional(player).get().hasBackpack() && TravelersBackpack.enableIntegration()) {
                ItemStack backpack = ComponentUtils.getComponentOptional(player).get().getBackpack();
                context.renderItem(backpack, this.leftPos + 77 + 59, this.topPos + 62 - 18 - 36);
                if (mouseX >= this.leftPos + 77 + 59 && mouseX < this.leftPos + 77 + 59 + 16 && mouseY >= this.topPos + 62 - 18 - 36 && mouseY < this.topPos + 62 - 18 - 36 + 16) {
                    AbstractContainerScreen.renderSlotHighlight(context, this.leftPos + 77 + 59, this.topPos + 62 - 18 - 36, -1000);
                    List<Component> components = new ArrayList();
                    components.add(Component.translatable("screen.travelersbackpack.retrieve_backpack"));
                    context.renderTooltip(Minecraft.getInstance().font, components, Optional.of(new BackpackTooltipComponent(backpack)), mouseX, mouseY);
                }
            }

            if (TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory) {
                if (ComponentUtils.isWearingBackpack(player)) {
                    if (TravelersBackpack.enableIntegration()) {
                        return;
                    }

                    ItemStack backpack = ComponentUtils.getWearingBackpack(player);
                    context.renderItem(backpack, this.leftPos + 77 + 59, this.topPos + 62 - 18 - 36);
                    if (mouseX >= this.leftPos + 77 + 59 && mouseX < this.leftPos + 77 + 59 + 16 && mouseY >= this.topPos + 62 - 18 - 36 && mouseY < this.topPos + 62 - 18 - 36 + 16) {
                        EffectRenderingInventoryScreen.renderSlotHighlight(context, this.leftPos + 77 + 59, this.topPos + 62 - 18 - 36, -1000);
                        String button = KeybindHandler.OPEN_BACKPACK.getTranslatedKeyMessage().getString();
                        List<Component> components = new ArrayList();
                        components.add(Component.translatable("screen.travelersbackpack.open_inventory", button));
                        components.add(Component.translatable("screen.travelersbackpack.unequip_tip"));
                        components.add(Component.translatable("screen.travelersbackpack.hide_icon"));
                        TooltipFlag.Default tooltipflag$default = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
                        backpack.getItem().appendHoverText(backpack, player.level(), components, tooltipflag$default);
                        context.renderTooltip(Minecraft.getInstance().font, components, Optional.of(new BackpackTooltipComponent(backpack)), mouseX, mouseY);
                    }
                }

            }
        }
    }

    @Inject(
            at = {@At("TAIL")},
            method = {"mouseClicked"}
    )
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (Minecraft.getInstance().screen instanceof InventoryScreen && ComponentUtils.getComponentOptional(player).isPresent() && ComponentUtils.getComponentOptional(player).get().hasBackpack() && TravelersBackpack.enableIntegration() && mouseX >= (double)(this.leftPos + 77 + 59) && mouseX < (double)(this.leftPos + 77 + 59 + 16) && mouseY >= (double)(this.topPos + 62 - 18 - 36) && mouseY < (double)(this.topPos + 62 - 18 - 36 + 16) && button == 0) {
                PacketDistributorHelper.sendToServer(new ServerboundRetrieveBackpackPacket(ComponentUtils.getComponentOptional(player).get().getBackpack().getItem().getDefaultInstance()));
            }

            if (TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory) {
                if (ComponentUtils.isWearingBackpack(player)) {
                    if (TravelersBackpack.enableIntegration()) {
                        return;
                    }

                    if (mouseX >= (double)(this.leftPos + 77 + 59) && mouseX < (double)(this.leftPos + 77 + 59 + 16) && mouseY >= (double)(this.topPos + 62 - 18 - 36) && mouseY < (double)(this.topPos + 62 - 18 - 36 + 16) && button == 0) {
                        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340)) {
                            player.sendSystemMessage(Component.translatable("screen.travelersbackpack.hide_icon_info"));
                        } else {
                            ServerboundActionTagPacket.create(1);
                        }
                    }
                }

            }
        }
    }
}

