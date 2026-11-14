package com.equipmentaffix.affix;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;

/**
 * Loot rarity. Crafted equipment is always common; loot uses the weights below.
 */
public enum Rarity {
    COMMON("common", 55, 3, 1, 1, ChatFormatting.WHITE),
    UNCOMMON("uncommon", 28, 3, 1, 2, ChatFormatting.GREEN),
    RARE("rare", 13, 4, 2, 2, ChatFormatting.BLUE),
    EPIC("epic", 4, 4, 2, 3, ChatFormatting.DARK_PURPLE);

    private static final Map<String, Rarity> BY_ID = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(Rarity::id, Function.identity()));

    private final String id;
    private final int weight;
    private final int affixCount;
    private final int minTier;
    private final int maxTier;
    private final ChatFormatting color;

    Rarity(String id, int weight, int affixCount, int minTier, int maxTier, ChatFormatting color) {
        this.id = id;
        this.weight = weight;
        this.affixCount = affixCount;
        this.minTier = minTier;
        this.maxTier = maxTier;
        this.color = color;
    }

    public String id() {
        return this.id;
    }

    public int weight() {
        return this.weight;
    }

    public int affixCount() {
        return this.affixCount;
    }

    public int minTier() {
        return this.minTier;
    }

    public int maxTier() {
        return this.maxTier;
    }

    public ChatFormatting color() {
        return this.color;
    }

    public String translationKey() {
        return "rarity.equipment_affix." + this.id;
    }

    public static Rarity byId(String id) {
        return BY_ID.getOrDefault(id, COMMON);
    }
}
