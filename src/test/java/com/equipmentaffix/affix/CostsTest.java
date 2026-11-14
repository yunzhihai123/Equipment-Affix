package com.equipmentaffix.affix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CostsTest {
    @Test
    void experienceCostGrowsExponentiallyAndStopsAtMaxLevel() {
        assertEquals(6, Costs.xpCost(0));
        assertEquals(7, Costs.xpCost(1));
        assertEquals(8, Costs.xpCost(2));
        assertEquals(9, Costs.xpCost(3));
        assertEquals(73, Costs.xpCost(19));
        assertEquals(0, Costs.xpCost(20));
        assertEquals(0, Costs.xpCost(99));

        for (int level = 0; level < Costs.MAX_LEVEL - 1; level++) {
            assertTrue(Costs.xpCost(level + 1) >= Costs.xpCost(level));
        }
    }

    @Test
    void breakthroughsHappenEveryFourLevels() {
        for (int level = 0; level < Costs.MAX_LEVEL; level++) {
            boolean expected = level == 3 || level == 7 || level == 11 || level == 15 || level == 19;
            assertEquals(expected, Costs.isBreakthrough(level), "level " + level);
        }
        assertFalse(Costs.isBreakthrough(-1));
        assertFalse(Costs.isBreakthrough(20));
    }
}
