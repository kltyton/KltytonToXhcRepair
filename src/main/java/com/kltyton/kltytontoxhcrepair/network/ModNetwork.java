package com.kltyton.kltytontoxhcrepair.network;

import com.ninni.species.entity.Cruncher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ModNetwork {
    //public static final ResourceLocation OPEN_CRUNCHER_SCREEN = new ResourceLocation("kltyton", "open_cruncher_screen");
    @Environment(EnvType.CLIENT)
    public static class Client {
        public static void init() {
/*            ClientPlayNetworking.registerGlobalReceiver(OPEN_CRUNCHER_SCREEN, (client, handler, buf, responseSender) -> {
                int id = buf.readInt();
                int slotCount = buf.readInt(); // 其实可以忽略
                int syncId = buf.readInt();

                client.execute(() -> {
                    Level level = client.level;
                    Entity entity = level.getEntity(id);
                    if (entity instanceof Cruncher cruncher) {
                        LocalPlayer player = client.player;
                        KltytonCruncherInventoryMenu menu = new KltytonCruncherInventoryMenu(
                                syncId,
                                player.getInventory(),
                                cruncher.getInventory(), // ✅ 使用真实的 Cruncher 库存
                                cruncher
                        );
                        player.containerMenu = menu;
                        client.setScreen(new KltytonCruncherInventoryScreen(menu, player.getInventory(), cruncher));
                    }
                });
            });*/
        }
    }
}
