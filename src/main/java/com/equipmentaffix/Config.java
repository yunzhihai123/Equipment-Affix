package com.equipmentaffix;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Server-side configuration. Values are read lazily because the config is not
 * loaded while the mod is being constructed.
 */
public final class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue LOOT_CHANCE;
    public static final ForgeConfigSpec.BooleanValue AUTO_DETECT_CRAFTED;
    public static final ForgeConfigSpec.BooleanValue AFFIX_CREATIVE_ITEMS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Equipment Affix gameplay settings").push("general");
        LOOT_CHANCE = builder
                .comment("Chance for each naturally generated equipment item to receive affixes.")
                .defineInRange("lootChance", 0.45D, 0.0D, 1.0D);
        AUTO_DETECT_CRAFTED = builder
                .comment("Give freshly crafted equipment three tier 1 affixes automatically.")
                .define("autoDetectCrafted", true);
        AFFIX_CREATIVE_ITEMS = builder
                .comment("Give affixable equipment in a creative player's inventory three tier 1 affixes automatically.")
                .define("affixCreativeItems", true);
        BLACKLIST = builder
                .comment("Item ids that must never receive affixes.")
                .defineList("blacklist", List.of(), value -> value instanceof String);
        builder.pop();
        SPEC = builder.build();
    }

    private Config() {
    }

    public static double lootChance() {
        try {
            return LOOT_CHANCE.get();
        } catch (IllegalStateException exception) {
            return 0.45D;
        }
    }

    public static boolean autoDetectCrafted() {
        try {
            return AUTO_DETECT_CRAFTED.get();
        } catch (IllegalStateException exception) {
            return true;
        }
    }

    public static boolean affixCreativeItems() {
        try {
            return AFFIX_CREATIVE_ITEMS.get();
        } catch (IllegalStateException exception) {
            return true;
        }
    }

    public static boolean isBlacklisted(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        try {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
            return id != null && BLACKLIST.get().contains(id.toString());
        } catch (IllegalStateException exception) {
            return false;
        }
    }
}
