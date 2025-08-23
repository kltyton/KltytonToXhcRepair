package com.kltyton.kltytontoxhcrepair.mixin;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BottleItem.class)
public class BottleItemMixin extends Item {
    public BottleItemMixin(Properties properties) {
        super(properties);
    }

    // 当玩家使用瓶子时的逻辑
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        EntityType<?> betterEndDragonflyType = EntityType.byString("betterend:dragonfly").orElse(null);
        EntityType<?> swampierSwampsDragonflyType = EntityType.byString("swampier_swamps:dragonfly").orElse(null);
        EntityType<?> crittersandcompanionsdragonflyType = EntityType.byString("crittersandcompanions:dragonfly").orElse(null);
        // 获取玩家周围的 AreaEffectCloud 实体列表
        List<AreaEffectCloud> list = level.getEntitiesOfClass(AreaEffectCloud.class, player.getBoundingBox().inflate(2.0), areaEffectCloud -> areaEffectCloud != null && areaEffectCloud.isAlive() && areaEffectCloud.getOwner() != null && (areaEffectCloud.getOwner().getType() == betterEndDragonflyType || areaEffectCloud.getOwner().getType() == swampierSwampsDragonflyType || areaEffectCloud.getOwner().getType() == crittersandcompanionsdragonflyType));
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!list.isEmpty()) {
            // 如果检测到有符合条件的 AreaEffectCloud 实体
            AreaEffectCloud areaEffectCloud2 = list.get(0);
            areaEffectCloud2.discard();
            // 播放 Dragon Breath 声音效果
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0f, 1.0f);
            // 触发 FLUID_PICKUP 游戏事件
            level.gameEvent(player, GameEvent.FLUID_PICKUP, player.position());
            // 将瓶子转换为 Dragon Breath 物品
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(this.onTurnBottleIntoItem(itemStack, player, new ItemStack(Items.DRAGON_BREATH)), level.isClientSide()));
        }
    }

    // 将瓶子转换为 Dragon Breath 物品，并奖励玩家统计数据
    @Unique
    protected ItemStack onTurnBottleIntoItem(ItemStack bottleStack, Player player, ItemStack filledBottleStack) {
        player.awardStat(Stats.ITEM_USED.get((BottleItem)(Object)this));
        return ItemUtils.createFilledResult(bottleStack, player, filledBottleStack);
    }
}
