package com.kltyton.kltytontoxhcrepair.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Zoglin;
import net.satisfy.wildernature.entity.BoarEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    private void onThunderHit(ServerLevel level, LightningBolt lightning, CallbackInfo ci) {
        if ((Object) this instanceof BoarEntity) {
            if (level.getDifficulty() != Difficulty.PEACEFUL) {
                Zoglin zoglin = EntityType.ZOGLIN.create(level);
                if (zoglin != null) {
                    BoarEntity boar = (BoarEntity) (Object) this;
                    zoglin.moveTo(boar.getX(), boar.getY(), boar.getZ(), boar.getYRot(), boar.getXRot());
                    zoglin.setNoAi(boar.isNoAi());
                    zoglin.setBaby(boar.isBaby());
                    if (boar.hasCustomName()) {
                        zoglin.setCustomName(boar.getCustomName());
                        zoglin.setCustomNameVisible(boar.isCustomNameVisible());
                    }
                    zoglin.setPersistenceRequired();
                    level.addFreshEntity(zoglin);
                    boar.discard();
                    ci.cancel();
                }
            }
        }
    }
}

