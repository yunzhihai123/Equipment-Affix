package com.equipmentaffix.affix;

import com.equipmentaffix.EquipmentAffixMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The four equipment families this mod understands. Tags are the primary
 * compatibility mechanism; vanilla classes are only a fallback for items that
 * do not declare a tag.
 */
public enum AffixCategory {
    MELEE("melee"),
    RANGED("ranged"),
    TOOL("tools"),
    ARMOR("armor");

    private final String tagPath;
    private TagKey<Item> tag;

    AffixCategory(String tagPath) {
        this.tagPath = tagPath;
    }

    public String tagPath() {
        return this.tagPath;
    }

    public TagKey<Item> tag() {
        if (this.tag == null) {
            this.tag = ItemTags.create(
                    new ResourceLocation(EquipmentAffixMod.MOD_ID, "affixable/" + this.tagPath));
        }
        return this.tag;
    }

    public boolean matches(ItemStack stack) {
        return !stack.isEmpty() && stack.is(this.tag());
    }
}
