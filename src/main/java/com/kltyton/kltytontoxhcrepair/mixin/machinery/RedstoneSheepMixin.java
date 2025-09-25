package com.kltyton.kltytontoxhcrepair.mixin.machinery;


import com.kltyton.kltytontoxhcrepair.Kltytontoxhcrepair;
import immersive_machinery.config.Config;
import immersive_machinery.entity.MachineEntity;
import immersive_machinery.entity.NavigatingMachine;
import immersive_machinery.entity.RedstoneSheep;
import it.crystalnest.harvest_with_ease.api.HarvestUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.NoSuchElementException;
import java.util.Optional;

@Pseudo
@Mixin(RedstoneSheep.class)
public abstract class RedstoneSheepMixin extends NavigatingMachine {
    @Shadow(remap = false)
    private int reloadingTicks;

    @Shadow(remap = false)
    private int rescanningTicks;


    public RedstoneSheepMixin(EntityType<? extends MachineEntity> entityType, Level world, boolean canExplodeOnCrash, boolean isFlying, int pathAccuracy) {
        super(entityType, world, canExplodeOnCrash, isFlying, pathAccuracy);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.rescanningTicks == 100) this.rescanningTicks = 10;
        if (this.reloadingTicks == 60) this.reloadingTicks = 20;
        if (!this.level().isClientSide) {
            // 浮在水面上的逻辑
            if (this.isInWater()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                if (this.getY() < this.getY() + 0.1) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.15, 0.0));
                }
            }
        }
    }
    @Inject(method = "work", at = @At(value = "INVOKE",
            target = "Limmersive_machinery/entity/RedstoneSheep;getAgeProperty(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"
    ), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public <T extends Comparable<T>> void onWork(BlockPos pos, CallbackInfo ci, BlockState state, ServerLevel serverLevel, Level var4) {
        ci.cancel();
        resetCropAge(serverLevel, pos, state);
        this.consumeFuel((float) Config.getInstance().fuelTicksPerHarvest);
        serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, state), pos.getX(), pos.getY(), pos.getZ(), 10, 0.5F, 0.0F, 0.5F, 1.0F);
        serverLevel.playSound(null, pos, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.BLOCKS, 1.0F, 1.2F);
    }
    @Unique
    private static void resetCropAge(ServerLevel level, BlockPos pos, BlockState state) {
        try {
            IntegerProperty age = HarvestUtils.getAge(state);
            Block crop = state.getBlock();

            // 特殊处理 PitcherCrop
            if (crop == Blocks.PITCHER_CROP) {
                // 如果你不需要“返还种子”逻辑，直接销毁即可
                level.destroyBlock(pos, false);
                return;
            }

            // 普通作物：age = 0
            level.setBlockAndUpdate(pos, state.setValue(age, 0));

            // 双高作物：砍掉上面
            if (state.is(BlockTags.CROPS) && level.getBlockState(pos.above()).is(crop)) {
                level.destroyBlock(pos.above(), false);
            }
        } catch (NoSuchElementException | ClassCastException | NullPointerException ignored) {
            // 不是带 age 的作物，忽略或 destroy
            level.destroyBlock(pos, false);
        }
    }
    @Inject(method = "getAgeProperty", at = @At("HEAD"), cancellable = true)
    private static void getAgeProperty(BlockState state, CallbackInfoReturnable<Optional<Property<Integer>>> cir) {
        cir.cancel();
        cir.setReturnValue(Optional.of(HarvestUtils.getAge(state)));
    }
    @Inject(method = "isCrop", at = @At("HEAD"), cancellable = true)
    private static void isCrop(Block block, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        cir.setReturnValue(HarvestUtils.isCrop(block) || block == Blocks.MELON || block == Blocks.PUMPKIN);
    }
    @Inject(method = "isMature", at = @At("HEAD"), cancellable = true)
    private static void isMature(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        if (state.getBlock() == Blocks.MELON || state.getBlock() == Blocks.PUMPKIN) {
            Kltytontoxhcrepair.LOGGER.info("isMature");
            cir.setReturnValue(true);
        } else cir.setReturnValue(HarvestUtils.isMature(state));
    }
}
