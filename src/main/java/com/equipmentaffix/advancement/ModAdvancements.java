package com.equipmentaffix.advancement;

import com.equipmentaffix.EquipmentAffixMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Small helper for custom advancements. All of them use the
 * {@code minecraft:impossible} trigger and are awarded from gameplay code.
 */
public final class ModAdvancements {
    public static final String CRITERION = "code_trigger";

    private ModAdvancements() {
    }

    public static void award(ServerPlayer player, String id) {
        if (player == null || player.getServer() == null) {
            return;
        }
        Advancement advancement = player.getServer().getAdvancements()
                .getAdvancement(new ResourceLocation(EquipmentAffixMod.MOD_ID, id));
        if (advancement != null) {
            player.getAdvancements().award(advancement, CRITERION);
        }
    }
}
