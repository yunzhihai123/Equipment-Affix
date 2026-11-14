package com.equipmentaffix.event;

import com.equipmentaffix.Config;
import com.equipmentaffix.EquipmentAffixMod;
import com.equipmentaffix.affix.Affix;
import com.equipmentaffix.affix.AffixData;
import com.equipmentaffix.affix.AffixRoller;
import com.equipmentaffix.affix.AffixType;
import com.equipmentaffix.affix.Costs;
import com.equipmentaffix.affix.Rarity;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * All gameplay hooks for affixes: combat, tools, armor, tooltips, crafting and
 * loot. Kept in one class so the event surface is easy to audit.
 */
@Mod.EventBusSubscriber(modid = EquipmentAffixMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CombatEvents {
    private static final String PROJECTILE_TAG = "EquipmentAffixProjectile";
    private static final float VANILLA_CRIT_MULTIPLIER = 1.5F;

    private CombatEvents() {
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        ItemStack weapon = player.getMainHandItem();
        double chance = AffixData.sum(weapon, AffixType.Effect.CRIT_CHANCE);
        double critDamage = AffixData.sum(weapon, AffixType.Effect.CRIT_DAMAGE);
        if (chance <= 0.0D && critDamage <= 0.0D && !event.isVanillaCritical()) {
            return;
        }
        boolean critical = event.isVanillaCritical()
                || (chance > 0.0D && player.getRandom().nextDouble() * 100.0D < chance);
        if (!critical) {
            return;
        }
        event.setResult(Event.Result.ALLOW);
        event.setDamageModifier(VANILLA_CRIT_MULTIPLIER
                * (1.0F + (float) (critDamage / 100.0D)));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            double reduction = AffixData.sumArmor(player, AffixType.Effect.DAMAGE_REDUCTION);
            if (reduction > 0.0D) {
                reduction = Math.min(60.0D, reduction);
                event.setAmount(event.getAmount() * (float) (1.0D - reduction / 100.0D));
            }
        }

        DamageSource source = event.getSource();
        Entity direct = source.getDirectEntity();
        if (!(direct instanceof AbstractArrow arrow)) {
            return;
        }
        CompoundTag data = arrow.getPersistentData().getCompound(PROJECTILE_TAG);
        if (data.isEmpty()) {
            return;
        }
        double damage = data.getDouble("Damage");
        double chance = data.getDouble("CritChance");
        double critDamage = data.getDouble("CritDamage");
        boolean critical = chance > 0.0D && arrow.level().random.nextDouble() * 100.0D < chance;
        double multiplier = 1.0D + damage / 100.0D;
        if (critical) {
            multiplier *= VANILLA_CRIT_MULTIPLIER * (1.0D + critDamage / 100.0D);
        }
        event.setAmount((float) (event.getAmount() * multiplier));
        if (critical && arrow.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.CRIT, arrow.getX(), arrow.getY(), arrow.getZ(),
                    10, 0.25D, 0.25D, 0.25D, 0.0D);
            level.playSound(null, arrow.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        LivingEntity victim = event.getEntity();
        LivingEntity attacker = victim.getLastHurtByMob();
        if (!(attacker instanceof Player player)) {
            return;
        }
        double bonus = AffixData.sum(player.getMainHandItem(), AffixType.Effect.KNOCKBACK);
        if (bonus <= 0.0D) {
            return;
        }
        double factor = 1.0D + bonus / 100.0D;
        event.setStrength((float) (event.getStrength() * factor));
        event.setRatioX(event.getRatioX() * factor);
        event.setRatioZ(event.getRatioZ() * factor);
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        double bonus = AffixData.sum(player.getMainHandItem(), AffixType.Effect.MINING_SPEED);
        if (bonus > 0.0D) {
            event.setNewSpeed((float) (event.getNewSpeed() * (1.0D + bonus / 100.0D)));
        }
    }

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        if (event.getAmount() <= 0) {
            return;
        }
        Player player = event.getEntity();
        double bonus = AffixData.sum(player.getMainHandItem(), AffixType.Effect.XP_BONUS);
        if (bonus > 0.0D) {
            event.setAmount((int) Math.round(event.getAmount() * (1.0D + bonus / 100.0D)));
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!Config.autoDetectCrafted()) {
            return;
        }
        ItemStack crafted = event.getCrafting();
        if (crafted.isEmpty() || AffixData.hasAffixes(crafted) || !AffixData.isAffixable(crafted)) {
            return;
        }
        AffixRoller.applyCrafted(crafted, event.getEntity().getRandom());
    }

    /**
     * Creative players can pull arbitrary vanilla or modded gear out of the
     * creative inventory. Apply the same starter roll the crafting hook gives
     * survival players, without touching items that already have affixes.
     */
    @SubscribeEvent
    public static void onCreativeTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide
                || !player.isCreative() || !Config.affixCreativeItems()
                || player.tickCount % 20 != 0) {
            return;
        }
        boolean changed = false;
        for (ItemStack stack : player.getInventory().items) {
            changed |= affixCreativeStack(stack, player);
        }
        for (ItemStack stack : player.getInventory().armor) {
            changed |= affixCreativeStack(stack, player);
        }
        for (ItemStack stack : player.getInventory().offhand) {
            changed |= affixCreativeStack(stack, player);
        }
        if (changed) {
            player.getInventory().setChanged();
            player.containerMenu.broadcastChanges();
        }
    }

    private static boolean affixCreativeStack(ItemStack stack, Player player) {
        if (stack.isEmpty() || AffixData.hasAffixes(stack) || !AffixData.isAffixable(stack)) {
            return false;
        }
        AffixRoller.applyCrafted(stack, player.getRandom());
        return true;
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!AffixData.hasAffixes(stack)) {
            return;
        }
        List<Component> tooltip = event.getToolTip();
        if (tooltip.isEmpty()) {
            return;
        }
        Rarity rarity = AffixData.rarity(stack);
        tooltip.set(0, tooltip.get(0).copy().withStyle(rarity.color()));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.equipment_affix.level",
                AffixData.level(stack), AffixData.MAX_LEVEL).withStyle(ChatFormatting.GRAY));
        for (Affix affix : AffixData.affixes(stack)) {
            tooltip.add(Component.translatable("tooltip.equipment_affix.affix",
                    Component.translatable(affix.type().translationKey()), affix.formattedValue())
                    .withStyle(tierColor(affix.tier())));
        }
        if (Costs.isBreakthrough(AffixData.level(stack))) {
            tooltip.add(Component.translatable("tooltip.equipment_affix.breakthrough")
                    .withStyle(ChatFormatting.GOLD));
        }
        tooltip.add(Component.translatable("tooltip.equipment_affix.hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @SubscribeEvent
    public static void onAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!AffixData.hasAffixes(stack) || !appliesToSlot(stack, event.getSlotType())) {
            return;
        }
        add(event, Attributes.ATTACK_DAMAGE,
                AffixData.sum(stack, AffixType.Effect.ATTACK_DAMAGE) / 100.0D,
                AttributeModifier.Operation.MULTIPLY_BASE, "attack_damage");

        double intervalReduction = AffixData.sum(stack, AffixType.Effect.ATTACK_SPEED);
        if (intervalReduction > 0.0D && intervalReduction < 100.0D) {
            double speedBonus = 1.0D / (1.0D - intervalReduction / 100.0D) - 1.0D;
            add(event, Attributes.ATTACK_SPEED, speedBonus,
                    AttributeModifier.Operation.MULTIPLY_BASE, "attack_speed");
        }

        Attribute reach = ForgeMod.ENTITY_REACH.get();
        add(event, reach, AffixData.sum(stack, AffixType.Effect.REACH) / 100.0D,
                AttributeModifier.Operation.MULTIPLY_BASE, "reach");
        add(event, Attributes.MAX_HEALTH,
                AffixData.sum(stack, AffixType.Effect.MAX_HEALTH),
                AttributeModifier.Operation.ADDITION, "max_health");
        add(event, Attributes.ARMOR,
                AffixData.sum(stack, AffixType.Effect.ARMOR),
                AttributeModifier.Operation.ADDITION, "armor");
        add(event, Attributes.ARMOR_TOUGHNESS,
                AffixData.sum(stack, AffixType.Effect.ARMOR_TOUGHNESS),
                AttributeModifier.Operation.ADDITION, "armor_toughness");
        add(event, Attributes.KNOCKBACK_RESISTANCE,
                AffixData.sum(stack, AffixType.Effect.KNOCKBACK_RESISTANCE) / 100.0D,
                AttributeModifier.Operation.ADDITION, "knockback_resistance");
        add(event, Attributes.MOVEMENT_SPEED,
                AffixData.sum(stack, AffixType.Effect.MOVEMENT_SPEED) / 100.0D,
                AttributeModifier.Operation.MULTIPLY_BASE, "movement_speed");
        add(event, Attributes.LUCK,
                AffixData.sum(stack, AffixType.Effect.LUCK),
                AttributeModifier.Operation.ADDITION, "luck");
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof AbstractArrow arrow)) {
            return;
        }
        if (!(arrow.getOwner() instanceof Player player) || arrow.getPersistentData().contains(PROJECTILE_TAG)) {
            return;
        }
        ItemStack weapon = player.getUseItem();
        if (!(weapon.getItem() instanceof ProjectileWeaponItem)) {
            weapon = player.getMainHandItem();
        }
        double damage = AffixData.sum(weapon, AffixType.Effect.PROJECTILE_DAMAGE);
        double speed = AffixData.sum(weapon, AffixType.Effect.PROJECTILE_SPEED);
        double chance = AffixData.sum(weapon, AffixType.Effect.CRIT_CHANCE);
        double critDamage = AffixData.sum(weapon, AffixType.Effect.CRIT_DAMAGE);
        if (damage <= 0.0D && speed <= 0.0D && chance <= 0.0D && critDamage <= 0.0D) {
            return;
        }
        CompoundTag data = new CompoundTag();
        data.putDouble("Damage", damage);
        data.putDouble("CritChance", chance);
        data.putDouble("CritDamage", critDamage);
        arrow.getPersistentData().put(PROJECTILE_TAG, data);
        if (speed > 0.0D) {
            arrow.setDeltaMovement(arrow.getDeltaMovement().scale(1.0D + speed / 100.0D));
        }
        EquipmentAffixMod.LOGGER.debug("Applied projectile affixes to arrow from {}", player.getName().getString());
    }

    /**
     * Forge fires this event once for every equipment slot the stack can occupy.
     * Adding the same modifier to all of them made vanilla print the whole
     * "when in main hand / off hand / on feet..." block for every affix line.
     * Only the slot that actually uses the item should receive affix modifiers.
     */
    private static boolean appliesToSlot(ItemStack stack, EquipmentSlot slot) {
        if (slot == null) {
            return true;
        }
        return slot == LivingEntity.getEquipmentSlotForItem(stack);
    }

    private static void add(ItemAttributeModifierEvent event, Attribute attribute, double amount,
                            AttributeModifier.Operation operation, String name) {
        if (attribute == null || amount == 0.0D) {
            return;
        }
        EquipmentSlot slot = event.getSlotType();
        String slotName = slot == null ? "any" : slot.getName();
        String key = EquipmentAffixMod.MOD_ID + ":" + name + ":" + slotName;
        UUID uuid = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
        event.addModifier(attribute, new AttributeModifier(uuid, key, amount, operation));
    }

    private static ChatFormatting tierColor(int tier) {
        return switch (tier) {
            case 1 -> ChatFormatting.GRAY;
            case 2 -> ChatFormatting.WHITE;
            case 3 -> ChatFormatting.GREEN;
            case 4 -> ChatFormatting.BLUE;
            default -> ChatFormatting.DARK_PURPLE;
        };
    }
}
