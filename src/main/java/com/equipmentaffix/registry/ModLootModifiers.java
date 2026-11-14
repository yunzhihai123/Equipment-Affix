package com.equipmentaffix.registry;

import com.equipmentaffix.EquipmentAffixMod;
import com.equipmentaffix.loot.AffixLootModifier;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
                    EquipmentAffixMod.MOD_ID);

    public static final RegistryObject<Codec<AffixLootModifier>> AFFIX_GEAR = SERIALIZERS.register(
            "affix_gear", () -> AffixLootModifier.CODEC);

    private ModLootModifiers() {
    }
}
