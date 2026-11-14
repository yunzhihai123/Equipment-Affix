package com.equipmentaffix.affix;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Levels, experience costs and catalyst materials for the upgrade table.
 */
public final class Costs {
    public static final int MAX_LEVEL = 20;
    public static final int RESET_XP = 12;
    public static final int INITIALIZE_XP = 3;

    private Costs() {
    }

    public static int xpCost(int currentLevel) {
        if (currentLevel >= MAX_LEVEL) {
            return 0;
        }
        return (int) Math.ceil(6.0D * Math.pow(1.14D, currentLevel));
    }

    /** Levels 3, 7, 11, 15 and 19 are the last normal levels before a breakthrough. */
    public static boolean isBreakthrough(int currentLevel) {
        return currentLevel == 3 || currentLevel == 7 || currentLevel == 11
                || currentLevel == 15 || currentLevel == 19;
    }

    public static ItemStack materialForUpgrade(int currentLevel) {
        if (!isBreakthrough(currentLevel)) {
            return new ItemStack(Items.AMETHYST_SHARD);
        }
        return switch (currentLevel) {
            case 3 -> new ItemStack(Items.AMETHYST_SHARD, 8);
            case 7 -> new ItemStack(Items.DIAMOND);
            case 11 -> new ItemStack(Items.NETHERITE_SCRAP);
            case 15 -> new ItemStack(Items.ECHO_SHARD);
            case 19 -> new ItemStack(Items.NETHER_STAR);
            default -> new ItemStack(Items.AMETHYST_SHARD);
        };
    }

    public static ItemStack materialForReset() {
        return new ItemStack(Items.ECHO_SHARD);
    }

    public static ItemStack materialForInitialization() {
        return new ItemStack(Items.AMETHYST_SHARD);
    }
}
