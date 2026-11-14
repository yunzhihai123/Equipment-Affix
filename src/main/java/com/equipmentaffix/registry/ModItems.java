package com.equipmentaffix.registry;

import com.equipmentaffix.EquipmentAffixMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.Keys.ITEMS, EquipmentAffixMod.MOD_ID);

    public static final RegistryObject<BlockItem> UPGRADE_TABLE = ITEMS.register("upgrade_table",
            () -> new BlockItem(ModBlocks.UPGRADE_TABLE.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> RESET_TABLE = ITEMS.register("reset_table",
            () -> new BlockItem(ModBlocks.RESET_TABLE.get(), new Item.Properties()));

    private ModItems() {
    }
}
