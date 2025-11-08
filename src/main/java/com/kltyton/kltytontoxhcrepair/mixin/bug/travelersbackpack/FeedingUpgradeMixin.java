package com.kltyton.kltytontoxhcrepair.mixin.bug.travelersbackpack;

import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.handler.StorageAccessWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.feeding.FeedingUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.IFilter;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tictim.paraglider.contents.item.ParagliderItem;

@Mixin(FeedingUpgrade.class)
public abstract class FeedingUpgradeMixin extends UpgradeBase<FeedingUpgrade> implements IFilter, IEnable, ITickableUpgrade {

    public FeedingUpgradeMixin(UpgradeManager manager, int dataHolderSlot, Point openTabSize) {
        super(manager, dataHolderSlot, openTabSize);
    }

    @Inject(method = "tryFeedingStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void tryFeedingStack(Level level, int hungerLevel, Player player, Integer slot, ItemStack stack, ItemStackHandler backpackStorage, CallbackInfoReturnable<Boolean> cir, ItemStack mainHandItem) {
        if (mainHandItem.getItem() instanceof ParagliderItem) {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }
}
