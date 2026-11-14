package com.equipmentaffix.block;

import com.equipmentaffix.affix.AffixData;
import com.equipmentaffix.affix.AffixRoller;
import com.equipmentaffix.affix.Costs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import com.equipmentaffix.advancement.ModAdvancements;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared block entity for both workstations. Only the input and catalyst slots
 * are stored; the result is always derived from them, which makes the menu
 * duping-proof and the preview stable between client and server.
 */
public class AffixTableBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_MATERIAL = 1;
    public static final int SLOT_RESULT = 2;
    public static final int SLOT_COUNT = 3;

    private static final int[] SLOTS = {SLOT_INPUT, SLOT_MATERIAL};

    public enum Status {
        NOT_AFFIXABLE,
        READY,
        MISSING_MATERIAL,
        MAX_LEVEL,
        NOT_AFFIXED
    }

    public enum Action {
        NONE,
        INITIALIZE,
        UPGRADE,
        BREAKTHROUGH,
        RESET
    }

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private boolean oneKey;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return AffixTableBlockEntity.this.getDataValue(index);
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public AffixTableBlockEntity(BlockPos pos, BlockState state) {
        super(com.equipmentaffix.registry.ModBlockEntities.AFFIX_TABLE.get(), pos, state);
    }

    public TableKind kind() {
        return this.getBlockState().getBlock() instanceof AffixTableBlock table
                ? table.kind() : TableKind.UPGRADE;
    }

    public ContainerData data() {
        return this.data;
    }

    public ItemStack getInput() {
        return this.items.get(SLOT_INPUT);
    }

    public ItemStack getMaterial() {
        return this.items.get(SLOT_MATERIAL);
    }

    public int getAffixLevel() {
        return AffixData.level(this.getInput());
    }

    public boolean oneKey() {
        return this.oneKey;
    }

    public void toggleOneKey() {
        this.oneKey = !this.oneKey;
        this.refreshResult();
        this.setChanged();
    }

    /** True only when the one-key flag can affect the current upgrade action. */
    private boolean oneKeyActive() {
        return this.oneKey && this.kind() == TableKind.UPGRADE && this.action() == Action.UPGRADE;
    }

    /** Number of normal upgrades before the next breakthrough, including 0 at a breakthrough. */
    private int oneKeySteps() {
        int steps = 0;
        int level = this.getAffixLevel();
        while (level < Costs.MAX_LEVEL && !Costs.isBreakthrough(level)) {
            steps++;
            level++;
        }
        return steps;
    }

    private int oneKeyXpCost() {
        int total = 0;
        int level = this.getAffixLevel();
        for (int i = 0; i < this.oneKeySteps(); i++) {
            total += Costs.xpCost(level + i);
        }
        return total;
    }

    public Action action() {
        ItemStack input = this.getInput();
        if (input.isEmpty() || !AffixData.isAffixable(input)) {
            return Action.NONE;
        }
        if (this.kind() == TableKind.RESET) {
            return AffixData.hasAffixes(input) ? Action.RESET : Action.NONE;
        }
        if (!AffixData.hasAffixes(input)) {
            return Action.INITIALIZE;
        }
        if (this.getAffixLevel() >= Costs.MAX_LEVEL) {
            return Action.NONE;
        }
        return Costs.isBreakthrough(this.getAffixLevel()) ? Action.BREAKTHROUGH : Action.UPGRADE;
    }

    public Status status() {
        ItemStack input = this.getInput();
        if (input.isEmpty() || !AffixData.isAffixable(input)) {
            return Status.NOT_AFFIXABLE;
        }
        if (this.kind() == TableKind.RESET) {
            if (!AffixData.hasAffixes(input)) {
                return Status.NOT_AFFIXED;
            }
            return this.hasMaterial() ? Status.READY : Status.MISSING_MATERIAL;
        }
        if (!AffixData.hasAffixes(input)) {
            return this.hasMaterial() ? Status.READY : Status.MISSING_MATERIAL;
        }
        if (this.getAffixLevel() >= Costs.MAX_LEVEL) {
            return Status.MAX_LEVEL;
        }
        return this.hasMaterial() ? Status.READY : Status.MISSING_MATERIAL;
    }

    public ItemStack requiredMaterial() {
        if (this.kind() == TableKind.RESET) {
            return Costs.materialForReset();
        }
        if (!AffixData.hasAffixes(this.getInput())) {
            return Costs.materialForInitialization();
        }
        if (this.oneKeyActive()) {
            ItemStack material = Costs.materialForUpgrade(this.getAffixLevel());
            material.setCount(this.oneKeySteps());
            return material;
        }
        return Costs.materialForUpgrade(this.getAffixLevel());
    }

    public int requiredXp() {
        if (this.kind() == TableKind.RESET) {
            return Costs.RESET_XP;
        }
        if (!AffixData.hasAffixes(this.getInput())) {
            return Costs.INITIALIZE_XP;
        }
        if (this.oneKeyActive()) {
            return this.oneKeyXpCost();
        }
        return Costs.xpCost(this.getAffixLevel());
    }

    public boolean hasMaterial() {
        ItemStack required = this.requiredMaterial();
        ItemStack material = this.getMaterial();
        return !required.isEmpty() && ItemStack.isSameItemSameTags(required, material)
                && material.getCount() >= required.getCount();
    }

    public boolean canProcess(Player player) {
        if (player == null || this.action() == Action.NONE || !this.hasMaterial()) {
            return false;
        }
        return player.getAbilities().instabuild || player.experienceLevel >= this.requiredXp();
    }

    /**
     * Deterministic preview. The seed depends only on the input stack contents,
     * so the client and server compute the same result before any items are
     * consumed.
     */
    public ItemStack getPreview() {
        if (this.action() == Action.NONE) {
            return ItemStack.EMPTY;
        }
        ItemStack input = this.getInput();
        long seed = 1125899906842597L;
        var itemKey = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(input.getItem());
        seed = 31L * seed + (itemKey == null ? 0 : itemKey.hashCode());
        seed = 31L * seed + input.getDamageValue();
        seed = 31L * seed + (input.getTag() == null ? 0 : input.getTag().hashCode());
        seed = 31L * seed + (AffixData.hasAffixes(input) ? 1 : 0);
        seed = 31L * seed + this.kind().ordinal();
        RandomSource random = RandomSource.create(seed);

        ItemStack result = input.copy();
        result.setCount(1);
        switch (this.action()) {
            case INITIALIZE -> AffixRoller.applyInitialized(result, random);
            case UPGRADE -> {
                int target = this.getAffixLevel() + 1;
                if (this.oneKeyActive()) {
                    target = this.getAffixLevel() + this.oneKeySteps();
                }
                AffixData.setLevel(result, target);
            }
            case BREAKTHROUGH -> {
                AffixData.setLevel(result, this.getAffixLevel() + 1);
                AffixRoller.applyBreakthrough(result, random);
            }
            case RESET -> AffixRoller.applyReset(result, random);
            default -> {
                return ItemStack.EMPTY;
            }
        }
        return result;
    }

    /**
     * The result slot is cached in the container so vanilla Slot/hoppers can
     * handle it; it is never persisted and is always recomputed from input and
     * catalyst.
     */
    public void refreshResult() {
        this.items.set(SLOT_RESULT, this.getPreview());
    }

    public ItemStack process(Player player) {
        if (!this.canProcess(player)) {
            return ItemStack.EMPTY;
        }
        Action action = this.action();
        ItemStack result = this.getPreview();
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack required = this.requiredMaterial();
        int xp = this.requiredXp();

        ItemStack input = this.getInput();
        input.shrink(1);
        if (input.isEmpty()) {
            this.items.set(SLOT_INPUT, ItemStack.EMPTY);
        }

        ItemStack material = this.getMaterial();
        material.shrink(required.getCount());
        if (material.isEmpty()) {
            this.items.set(SLOT_MATERIAL, ItemStack.EMPTY);
        }

        if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-xp);
        }
        this.refreshResult();
        this.setChanged();

        if (player instanceof ServerPlayer serverPlayer) {
            if (AffixData.hasAffixes(result)) {
                ModAdvancements.award(serverPlayer, "first_affix");
            }
            if (AffixData.level(result) >= 1) {
                ModAdvancements.award(serverPlayer, "first_upgrade");
            }
            if (action == Action.BREAKTHROUGH) {
                ModAdvancements.award(serverPlayer, "breakthrough");
            }
            if (this.kind() == TableKind.RESET) {
                ModAdvancements.award(serverPlayer, "reforge");
            }
            if (AffixData.level(result) >= Costs.MAX_LEVEL) {
                ModAdvancements.award(serverPlayer, "max_level");
            }
        }
        return result;
    }

    private int getDataValue(int index) {
        return switch (index) {
            case 0 -> this.getAffixLevel();
            case 1 -> this.requiredXp();
            case 2 -> this.status().ordinal();
            case 3 -> this.action() != Action.NONE && this.hasMaterial() ? 1 : 0;
            case 4 -> this.oneKey ? 1 : 0;
            default -> 0;
        };
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < SLOT_COUNT ? this.items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = this.items.get(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result;
        if (stack.getCount() <= amount) {
            this.items.set(slot, ItemStack.EMPTY);
            result = stack;
        } else {
            result = stack.split(amount);
        }
        if (slot == SLOT_INPUT || slot == SLOT_MATERIAL) {
            this.refreshResult();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = this.items.get(slot);
        this.items.set(slot, ItemStack.EMPTY);
        if (slot == SLOT_INPUT || slot == SLOT_MATERIAL) {
            this.refreshResult();
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        if (slot == SLOT_INPUT || slot == SLOT_MATERIAL) {
            this.refreshResult();
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == SLOT_INPUT) {
            return AffixData.isAffixable(stack);
        }
        return slot == SLOT_MATERIAL;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            this.items.set(i, ItemStack.EMPTY);
        }
        this.refreshResult();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.kind().translationKey());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new com.equipmentaffix.menu.AffixTableMenu(id, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag input = new CompoundTag();
        this.getInput().save(input);
        tag.put("Input", input);
        CompoundTag material = new CompoundTag();
        this.getMaterial().save(material);
        tag.put("Material", material);
        tag.putBoolean("OneKey", this.oneKey);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items.set(SLOT_INPUT, ItemStack.of(tag.getCompound("Input")));
        this.items.set(SLOT_MATERIAL, ItemStack.of(tag.getCompound("Material")));
        this.oneKey = tag.getBoolean("OneKey");
        this.refreshResult();
    }
}
