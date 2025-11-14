package com.kltyton.kltytontoxhcrepair.mixin.campfire;

import com.natamus.healingcampfire_common_fabric.events.CampfireEvent;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CampfireEvent.class)
public class CampfireEventMixin {
    @Redirect(method = "playerTickEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0))
    private static <T extends Comparable<T>> T getValue(BlockState instance, Property<T> property) {
        if (!instance.hasProperty(CampfireBlock.LIT)) {
            if (property == CampfireBlock.LIT) {
                return property.getValueClass().cast(Boolean.FALSE);
            }
            throw new IllegalArgumentException("Property " + property.getName() + " is not supported");
        }
        return instance.getValue(property);
    }
    @Redirect(method = "playerTickEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 1))
    private static <T extends Comparable<T>> T getValue2(BlockState instance, Property<T> property) {
        if (!instance.hasProperty(CampfireBlock.SIGNAL_FIRE)) {
            if (property == CampfireBlock.SIGNAL_FIRE) {
                return property.getValueClass().cast(Boolean.FALSE);
            }
            throw new IllegalArgumentException("Property " + property.getName() + " is not supported");
        }
        return instance.getValue(property);
    }
}
