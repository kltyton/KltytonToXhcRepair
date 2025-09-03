package com.kltyton.kltytontoxhcrepair.mixin.bug.carry;

import com.kltyton.kltytontoxhcrepair.util.TaskScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.block.BrewingstationBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.PlacementHandler;

import java.util.function.BiFunction;

@Mixin(PlacementHandler.class)
public class PlacementHandlerMixin {
    @Inject(method = "tryPlaceBlock", at = @At(value = "INVOKE", target = "Ltschipp/carryon/common/carry/CarryOnData;isCarrying(Ltschipp/carryon/common/carry/CarryOnData$CarryType;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private static void tryPlaceBlock(ServerPlayer player, BlockPos pos, Direction facing, BiFunction<BlockPos, BlockState, Boolean> placementCallback, CallbackInfoReturnable<Boolean> cir, CarryOnData carry) {
        if (carry.getBlock().getBlock() instanceof BrewingstationBlock) {
            BlockPos newPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            TaskScheduler.runLater(1, () -> {
                BlockState blockState = player.level().getBlockState(newPos);
                if (blockState.getBlock() instanceof BrewingstationBlock brewingstationBlock) {
                    brewingstationBlock.setPlacedBy(player.level(), newPos, blockState, player, null);
                }
            });
        }
    }
}
