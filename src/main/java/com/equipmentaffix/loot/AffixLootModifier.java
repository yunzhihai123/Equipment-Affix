package com.equipmentaffix.loot;

import com.equipmentaffix.Config;
import com.equipmentaffix.affix.AffixData;
import com.equipmentaffix.affix.AffixRoller;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Adds affixes to generated equipment. Compatibility is entirely tag/class
 * driven, so modded gear that fits a vanilla category participates automatically.
 */
public class AffixLootModifier extends LootModifier {
    public static final Codec<AffixLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance).apply(instance, AffixLootModifier::new));

    public AffixLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generated, LootContext context) {
        double chance = Config.lootChance();
        for (ItemStack stack : generated) {
            if (stack.isEmpty() || !AffixData.isAffixable(stack) || AffixData.hasAffixes(stack)) {
                continue;
            }
            if (context.getRandom().nextDouble() < chance) {
                AffixRoller.applyLoot(stack, context.getRandom(), context.getLuck());
            }
        }
        return generated;
    }

    @Override
    public @NotNull Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
