package com.kltyton.kltytontoxhcrepair.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Mob mob = (Mob) (Object) this; // 将 this 转换为 Mob 实例
        // 获取 DragonflyEntity 的 EntityType
        EntityType<?> dragonflyType = EntityType.byString("betterend:dragonfly").orElse(null);
        EntityType<?> swampDragonflyType = EntityType.byString("swampier_swamps:dragonfly").orElse(null);
        EntityType<?> crittersandcompanionsdragonflyType = EntityType.byString("crittersandcompanions:dragonfly").orElse(null);
        // 检查实体的类型是否为 DragonflyEntity
        if (mob.getType() == dragonflyType || mob.getType() == swampDragonflyType || mob.getType() == crittersandcompanionsdragonflyType) {
            ItemStack stack = player.getItemInHand(hand);

            if (stack.is(Items.CHORUS_FRUIT)) {
                mob.playSound(SoundEvents.GENERIC_EAT);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                if (!mob.level().isClientSide) {
                    AreaEffectCloud areaEffectCloud = new AreaEffectCloud(mob.level(), mob.getX(), mob.getY(), mob.getZ());
                    areaEffectCloud.setOwner(mob);
                    areaEffectCloud.setParticle(ParticleTypes.DRAGON_BREATH);
                    areaEffectCloud.setRadius(0.5f);
                    areaEffectCloud.setDuration(200);
                    areaEffectCloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
                    areaEffectCloud.setPos(mob.getX(), mob.getY(), mob.getZ());
                    mob.level().addFreshEntity(areaEffectCloud);
                }

                cir.setReturnValue(InteractionResult.sidedSuccess(mob.level().isClientSide));
            }
        }
    }
}