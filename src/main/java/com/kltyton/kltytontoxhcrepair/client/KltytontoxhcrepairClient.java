package com.kltyton.kltytontoxhcrepair.client;

import com.kltyton.kltytontoxhcrepair.Kltytontoxhcrepair;
import com.kltyton.kltytontoxhcrepair.network.ModNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class KltytontoxhcrepairClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModNetwork.Client.init();
    }
}
