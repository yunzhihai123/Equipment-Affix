package com.equipmentaffix.menu;

import com.equipmentaffix.affix.AffixData;
import com.equipmentaffix.block.AffixTableBlockEntity;
import com.equipmentaffix.block.TableKind;
import com.equipmentaffix.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * One menu implementation for both tables. The result slot is backed by a
 * computed slot in the block entity, so the client always sees the same preview
 * the server will produce.
 */
public class AffixTableMenu extends AbstractContainerMenu {
    private final TableKind kind;
    private final AffixTableBlockEntity table;
    private final Container container;
    private final ContainerData data;

    public AffixTableMenu(int id, Inventory inventory, AffixTableBlockEntity table) {
        this(id, inventory, table, table.kind());
    }

    private AffixTableMenu(int id, Inventory inventory, AffixTableBlockEntity table, TableKind kind) {
        super(menuType(kind), id);
        this.kind = kind;
        this.table = table;
        this.container = table != null ? table : new SimpleContainer(AffixTableBlockEntity.SLOT_COUNT);
        this.data = table != null ? table.data() : new SimpleContainerData(5);
        this.addSlots(inventory);
    }

    public AffixTableMenu(int id, Inventory inventory, FriendlyByteBuf data, TableKind kind) {
        this(id, inventory, resolve(inventory, data.readBlockPos()), kind);
    }

    private static AffixTableBlockEntity resolve(Inventory inventory, BlockPos pos) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(pos);
        return blockEntity instanceof AffixTableBlockEntity table ? table : null;
    }

    private static MenuType<AffixTableMenu> menuType(TableKind kind) {
        return kind == TableKind.RESET ? ModMenus.RESET_TABLE.get() : ModMenus.UPGRADE_TABLE.get();
    }

    private void addSlots(Inventory inventory) {
        this.addDataSlots(this.data);

        this.addSlot(new Slot(this.container, AffixTableBlockEntity.SLOT_INPUT, 26, 42) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return AffixData.isAffixable(stack);
            }
        });
        this.addSlot(new Slot(this.container, AffixTableBlockEntity.SLOT_MATERIAL, 80, 42));
        this.addSlot(new AffixResultSlot(AffixTableBlockEntity.SLOT_RESULT, 134, 42));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    public TableKind getKind() {
        return this.kind;
    }

    public AffixTableBlockEntity getTable() {
        return this.table;
    }

    public ContainerData getData() {
        return this.data;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.table != null && this.table.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && this.kind == TableKind.UPGRADE && this.table != null) {
            this.table.toggleOneKey();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == AffixTableBlockEntity.SLOT_RESULT) {
            if (!this.moveItemStackTo(stack, 3, 39, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, original);
        } else if (index == AffixTableBlockEntity.SLOT_INPUT || index == AffixTableBlockEntity.SLOT_MATERIAL) {
            if (!this.moveItemStackTo(stack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
        } else if (AffixData.isAffixable(stack)) {
            if (!this.moveItemStackTo(stack, AffixTableBlockEntity.SLOT_INPUT, AffixTableBlockEntity.SLOT_INPUT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, AffixTableBlockEntity.SLOT_MATERIAL, AffixTableBlockEntity.SLOT_MATERIAL + 1, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    private class AffixResultSlot extends Slot {
        AffixResultSlot(int index, int x, int y) {
            super(AffixTableMenu.this.container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return AffixTableMenu.this.table != null && AffixTableMenu.this.table.canProcess(player);
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            AffixTableBlockEntity table = AffixTableMenu.this.table;
            if (table != null && !player.level().isClientSide) {
                table.process(player);
            }
            super.onTake(player, stack);
        }

        @Override
        public void set(ItemStack stack) {
            // The result is derived from the input and catalyst, never pushed in.
        }

        @Override
        public void setChanged() {
            // No persistent change for the derived slot.
        }
    }
}
