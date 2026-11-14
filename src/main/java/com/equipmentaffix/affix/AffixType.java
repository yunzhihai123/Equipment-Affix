package com.equipmentaffix.affix;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * All affixes are defined in code so that they are easy to balance and easy to
 * expose to the config screen. Values are stored in percentage points (e.g.
 * 10.0 means +10%) or as flat amounts for the few flat affixes.
 */
public enum AffixType {
    ATTACK_DAMAGE("attack_damage", EnumSet.of(AffixCategory.MELEE), Effect.ATTACK_DAMAGE, true,
            new double[]{4, 7, 10, 14, 19}, new double[]{6, 9, 13, 18, 24}),
    CRIT_CHANCE("crit_chance", EnumSet.of(AffixCategory.MELEE, AffixCategory.RANGED), Effect.CRIT_CHANCE, true,
            new double[]{3, 6, 9, 13, 17}, new double[]{5, 8, 12, 16, 20}),
    CRIT_DAMAGE("crit_damage", EnumSet.of(AffixCategory.MELEE, AffixCategory.RANGED), Effect.CRIT_DAMAGE, true,
            new double[]{8, 13, 19, 26, 35}, new double[]{12, 18, 25, 34, 45}),
    ATTACK_SPEED("attack_speed", EnumSet.of(AffixCategory.MELEE), Effect.ATTACK_SPEED, true,
            new double[]{3, 5, 8, 11, 15}, new double[]{4, 7, 10, 14, 18}),
    REACH("reach", EnumSet.of(AffixCategory.MELEE), Effect.REACH, true,
            new double[]{3, 6, 9, 13, 17}, new double[]{5, 8, 12, 16, 20}),
    KNOCKBACK("knockback", EnumSet.of(AffixCategory.MELEE), Effect.KNOCKBACK, true,
            new double[]{8, 13, 19, 26, 35}, new double[]{12, 18, 25, 34, 45}),
    PROJECTILE_DAMAGE("projectile_damage", EnumSet.of(AffixCategory.RANGED), Effect.PROJECTILE_DAMAGE, true,
            new double[]{5, 8, 12, 17, 23}, new double[]{7, 11, 16, 22, 30}),
    PROJECTILE_SPEED("projectile_speed", EnumSet.of(AffixCategory.RANGED), Effect.PROJECTILE_SPEED, true,
            new double[]{5, 9, 14, 20, 27}, new double[]{8, 13, 19, 26, 35}),
    MINING_SPEED("mining_speed", EnumSet.of(AffixCategory.TOOL), Effect.MINING_SPEED, true,
            new double[]{6, 10, 15, 21, 28}, new double[]{9, 14, 20, 27, 35}),
    XP_BONUS("xp_bonus", EnumSet.of(AffixCategory.TOOL), Effect.XP_BONUS, true,
            new double[]{5, 9, 14, 20, 27}, new double[]{8, 13, 19, 26, 35}),
    LUCK("luck", EnumSet.of(AffixCategory.TOOL), Effect.LUCK, false,
            new double[]{1, 3, 5, 7, 9}, new double[]{2, 4, 6, 8, 10}),
    MAX_HEALTH("max_health", EnumSet.of(AffixCategory.ARMOR), Effect.MAX_HEALTH, false,
            new double[]{2, 4, 6, 9, 12}, new double[]{3, 5, 8, 11, 15}),
    ARMOR("armor", EnumSet.of(AffixCategory.ARMOR), Effect.ARMOR, false,
            new double[]{1, 3, 5, 7, 9}, new double[]{2, 4, 6, 8, 10}),
    ARMOR_TOUGHNESS("armor_toughness", EnumSet.of(AffixCategory.ARMOR), Effect.ARMOR_TOUGHNESS, false,
            new double[]{1, 1, 2, 3, 4}, new double[]{1, 2, 3, 4, 5}),
    KNOCKBACK_RESISTANCE("knockback_resistance", EnumSet.of(AffixCategory.ARMOR), Effect.KNOCKBACK_RESISTANCE, true,
            new double[]{3, 6, 9, 13, 17}, new double[]{5, 8, 12, 16, 20}),
    MOVEMENT_SPEED("movement_speed", EnumSet.of(AffixCategory.ARMOR), Effect.MOVEMENT_SPEED, true,
            new double[]{2, 4, 6, 8, 10}, new double[]{3, 5, 7, 9, 12}),
    DAMAGE_REDUCTION("damage_reduction", EnumSet.of(AffixCategory.ARMOR), Effect.DAMAGE_REDUCTION, true,
            new double[]{2, 4, 6, 9, 12}, new double[]{3, 5, 8, 11, 15});

    /** The mechanical effect of an affix. */
    public enum Effect {
        ATTACK_DAMAGE,
        CRIT_CHANCE,
        CRIT_DAMAGE,
        ATTACK_SPEED,
        REACH,
        KNOCKBACK,
        PROJECTILE_DAMAGE,
        PROJECTILE_SPEED,
        MINING_SPEED,
        XP_BONUS,
        LUCK,
        MAX_HEALTH,
        ARMOR,
        ARMOR_TOUGHNESS,
        KNOCKBACK_RESISTANCE,
        MOVEMENT_SPEED,
        DAMAGE_REDUCTION
    }

    private static final Map<String, AffixType> BY_ID = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(AffixType::id, Function.identity()));

    private final String id;
    private final Set<AffixCategory> categories;
    private final Effect effect;
    private final boolean percent;
    private final double[] minValues;
    private final double[] maxValues;

    AffixType(String id, Set<AffixCategory> categories, Effect effect, boolean percent,
              double[] minValues, double[] maxValues) {
        if (minValues.length != 5 || maxValues.length != 5) {
            throw new IllegalArgumentException("Every affix needs exactly five tiers");
        }
        this.id = id;
        this.categories = Set.copyOf(categories);
        this.effect = effect;
        this.percent = percent;
        this.minValues = minValues.clone();
        this.maxValues = maxValues.clone();
    }

    public String id() {
        return this.id;
    }

    public Set<AffixCategory> categories() {
        return this.categories;
    }

    public Effect effect() {
        return this.effect;
    }

    public boolean isPercent() {
        return this.percent;
    }

    public double minValue(int tier) {
        return this.minValues[clampTier(tier) - 1];
    }

    public double maxValue(int tier) {
        return this.maxValues[clampTier(tier) - 1];
    }

    public String translationKey() {
        return "affix.equipment_affix." + this.id;
    }

    public String descriptionKey() {
        return "affix.equipment_affix." + this.id + ".desc";
    }

    public String formatValue(double value) {
        double rounded = Math.round(value * 10.0D) / 10.0D;
        String number = rounded == Math.rint(rounded)
                ? Long.toString(Math.round(rounded))
                : String.format(Locale.ROOT, "%.1f", rounded);
        return number + (this.percent ? "%" : "");
    }

    public static AffixType byId(String id) {
        return BY_ID.get(id);
    }

    public static List<AffixType> forCategory(AffixCategory category) {
        return Arrays.stream(values())
                .filter(type -> type.categories.contains(category))
                .toList();
    }

    public static int clampTier(int tier) {
        return Math.max(1, Math.min(5, tier));
    }
}
