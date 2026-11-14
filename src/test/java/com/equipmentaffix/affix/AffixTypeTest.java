package com.equipmentaffix.affix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class AffixTypeTest {
    @Test
    void everyTypeHasFiveOrderedTiers() {
        for (AffixType type : AffixType.values()) {
            for (int tier = 1; tier <= 5; tier++) {
                double minimum = type.minValue(tier);
                double maximum = type.maxValue(tier);
                assertTrue(minimum >= 0.0D, type.id() + " tier " + tier + " has a negative minimum");
                assertTrue(minimum <= maximum, type.id() + " tier " + tier + " min > max");
            }
        }
    }

    @Test
    void tierIsClampedToOneThroughFive() {
        for (AffixType type : AffixType.values()) {
            assertEquals(type.minValue(1), type.minValue(0));
            assertEquals(type.minValue(1), type.minValue(-20));
            assertEquals(type.maxValue(5), type.maxValue(6));
            assertEquals(type.maxValue(5), type.maxValue(100));
        }
    }

    @Test
    void idsRoundTrip() {
        for (AffixType type : AffixType.values()) {
            assertEquals(type, AffixType.byId(type.id()));
        }
        assertNull(AffixType.byId("does_not_exist"));
    }

    @Test
    void categoryPoolsContainTheExpectedAffixes() {
        List<AffixType> melee = AffixType.forCategory(AffixCategory.MELEE);
        assertTrue(melee.contains(AffixType.ATTACK_DAMAGE));
        assertTrue(melee.contains(AffixType.CRIT_CHANCE));
        assertTrue(melee.contains(AffixType.KNOCKBACK));

        List<AffixType> armor = AffixType.forCategory(AffixCategory.ARMOR);
        assertTrue(armor.contains(AffixType.MAX_HEALTH));
        assertTrue(armor.contains(AffixType.DAMAGE_REDUCTION));

        List<AffixType> tools = AffixType.forCategory(AffixCategory.TOOL);
        assertTrue(tools.contains(AffixType.MINING_SPEED));
        assertTrue(tools.contains(AffixType.LUCK));
    }

    @Test
    void valuesAreFormattedForTheTooltip() {
        assertEquals("12.3%", AffixType.CRIT_CHANCE.formatValue(12.3D));
        assertEquals("5%", AffixType.CRIT_CHANCE.formatValue(5.0D));
        assertEquals("3", AffixType.LUCK.formatValue(3.0D));
        assertEquals("4.5", AffixType.LUCK.formatValue(4.5D));
    }

    @Test
    void everyAffixHasTranslationKeys() {
        for (AffixType type : AffixType.values()) {
            assertNotNull(type.translationKey());
            assertTrue(type.translationKey().startsWith("affix.equipment_affix."));
            assertTrue(type.descriptionKey().startsWith(type.translationKey()));
        }
    }
}
