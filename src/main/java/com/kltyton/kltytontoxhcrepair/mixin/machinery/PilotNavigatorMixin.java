package com.kltyton.kltytontoxhcrepair.mixin.machinery;

import immersive_aircraft.entity.VehicleEntity;
import immersive_machinery.client.render.entity.renderer.PathDebugRenderer;
import immersive_machinery.entity.misc.PilotNavigator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PilotNavigator.class)
public abstract class PilotNavigatorMixin {
    @Shadow @Final private VehicleEntity vehicle;
    @Shadow @Final private int accuracy;

    @Shadow private BlockPos target;

    @Shadow private int stuckTime;

    @Shadow private Path currentPath;

    @Shadow protected abstract Path findPath(BlockPos pos);

    @Inject(method = "moveTo", at = @At("HEAD"), cancellable = true)
    private void onMoveTo(BlockPos pos, CallbackInfo ci) {
        if (!pos.equals(this.target) && !pos.equals(this.vehicle.blockPosition())) {
            this.target = pos;
            this.stuckTime = 0;
            this.currentPath = this.findPath(pos);
            if (this.currentPath != null && (double)this.currentPath.getDistToTarget() > (double)this.accuracy + (double)16F) {
                this.currentPath = null;
            }

            PathDebugRenderer.INSTANCE.setPath(this.currentPath, this.vehicle);
        }
        ci.cancel();
    }
}
