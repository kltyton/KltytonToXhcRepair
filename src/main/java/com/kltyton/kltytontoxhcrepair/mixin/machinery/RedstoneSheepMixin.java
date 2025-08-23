package com.kltyton.kltytontoxhcrepair.mixin.machinery;

import com.kltyton.kltytontoxhcrepair.util.ConfigExt;
import immersive_machinery.config.Config;
import immersive_machinery.entity.MachineEntity;
import immersive_machinery.entity.NavigatingMachine;
import immersive_machinery.entity.RedstoneSheep;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.CabinetBlock;
import vectorwing.farmersdelight.common.block.RichSoilFarmlandBlock;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Pseudo
@Mixin(RedstoneSheep.class)
public abstract class RedstoneSheepMixin extends NavigatingMachine {
    @Unique
    private boolean isDirtBlock(Block block) {
        // 涵盖原版常见泥土类方块，可根据需要扩展
        return !(block instanceof RichSoilFarmlandBlock) && !(block instanceof CabinetBlock);
    }
    /**
     * 判断给定的方块是不是两格高作物的一部分。
     * 只检查上下一格即可，绝大多数两格高作物都满足。
     */
    @Unique
    private boolean isDoubleCrop(BlockPos pos) {
        return isCropPart(pos) && (isCropPart(pos.above()) || isCropPart(pos.below()));
    }

    /**
     * 判断给定坐标是不是“作物”——用你已经写好的 isCrop 逻辑即可。
     */
    @Unique
    private boolean isCropPart(BlockPos pos) {
        return isCrop((level().getBlockState(pos).getBlock()));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!this.level().isClientSide) {
            // 浮在水面上的逻辑
            if (this.isInWater()) {
                // 取消下沉速度，模拟浮力
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                // 如果实体下半身在水里，强制上浮
                if (this.getY() < this.getY() + 0.1) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.15, 0.0));
                }
            }
        }
    }
    @Shadow
    public static native Optional<Property<Integer>> getAgeProperty(BlockState state);


    @Shadow
    public static boolean isCrop(Block block) {
        return false;
    }

    public RedstoneSheepMixin(EntityType<? extends MachineEntity> entityType, Level world, boolean canExplodeOnCrash, boolean isFlying, int pathAccuracy) {
        super(entityType, world, canExplodeOnCrash, isFlying, pathAccuracy);
    }

    /**
     * 把原来 work 方法中“收尾工作”抽出来，避免重复代码。
     */
    @Unique
    private void doPostWork(BlockPos pos) {
        consumeFuel(Config.getInstance().fuelTicksPerHarvest);

        if (level() instanceof ServerLevel server) {
            server.sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST,
                            level().getBlockState(pos)),
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    10, 0.5, 0.5, 0.5, 1.0);

            SoundEvent sound = getHarvestSound(
                    BuiltInRegistries.BLOCK.getKey(level().getBlockState(pos).getBlock()).toString());
            server.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.2f);
        }
    }
    /**
     * 对单个方块执行“玩家收获”流程：
     * 1. 掉落物品
     * 2. 重置 age 或破坏方块
     */
    @Unique
    private void harvestOneBlock(BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        if (!(level() instanceof ServerLevel server)) return;

        /* 1. 掉落物（无论上下半都要掉落） */
        Block.getDrops(state, server, pos, null).forEach(stack -> {
            ItemStack remainder = addItem(stack);
            if (!remainder.isEmpty())
                Block.popResource(server, pos, remainder);
        });

        /* 2. 处理方块本身 */
        if (isCropPart(pos.above())) {
            /* 上方还有作物 → 当前是下半部分：重置 age */
            Optional<Property<Integer>> ageProp = getAgeProperty(state);
            if (ageProp.isPresent()) {
                Property<Integer> prop = ageProp.get();
                int resetAge = isReharvestable(
                        BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString())
                        ? getResetAge(
                        BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString(), prop)
                        : 0;
                server.setBlockAndUpdate(pos, state.setValue(prop, resetAge));
            } else {
                if (isDirtBlock(state.getBlock())) server.destroyBlock(pos, false);    // 没有 age 的一次性作物
            }
        } else {
            /* 上方没有作物 → 当前是上半部分：直接破坏 */
            if (isDirtBlock(state.getBlock())) server.destroyBlock(pos, false);
        }
    }
    @Inject(method = "work", at = @At("HEAD"), cancellable = true)
    private void work(BlockPos pos, CallbackInfo ci) {
        /* ---------- 新增：两格高作物处理 ---------- */
        if (isDoubleCrop(pos)) {
            // 1. 先确定上下两部分
            BlockPos lower, upper;
            if (isCropPart(pos.below())) {          // pos 是上格
                upper = pos;
                lower = pos.below();
            } else {                                // pos 是下格
                lower = pos;
                upper = pos.above();
            }

            // 2. 对两部分各执行一次“玩家收获”流程
            harvestOneBlock(lower);
            harvestOneBlock(upper);

            // 3. 统一收尾
            doPostWork(lower);   // 用 lower 或 upper 都行，只是播放粒子/声音的位置
            ci.cancel();
            return;
        }
        BlockState state = level().getBlockState(pos);
        if (level() instanceof ServerLevel serverLevel) {
            Block block = state.getBlock();
            String blockKey = BuiltInRegistries.BLOCK.getKey(block).toString();

            // 收集掉落物
            Block.getDrops(state, serverLevel, pos, null).forEach(stack -> {
                ItemStack remainder = addItem(stack);
                if (!remainder.isEmpty()) {
                    Block.popResource(serverLevel, pos, remainder);
                }
            });

            // 收获或重置成长阶段
            boolean harvestSuccessful = false;

            // 尝试重置成长阶段而不是破坏方块（对于可重复收获的作物）
            Optional<Property<Integer>> ageProperty = getAgeProperty(state);
            if (ageProperty.isPresent()) {
                Property<Integer> property = ageProperty.get();

                // 对于某些模组作物，可能需要特殊的收获处理
                if (isReharvestable(blockKey)) {
                    // 重复收获的作物，重置到某个阶段而不是0
                    int resetAge = getResetAge(blockKey, property);
                    serverLevel.setBlockAndUpdate(pos, state.setValue(property, resetAge));
                } else {
                    // 一次性收获的作物，重置到0
                    serverLevel.setBlockAndUpdate(pos, state.setValue(property, 0));
                }
                harvestSuccessful = true;
            }

            // 如果没有成长属性或特殊处理失败，直接破坏方块
            if (!harvestSuccessful) {
                if (shouldDestroyBlock(blockKey) && isDirtBlock(block)) {
                    serverLevel.destroyBlock(pos, false);
                }
            }

            // 消耗燃料
            consumeFuel(Config.getInstance().fuelTicksPerHarvest);

            // 生成粒子效果
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, state),
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 1.0);

            // 播放声音
            SoundEvent harvestSound = getHarvestSound(blockKey);
            serverLevel.playSound(null, pos, harvestSound, SoundSource.BLOCKS, 1.0f, 1.2f);
        }
        ci.cancel();
    }
    @Inject(method = "isCrop", at = @At("HEAD"), cancellable = true)
    private static void isCrop(Block block, CallbackInfoReturnable<Boolean> cir) {
        String key = BuiltInRegistries.BLOCK.getKey(block).toString();

        // 首先检查配置文件中明确指定的作物
        if (Config.getInstance().validCrops.containsKey(key)) {
            cir.setReturnValue(Config.getInstance().validCrops.get(key));
            cir.cancel();
            return;
        }
        // 如果启用了类检测，检查是否为原版作物类型
        if (((ConfigExt) ((Object) Config.getInstance())).fabricMixinTest$getEnableCropClassDetection()) {
            if (block instanceof CropBlock || block instanceof NetherWartBlock ||
                    block instanceof CocoaBlock || block instanceof PitcherCropBlock) {
                cir.setReturnValue(true);
                cir.cancel();
                return;
            }
        }

        // 如果启用了模组作物自动检测，使用更智能的检测方法
        if (((ConfigExt) ((Object) Config.getInstance())).fabricMixinTest$getEnableModdedCropAutoDetection()) {
            TagKey<Block> cropsTag = TagKey.create(BuiltInRegistries.BLOCK.key(), new ResourceLocation("c", "crops"));
            cir.setReturnValue(BuiltInRegistries.BLOCK.getOrCreateTag(cropsTag).contains(block.builtInRegistryHolder()));
            cir.cancel();
            return;
        }

        cir.setReturnValue(false);
        cir.cancel();
    }
    /**
     * 检查作物是否可以重复收获（如浆果丛、竹子等）
     */
    @Unique
    private static boolean isReharvestable(String blockKey) {
        return blockKey.contains("berry") || blockKey.contains("bush") ||
                blockKey.contains("vine") || blockKey.contains("bamboo") ||
                blockKey.startsWith("farmersdelight:rice") ||
                blockKey.contains("sweet_berry") || blockKey.contains("glow_berry");
    }

    /**
     * 获取作物重置后的年龄
     */
    @Unique
    private static int getResetAge(String blockKey, Property<Integer> property) {
        // 对于大多数可重复收获的作物，重置到某个中间阶段
        if (blockKey.contains("berry") || blockKey.contains("bush")) {
            // 浆果丛类型，通常重置到阶段1或2
            return Math.max(1, Collections.min(property.getPossibleValues()));
        }

        // 默认重置到最小值
        return Collections.min(property.getPossibleValues());
    }

    /**
     * 检查是否应该破坏方块
     */
    @Unique
    private static boolean shouldDestroyBlock(String blockKey) {

        // 对于永久性的作物，不要破坏方块
        if (blockKey.contains("berry") && blockKey.contains("bush")) {
            return false; // 浆果丛不破坏
        }

        return !blockKey.contains("vine") && !blockKey.contains("bamboo"); // 藤蔓和竹子不破坏

        // 默认情况下破坏方块
    }

    /**
     * 获取收获时的声音效果
     */
    @Unique
    private static SoundEvent getHarvestSound(String blockKey) {
        if (blockKey.contains("berry") || blockKey.contains("fruit")) {
            return SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES;
        } else if (blockKey.contains("bamboo")) {
            return SoundEvents.BAMBOO_BREAK;
        } else if (blockKey.contains("vine")) {
            return SoundEvents.VINE_BREAK;
        } else {
            return SoundEvents.PLAYER_ATTACK_SWEEP;
        }
    }
    @Inject(method = "getAgeProperty", at = @At("HEAD"), cancellable = true)
    private static void getAgeProperty(BlockState state, CallbackInfoReturnable<Optional<Property<Integer>>> cir) {
        // 尝试所有支持的成熟度属性名称
        for (String propertyName : ((ConfigExt) ((Object) Config.getInstance())).fabricMixinTest$getSupportedMaturityProperties()) {
            for (Property<?> property : state.getProperties()) {
                if (property.getName().equals(propertyName)) {
                    try {
                        //noinspection unchecked
                        cir.setReturnValue(Optional.of((Property<Integer>) property));
                        cir.cancel();
                        return;
                    } catch (ClassCastException e) {
                        // 如果不是Integer类型，继续尝试下一个
                    }
                }
            }
        }

        // 如果没有找到标准属性名，尝试寻找任何包含相关关键词的Integer属性
        for (Property<?> property : state.getProperties()) {
            String propName = property.getName().toLowerCase();
            if ((propName.contains("age") || propName.contains("growth") ||
                    propName.contains("stage") || propName.contains("maturity") ||
                    propName.contains("progress") || propName.contains("level")) &&
                    property.getValueClass() == Integer.class) {
                try {
                    //noinspection unchecked
                    cir.setReturnValue(Optional.of((Property<Integer>) property));
                    cir.cancel();
                    return;
                } catch (ClassCastException e) {
                    // 继续寻找
                }
            }
        }

        cir.setReturnValue(Optional.empty());
        cir.cancel();
    }
    @Inject(method = "isMature", at = @At("HEAD"), cancellable = true)
    private static void isMature(BlockState state, CallbackInfoReturnable<Boolean> cir) {

        Optional<Property<Integer>> ageProperty = getAgeProperty(state);

        if (ageProperty.isPresent()) {
            Property<Integer> property = ageProperty.get();
            Integer currentValue = state.getValue(property);
            Integer maxValue = Collections.max(property.getPossibleValues());

            // 检查当前值是否等于最大值
            cir.setReturnValue(Objects.equals(currentValue, maxValue));
            cir.cancel();
            return;
        }

        // 如果没有找到成熟度属性，尝试一些特殊的检测方法
        Block block = state.getBlock();
        String blockKey = BuiltInRegistries.BLOCK.getKey(block).toString();

        // 对于一些特殊的模组作物，可能需要特殊的成熟度检测
        if (blockKey.startsWith("farmersdelight:")) {
            // 农夫乐事的一些作物可能有特殊的成熟检测
            cir.setReturnValue(checkFarmersDelightMaturity(state));
            cir.cancel();
            return;
        } else if (blockKey.startsWith("mysticalagriculture:")) {
            // 神秘农艺的作物可能有特殊的成熟检测
            cir.setReturnValue(checkMysticalAgricultureMaturity(state));
            cir.cancel();
            return;
        }

        // 如果没有成熟度属性且不是特殊作物，假设它总是成熟的
        // 这对于一些简单的模组作物（如浆果丛）可能是合适的
        cir.setReturnValue(true);
        cir.cancel();
    }
    @Unique
    private static boolean checkFarmersDelightMaturity(BlockState state) {
        // 农夫乐事的特殊成熟度检测逻辑
        // 检查常见的农夫乐事属性
        for (Property<?> property : state.getProperties()) {
            String propName = property.getName();
            if (propName.equals("age") || propName.equals("maturity")) {
                if (property.getValueClass() == Integer.class) {
                    @SuppressWarnings("unchecked")
                    Property<Integer> intProperty = (Property<Integer>) property;
                    Integer currentValue = state.getValue(intProperty);
                    Integer maxValue = Collections.max(intProperty.getPossibleValues());
                    return Objects.equals(currentValue, maxValue);
                }
            }
        }
        return true; // 如果没有找到特定属性，假设成熟
    }

    @Unique
    private static boolean checkMysticalAgricultureMaturity(BlockState state) {
        // 神秘农艺的特殊成熟度检测逻辑
        for (Property<?> property : state.getProperties()) {
            String propName = property.getName();
            if (propName.equals("age") || propName.equals("growth") || propName.equals("stage")) {
                if (property.getValueClass() == Integer.class) {
                    @SuppressWarnings("unchecked")
                    Property<Integer> intProperty = (Property<Integer>) property;
                    Integer currentValue = state.getValue(intProperty);
                    Integer maxValue = Collections.max(intProperty.getPossibleValues());
                    return Objects.equals(currentValue, maxValue);
                }
            }
        }
        return true; // 如果没有找到特定属性，假设成熟
    }
}
