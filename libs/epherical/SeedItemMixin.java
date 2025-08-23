package com.kltyton.kltytontoxhcrepair.mixin;

import com.epherical.croptopia.items.SeedItem;
import io.github.uhq_games.regions_unexplored.block.RuBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SeedItem.class)
public class SeedItemMixin {
    @Redirect(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"))
    private Block modifyUseOn(BlockState state, UseOnContext context) {
        BlockPos hitPos = context.getClickedPos();
        Level world = context.getLevel();
        BlockPos abovePos = hitPos.above();
        Block block = state.getBlock();
        if ((block == RuBlocks.SILT_FARMLAND) && world.isEmptyBlock(abovePos)) {
            world.setBlock(abovePos, ((SeedItem)(Object)this).getBlock().defaultBlockState(), 3);
            context.getItemInHand().shrink(1);
            return block;
        }
        return block;
    }
}