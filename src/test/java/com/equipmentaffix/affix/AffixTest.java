package com.equipmentaffix.affix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AffixTest {
    @Test
    void valuesAreClampedToTheTierRange() {
        Affix low = new Affix(AffixType.ATTACK_DAMAGE, 1, -100.0D);
        assertEquals(AffixType.ATTACK_DAMAGE.minValue(1), low.value());

        Affix high = new Affix(AffixType.ATTACK_DAMAGE, 5, 999.0D);
        assertEquals(5, high.tier());
        assertEquals(AffixType.ATTACK_DAMAGE.maxValue(5), high.value());
    }

    @Test
    void tierIsClampedByTheConstructor() {
        assertEquals(1, new Affix(AffixType.CRIT_CHANCE, 0, 4.0D).tier());
        assertEquals(5, new Affix(AffixType.CRIT_CHANCE, 99, 4.0D).tier());
    }

    @Test
    void nullTypeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Affix(null, 1, 1.0D));
    }

    @Test
    void rarityWeightsAndAffixCountsFollowTheDesign() {
        assertEquals(55, Rarity.COMMON.weight());
        assertEquals(28, Rarity.UNCOMMON.weight());
        assertEquals(13, Rarity.RARE.weight());
        assertEquals(4, Rarity.EPIC.weight());
        assertEquals(3, Rarity.COMMON.affixCount());
        assertEquals(3, Rarity.UNCOMMON.affixCount());
        assertEquals(4, Rarity.RARE.affixCount());
        assertEquals(4, Rarity.EPIC.affixCount());
        assertEquals(Rarity.EPIC, Rarity.byId("epic"));
        assertEquals(Rarity.COMMON, Rarity.byId("unknown"));
    }
}
