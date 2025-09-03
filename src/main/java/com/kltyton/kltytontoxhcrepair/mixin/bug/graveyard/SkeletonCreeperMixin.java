package com.kltyton.kltytontoxhcrepair.mixin.bug.graveyard;

import com.google.common.collect.Sets;
import com.lion.graveyard.entities.SkeletonCreeper;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.creeperfirework.Configuration;
import plus.dragons.creeperfirework.FireworkEffect;
import plus.dragons.creeperfirework.mixin.ExplosionMethodInvoker;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Pseudo
@Mixin(SkeletonCreeper.class)
public abstract class SkeletonCreeperMixin extends Creeper {
    @Shadow(remap = false)
    @Final
    private double explosionRadius;

    public SkeletonCreeperMixin(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true, remap = false)
    public void explode(CallbackInfo ci) {
        if (!this.getCommandSenderWorld().isClientSide &&
                Configuration.isCreeperExplodeIntoFirework() &&
                Math.random() < Configuration.becomeFireworkChance()) {

            // 生成烟花
            FireworkEffect.create(this);
            if (Configuration.isFireworkHurtCreature()) {
                simulateExplodeHurtMob();
            }
            if (Configuration.isFireworkDestroyBlock() && this.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                simulateExplodeDestroyBlock();
            }
            this.discard();
        }
        ci.cancel();
    }
    @Unique
    private void simulateExplodeDestroyBlock() {
        this.level().gameEvent(this, GameEvent.EXPLODE, this.blockPosition());
        Set<BlockPos> explosionRange = Sets.newHashSet();
        BlockPos groundZero = this.blockPosition();

        for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
                for(int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d = (float)j / 15.0F * 2.0F - 1.0F;
                        double e = (float)k / 15.0F * 2.0F - 1.0F;
                        double f = (float)l / 15.0F * 2.0F - 1.0F;
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        float h = this.getExplosionPower() * (0.7F + this.level().random.nextFloat() * 0.6F);
                        double m = groundZero.getX();
                        double n = groundZero.getY();

                        for(double o = groundZero.getZ(); h > 0.0F; h -= 0.22500001F) {
                            BlockPos blockPos = new BlockPos((int)m, (int)n, (int)o);
                            BlockState blockState = this.level().getBlockState(blockPos);
                            FluidState fluidState = this.level().getFluidState(blockPos);
                            if (!this.level().isInWorldBounds(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = blockState.isAir() && fluidState.isEmpty() ? Optional.empty() : Optional.of(Math.max(blockState.getBlock().getExplosionResistance(), fluidState.getExplosionResistance()));
                            if (optional.isPresent()) {
                                h -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (h > 0.0F) {
                                explosionRange.add(blockPos);
                            }

                            m += d * (double)0.3F;
                            n += e * (double)0.3F;
                            o += f * (double)0.3F;
                        }
                    }
                }
            }
        }

        ObjectArrayList blockDropList = new ObjectArrayList();
        Explosion simulateExplosionForParameter = new Explosion(this.level(), null, null, null, this.getBlockX(), this.getBlockY(), this.getBlockZ(), this.getExplosionPower(), false, Explosion.BlockInteraction.DESTROY);

        for(BlockPos affectedPos : explosionRange) {
            BlockState blockStateOfAffected = this.level().getBlockState(affectedPos);
            Block block = blockStateOfAffected.getBlock();
            if (!blockStateOfAffected.isAir()) {
                BlockPos blockPos2 = affectedPos.immutable();
                this.level().getProfiler().push("explosion_blocks");
                BlockEntity blockEntity = blockStateOfAffected.hasBlockEntity() ? this.level().getBlockEntity(affectedPos) : null;
                LootParams.Builder builder = (new LootParams.Builder((ServerLevel)this.level())).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(affectedPos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.EXPLOSION_RADIUS, this.getExplosionPower());
                blockStateOfAffected.getDrops(builder).forEach((stack) -> ExplosionMethodInvoker.invokeTryMergeStack(blockDropList, stack, blockPos2));
                this.level().setBlock(affectedPos, Blocks.AIR.defaultBlockState(), 3);
                block.wasExploded(this.level(), affectedPos, simulateExplosionForParameter);
                this.level().getProfiler().pop();
            }
        }

        for (Object o : blockDropList) {
            Pair<ItemStack, BlockPos> itemStackBlockPosPair = (Pair) o;
            Block.popResource(this.level(), itemStackBlockPosPair.getSecond(), itemStackBlockPosPair.getFirst());
        }

    }
    @Unique
    private float getExplosionPower() {
        return (float)(this.getEntityData().get(DATA_IS_POWERED) ? explosionRadius * 2 : explosionRadius);
    }
    @Unique
    private void simulateExplodeHurtMob() {
        Vec3 groundZero = this.position();
        AABB box = (new AABB(this.blockPosition())).inflate(this.getExplosionPower());
        List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, box);
        victims.remove(this);

        for(LivingEntity victim : victims) {
            if (!victim.ignoreExplosion()) {
                float j = this.getExplosionPower() * 2.0F;
                double h = Math.sqrt(victim.distanceToSqr(groundZero)) / (double)j;
                if (h <= (double)1.0F) {
                    double s = victim.getX() - groundZero.x;
                    double t = victim.getEyeY() - groundZero.y;
                    double u = victim.getZ() - groundZero.z;
                    double blockPos = Math.sqrt(s * s + t * t + u * u);
                    if (blockPos != (double)0.0F) {
                        s /= blockPos;
                        t /= blockPos;
                        u /= blockPos;
                        double fluidState = Explosion.getSeenPercent(groundZero, victim);
                        double v = ((double)1.0F - h) * fluidState;
                        victim.hurt(this.damageSources().explosion(this, null), (float)((int)((v * v + v) / (double)2.0F * (double)7.0F * (double)j + (double)1.0F)));
                        double w = ProtectionEnchantment.getExplosionKnockbackAfterDampener(victim, v);
                        victim.setDeltaMovement(victim.getDeltaMovement().add(s * w, t * w, u * w));
                    }
                }
            }
        }

    }

}
