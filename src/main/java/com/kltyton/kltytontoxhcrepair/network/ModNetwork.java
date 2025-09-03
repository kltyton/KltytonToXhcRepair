package com.kltyton.kltytontoxhcrepair.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ModNetwork {
    //public static final ResourceLocation OPEN_CRUNCHER_SCREEN = new ResourceLocation("kltyton", "open_cruncher_screen");
    //public static final ResourceLocation SYNC_INV = new ResourceLocation("kltytontoxhcrepair", "sync_inventory");

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
    public static class Server {
        public static void init() {
        }
    }
}
