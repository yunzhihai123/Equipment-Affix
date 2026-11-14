package com.equipmentaffix.client;

import com.equipmentaffix.EquipmentAffixMod;
import com.equipmentaffix.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EquipmentAffixMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.UPGRADE_TABLE.get(), AffixTableScreen::new);
            MenuScreens.register(ModMenus.RESET_TABLE.get(), AffixTableScreen::new);
        });
    }
}
