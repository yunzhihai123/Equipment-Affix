package com.equipmentaffix.affix;

import com.equipmentaffix.Config;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;

/**
 * Reads and writes the single {@code EquipmentAffix} compound on an item stack.
 * Keeping everything in one compound makes the data easy to inspect, copy and
 * migrate.
 */
public final class AffixData {
    public static final String ROOT = "EquipmentAffix";
    public static final int MAX_LEVEL = 20;

    private AffixData() {
    }

    public static CompoundTag root(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return null;
        }
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(ROOT, Tag.TAG_COMPOUND) ? tag.getCompound(ROOT) : null;
    }

    public static boolean hasAffixes(ItemStack stack) {
        CompoundTag root = root(stack);
        return root != null && root.contains("Affixes", Tag.TAG_LIST) && !root.getList("Affixes", Tag.TAG_COMPOUND).isEmpty();
    }

    public static int level(ItemStack stack) {
        CompoundTag root = root(stack);
        if (root == null) {
            return 0;
        }
        return Math.max(0, Math.min(MAX_LEVEL, root.getInt("Level")));
    }

    public static Rarity rarity(ItemStack stack) {
        CompoundTag root = root(stack);
        return root == null ? Rarity.COMMON : Rarity.byId(root.getString("Rarity"));
    }

    public static List<Affix> affixes(ItemStack stack) {
        CompoundTag root = root(stack);
        if (root == null) {
            return List.of();
        }
        ListTag list = root.getList("Affixes", Tag.TAG_COMPOUND);
        List<Affix> result = new ArrayList<>(list.size());
        for (int index = 0; index < list.size(); index++) {
            Affix affix = Affix.load(list.getCompound(index));
            if (affix != null) {
                result.add(affix);
            }
        }
        return result;
    }

    public static void write(ItemStack stack, List<Affix> affixes, int level, Rarity rarity) {
        CompoundTag root = new CompoundTag();
        root.putInt("Level", Math.max(0, Math.min(MAX_LEVEL, level)));
        root.putString("Rarity", rarity.id());
        ListTag list = new ListTag();
        for (Affix affix : affixes) {
            list.add(affix.save());
        }
        root.put("Affixes", list);
        stack.getOrCreateTag().put(ROOT, root);
    }

    public static void setLevel(ItemStack stack, int level) {
        CompoundTag root = root(stack);
        if (root != null) {
            root.putInt("Level", Math.max(0, Math.min(MAX_LEVEL, level)));
            stack.getOrCreateTag().put(ROOT, root);
        }
    }

    public static double sum(ItemStack stack, AffixType.Effect effect) {
        double total = 0.0D;
        for (Affix affix : affixes(stack)) {
            if (affix.type().effect() == effect) {
                total += affix.value();
            }
        }
        return total;
    }

    public static boolean hasEffect(ItemStack stack, AffixType.Effect effect) {
        for (Affix affix : affixes(stack)) {
            if (affix.type().effect() == effect) {
                return true;
            }
        }
        return false;
    }

    /** Sums an effect over all four armor slots. */
    public static double sumArmor(Player player, AffixType.Effect effect) {
        double total = 0.0D;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            total += sum(player.getItemBySlot(slot), effect);
        }
        return total;
    }

    /** Sums an effect over the item in the player's main hand. */
    public static double sumMainHand(Player player, AffixType.Effect effect) {
        return sum(player.getMainHandItem(), effect);
    }

    public static AffixCategory category(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        for (AffixCategory category : AffixCategory.values()) {
            if (stack.is(category.tag())) {
                return category;
            }
        }
        if (stack.getItem() instanceof ArmorItem || stack.getItem() instanceof ElytraItem) {
            return AffixCategory.ARMOR;
        }
        if (stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof TridentItem) {
            return AffixCategory.MELEE;
        }
        if (stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem
                || stack.getItem() instanceof ProjectileWeaponItem) {
            return AffixCategory.RANGED;
        }
        if (stack.getItem() instanceof PickaxeItem || stack.getItem() instanceof ShovelItem
                || stack.getItem() instanceof HoeItem || stack.getItem() instanceof ShearsItem
                || stack.getItem() instanceof DiggerItem) {
            return AffixCategory.TOOL;
        }
        return null;
    }

    public static boolean isAffixable(ItemStack stack) {
        if (stack.isEmpty() || Config.isBlacklisted(stack)) {
            return false;
        }
        return category(stack) != null;
    }
}
