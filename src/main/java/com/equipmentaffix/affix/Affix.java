package com.equipmentaffix.affix;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

/**
 * One rolled affix on an item stack.
 */
public record Affix(AffixType type, int tier, double value) {
    public Affix {
        if (type == null) {
            throw new IllegalArgumentException("Affix type must not be null");
        }
        tier = AffixType.clampTier(tier);
        double minimum = type.minValue(tier);
        double maximum = type.maxValue(tier);
        if (!Double.isFinite(value)) {
            value = minimum;
        }
        value = Math.max(minimum, Math.min(maximum, value));
    }

    public static Affix roll(AffixType type, int tier, RandomSource random) {
        tier = AffixType.clampTier(tier);
        double minimum = type.minValue(tier);
        double maximum = type.maxValue(tier);
        double rolled = minimum + random.nextDouble() * (maximum - minimum);
        rolled = Math.round(rolled * 10.0D) / 10.0D;
        return new Affix(type, tier, rolled);
    }

    public Affix withTier(int newTier, RandomSource random) {
        return roll(this.type, newTier, random);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", this.type.id());
        tag.putInt("Tier", this.tier);
        tag.putDouble("Value", this.value);
        return tag;
    }

    public static Affix load(CompoundTag tag) {
        AffixType type = AffixType.byId(tag.getString("Id"));
        if (type == null) {
            return null;
        }
        int tier = AffixType.clampTier(tag.contains("Tier") ? tag.getInt("Tier") : 1);
        double value = tag.contains("Value") ? tag.getDouble("Value") : type.minValue(tier);
        return new Affix(type, tier, value);
    }

    public String formattedValue() {
        return this.type.formatValue(this.value);
    }
}
