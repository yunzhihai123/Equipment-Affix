package com.equipmentaffix;

import com.equipmentaffix.registry.ModBlockEntities;
import com.equipmentaffix.registry.ModBlocks;
import com.equipmentaffix.registry.ModCreativeTabs;
import com.equipmentaffix.registry.ModItems;
import com.equipmentaffix.registry.ModLootModifiers;
import com.equipmentaffix.registry.ModMenus;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Main entry point for Equipment Affix.
 */
@Mod(EquipmentAffixMod.MOD_ID)
public final class EquipmentAffixMod {
    public static final String MOD_ID = "equipment_affix";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EquipmentAffixMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        ModLootModifiers.SERIALIZERS.register(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        LOGGER.info("Equipment Affix loaded");
    }
}
