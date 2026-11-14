package com.equipmentaffix.compat.jei;

import com.equipmentaffix.EquipmentAffixMod;
import com.equipmentaffix.affix.AffixData;
import com.equipmentaffix.affix.AffixType;
import com.equipmentaffix.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Optional JEI information pages. JEI is a compile-time/runtime-only dependency:
 * no JEI classes are loaded unless JEI itself is installed.
 */
@JeiPlugin
public final class AffixJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID =
            new ResourceLocation(EquipmentAffixMod.MOD_ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addItemStackInfo(new ItemStack(ModItems.UPGRADE_TABLE.get()),
                Component.translatable("jei.equipment_affix.upgrade.1"),
                Component.translatable("jei.equipment_affix.upgrade.2"),
                Component.translatable("jei.equipment_affix.upgrade.3"),
                Component.translatable("jei.equipment_affix.upgrade.4"),
                Component.translatable("jei.equipment_affix.upgrade.5"),
                Component.translatable("jei.equipment_affix.upgrade.6"));
        registration.addItemStackInfo(new ItemStack(ModItems.RESET_TABLE.get()),
                Component.translatable("jei.equipment_affix.reforge.1"),
                Component.translatable("jei.equipment_affix.reforge.2"));

        addCatalyst(registration, Items.AMETHYST_SHARD, "amethyst.1");
        addCatalyst(registration, Items.DIAMOND, "diamond.1");
        addCatalyst(registration, Items.NETHERITE_SCRAP, "netherite_scrap.1");
        addCatalyst(registration, Items.ECHO_SHARD, "echo_shard.1");
        addCatalyst(registration, Items.NETHER_STAR, "nether_star.1");
        addCatalyst(registration, Items.SMITHING_TABLE, "smithing_table.1");

        List<ItemStack> affixable = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            ItemStack stack = new ItemStack(item);
            if (AffixData.isAffixable(stack)) {
                affixable.add(stack);
            }
        }
        if (!affixable.isEmpty()) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("jei.equipment_affix.equipment.1"));
            lines.add(Component.translatable("jei.equipment_affix.equipment.2"));
            for (AffixType type : AffixType.values()) {
                lines.add(Component.translatable("jei.equipment_affix.affix_entry",
                        Component.translatable(type.translationKey()),
                        type.formatValue(type.minValue(1)) + " - " + type.formatValue(type.maxValue(5))));
            }
            registration.addItemStackInfo(affixable, lines.toArray(Component[]::new));
        }
    }

    private static void addCatalyst(IRecipeRegistration registration, Item item, String key) {
        registration.addItemStackInfo(new ItemStack(item),
                Component.translatable("jei.equipment_affix." + key));
    }
}
