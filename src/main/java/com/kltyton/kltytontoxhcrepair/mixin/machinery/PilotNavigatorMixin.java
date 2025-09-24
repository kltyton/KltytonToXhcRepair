package com.kltyton.kltytontoxhcrepair.mixin.machinery;

import com.kltyton.kltytontoxhcrepair.Kltytontoxhcrepair;
import immersive_aircraft.entity.VehicleEntity;
import immersive_machinery.client.render.entity.renderer.PathDebugRenderer;
import immersive_machinery.entity.misc.PilotNavigator;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(PilotNavigator.class)
public abstract class PilotNavigatorMixin {
    @Shadow(remap = false) @Final private VehicleEntity vehicle;
    @Shadow(remap = false) @Final private int accuracy;

    @Shadow(remap = false) private BlockPos target;

    @Shadow(remap = false) private int stuckTime;

    @Shadow(remap = false) private Path currentPath;

    @Shadow(remap = false) protected abstract Path findPath(BlockPos pos);

    @Inject(method = "moveTo", at = @At("HEAD"), cancellable = true)
    private void onMoveTo(BlockPos pos, CallbackInfo ci) {
        if (!pos.equals(this.target) && !pos.equals(this.vehicle.blockPosition())) {
            this.target = pos;
            this.stuckTime = 0;
            Block block = this.vehicle.level().getBlockState(pos).getBlock();
            if (block == Blocks.PUMPKIN || block == Blocks.MELON) {
                this.currentPath = this.findPath(pos.offset(0, 1, 0));
            } else this.currentPath = this.findPath(pos);
            if (this.currentPath != null && !this.vehicle.level().getBlockState(pos.offset(0, -1, 0)).getFluidState().is(FluidTags.WATER) && (double)this.currentPath.getDistToTarget() > (double)this.accuracy + (double)0.5F) {
                this.currentPath = null;
            }
            PathDebugRenderer.INSTANCE.setPath(this.currentPath, this.vehicle);
        }
        ci.cancel();
    }
}
