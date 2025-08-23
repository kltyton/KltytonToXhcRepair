package com.kltyton.kltytontoxhcrepair.mixin.bug.cruncher;

import com.ninni.species.client.inventory.CruncherInventoryMenu;
import com.ninni.species.client.inventory.CruncherInventoryScreen;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.registry.SpeciesNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Pseudo
@Mixin(SpeciesNetwork.Client.class)
public class SpeciesNetworkMixin {
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/client/networking/v1/ClientPlayNetworking;registerGlobalReceiver(Lnet/minecraft/resources/ResourceLocation;Lnet/fabricmc/fabric/api/client/networking/v1/ClientPlayNetworking$PlayChannelHandler;)Z", ordinal = 1))
    private static boolean registerGlobalReceiver(ResourceLocation channelName, ClientPlayNetworking.PlayChannelHandler channelHandler) {
        return ClientPlayNetworking.registerGlobalReceiver(SpeciesNetwork.OPEN_CRUNCHER_SCREEN, (client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            int syncId = buf.readInt();

            client.execute(() -> {
                Level level = client.level;
                Entity entity = Objects.requireNonNull(level).getEntity(id);
                if (entity instanceof Cruncher cruncher) {
                    LocalPlayer player = client.player;
                    CruncherInventoryMenu menu = new CruncherInventoryMenu(
                            syncId,
                            Objects.requireNonNull(player).getInventory(),
                            cruncher.getInventory(),
                            cruncher
                    );
                    player.containerMenu = menu;
                    client.setScreen(new CruncherInventoryScreen(menu, player.getInventory(), cruncher));
                }
            });
        });

    }
}
