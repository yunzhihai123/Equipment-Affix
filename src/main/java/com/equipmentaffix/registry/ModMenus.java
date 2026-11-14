package com.equipmentaffix.registry;

import com.equipmentaffix.EquipmentAffixMod;
import com.equipmentaffix.block.TableKind;
import com.equipmentaffix.menu.AffixTableMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.Keys.MENU_TYPES, EquipmentAffixMod.MOD_ID);

    public static final RegistryObject<MenuType<AffixTableMenu>> UPGRADE_TABLE = MENUS.register("upgrade_table",
            () -> IForgeMenuType.create((id, inventory, data) -> new AffixTableMenu(id, inventory, data, TableKind.UPGRADE)));

    public static final RegistryObject<MenuType<AffixTableMenu>> RESET_TABLE = MENUS.register("reset_table",
            () -> IForgeMenuType.create((id, inventory, data) -> new AffixTableMenu(id, inventory, data, TableKind.RESET)));

    private ModMenus() {
    }
}
