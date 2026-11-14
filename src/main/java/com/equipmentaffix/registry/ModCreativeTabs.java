package com.equipmentaffix.registry;

import com.equipmentaffix.EquipmentAffixMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EquipmentAffixMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.UPGRADE_TABLE.get()))
                    .title(Component.translatable("itemGroup.equipment_affix"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.UPGRADE_TABLE.get());
                        output.accept(ModItems.RESET_TABLE.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}
