package com.kltyton.kltytontoxhcrepair.mixin.bug.carry;

import com.kltyton.kltytontoxhcrepair.util.TaskScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.common.carry.CarryOnData;

@Pseudo
@Mixin(CarryOnData.class)
public class CarryOnDataMixin {
    @Inject(method = "setBlock", at = @At("RETURN"), cancellable = true)
    private void onSetBlock(BlockState state, BlockEntity tile, CallbackInfo ci) {
        if (tile.getLevel() != null && !tile.getLevel().isClientSide) {
            ServerLevel level = (ServerLevel) tile.getLevel();
            BlockPos pos = tile.getBlockPos();
            TaskScheduler.runLater(1, () -> {
                // 移除方块
                level.removeBlock(pos, false);
                level.sendBlockUpdated(pos, state, Blocks.AIR.defaultBlockState(), 3);
            });
        }
    }

}
