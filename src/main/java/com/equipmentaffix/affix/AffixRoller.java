package com.equipmentaffix.affix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/**
 * All random affix generation lives here so the preview shown in the table and
 * the item that is actually produced can use the same deterministic seed.
 */
public final class AffixRoller {
    private AffixRoller() {
    }

    public static void applyCrafted(ItemStack stack, RandomSource random) {
        if (!AffixData.hasAffixes(stack)) {
            AffixData.write(stack, roll(stack, random, Rarity.COMMON), 0, Rarity.COMMON);
        }
    }

    public static void applyInitialized(ItemStack stack, RandomSource random) {
        AffixData.write(stack, roll(stack, random, Rarity.COMMON), 0, Rarity.COMMON);
    }

    public static void applyLoot(ItemStack stack, RandomSource random, float luck) {
        if (AffixData.hasAffixes(stack)) {
            return;
        }
        Rarity rarity = rollRarity(random, luck);
        AffixData.write(stack, roll(stack, random, rarity), 0, rarity);
    }

    public static List<Affix> roll(ItemStack stack, RandomSource random, Rarity rarity) {
        AffixCategory category = AffixData.category(stack);
        if (category == null) {
            return List.of();
        }
        int count = rarity.affixCount();
        List<AffixType> types = pickTypes(category, count, random);
        int[] tiers = new int[types.size()];
        for (int index = 0; index < tiers.length; index++) {
            int span = rarity.maxTier() - rarity.minTier();
            tiers[index] = rarity.minTier() + (span <= 0 ? 0 : random.nextInt(span + 1));
        }
        List<Affix> result = new ArrayList<>(types.size());
        for (int index = 0; index < types.size(); index++) {
            result.add(Affix.roll(types.get(index), tiers[index], random));
        }
        return result;
    }

    public static Rarity rollRarity(RandomSource random, float luck) {
        double bonus = Math.max(0.0D, luck);
        double common = Rarity.COMMON.weight();
        double uncommon = Rarity.UNCOMMON.weight();
        double rare = Rarity.RARE.weight() + bonus * 4.0D;
        double epic = Rarity.EPIC.weight() + bonus * 3.0D;
        double total = common + uncommon + rare + epic;
        double roll = random.nextDouble() * total;
        if ((roll -= common) < 0.0D) {
            return Rarity.COMMON;
        }
        if ((roll -= uncommon) < 0.0D) {
            return Rarity.UNCOMMON;
        }
        if ((roll -= rare) < 0.0D) {
            return Rarity.RARE;
        }
        return Rarity.EPIC;
    }

    public static void applyBreakthrough(ItemStack stack, RandomSource random) {
        List<Affix> current = AffixData.affixes(stack);
        if (current.isEmpty()) {
            return;
        }
        int index = random.nextInt(current.size());
        Affix chosen = current.get(index);
        int newTier = AffixType.clampTier(chosen.tier() + 1);
        current.set(index, Affix.roll(chosen.type(), newTier, random));
        AffixData.write(stack, current, AffixData.level(stack), AffixData.rarity(stack));
    }

    public static void applyReset(ItemStack stack, RandomSource random) {
        List<Affix> current = AffixData.affixes(stack);
        if (current.isEmpty()) {
            return;
        }
        AffixCategory category = AffixData.category(stack);
        if (category == null) {
            return;
        }
        int count = current.size();
        int totalTier = current.stream().mapToInt(Affix::tier).sum();
        List<AffixType> types = pickTypes(category, count, random);
        int[] tiers = distribute(totalTier, types.size(), 1, 5, random);
        List<Affix> result = new ArrayList<>(types.size());
        for (int index = 0; index < types.size(); index++) {
            result.add(Affix.roll(types.get(index), tiers[index], random));
        }
        AffixData.write(stack, result, AffixData.level(stack), AffixData.rarity(stack));
    }

    public static List<AffixType> pickTypes(AffixCategory category, int count, RandomSource random) {
        List<AffixType> pool = new ArrayList<>(AffixType.forCategory(category));
        for (int index = pool.size() - 1; index > 0; index--) {
            int swap = random.nextInt(index + 1);
            Collections.swap(pool, index, swap);
        }
        return pool.subList(0, Math.min(count, pool.size()));
    }

    public static int[] distribute(int total, int count, int min, int max, RandomSource random) {
        int[] tiers = new int[count];
        Arrays.fill(tiers, min);
        int extra = total - count * min;
        int guard = 0;
        while (extra > 0 && guard++ < 10000) {
            int index = random.nextInt(count);
            if (tiers[index] < max) {
                tiers[index]++;
                extra--;
            }
        }
        return tiers;
    }
}
