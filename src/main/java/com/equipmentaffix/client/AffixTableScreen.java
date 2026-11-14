package com.equipmentaffix.client;

import com.equipmentaffix.EquipmentAffixMod;
import com.equipmentaffix.block.AffixTableBlockEntity;
import com.equipmentaffix.block.TableKind;
import com.equipmentaffix.menu.AffixTableMenu;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;

/**
 * Vanilla-proportioned 176x166 screen. Textures live under
 * textures/gui/upgrade_table.png and textures/gui/reset_table.png.
 */
public class AffixTableScreen extends AbstractContainerScreen<AffixTableMenu> {
    private static final int BAR_X = 34;
    private static final int BAR_Y = 64;
    private static final int BAR_WIDTH = 108;
    private static final int BAR_HEIGHT = 5;
    private static final int TEXT_X = 8;
    private static final int LEVEL_Y = 23;
    private static final int STATUS_Y = 32;
    private static final int ONE_KEY_X = 118;
    private static final int ONE_KEY_Y = 21;
    private static final int ONE_KEY_SIZE = 10;

    /**
     * The server is the source of truth, but the button can acknowledge a click
     * one render before the ContainerData packet arrives. Keep a local visual
     * mirror so the tick is immediate and never depends on packet timing.
     */
    private boolean oneKeyVisual;
    private int lastServerOneKey = -1;

    public AffixTableScreen(AffixTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, Component.translatable(menu.getKind().translationKey()));
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    private ResourceLocation texture() {
        TableKind kind = this.menu.getKind();
        return new ResourceLocation(EquipmentAffixMod.MOD_ID, "textures/gui/" + kind.id() + ".png");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(this.texture(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        // Draw the slot wells from the actual Slot coordinates instead of baking
        // them into the background. This guarantees the art and the hitboxes can
        // never drift apart (including under high GUI scales / modded atlases).
        for (Slot slot : this.menu.slots) {
            int x = this.leftPos + slot.x - 1;
            int y = this.topPos + slot.y - 1;
            graphics.fill(x, y, x + 18, y + 18, 0xFF8B8B8B);
            graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF373737);
        }

        ContainerData data = this.menu.getData();
        int level = data.get(0);
        int filled = Math.round(BAR_WIDTH * Math.min(1.0F, level / 20.0F));
        if (filled > 0) {
            int color = this.menu.getKind() == TableKind.RESET ? 0xFF3FB6A8 : 0xFF8A5BD6;
            graphics.fill(this.leftPos + BAR_X, this.topPos + BAR_Y,
                    this.leftPos + BAR_X + filled, this.topPos + BAR_Y + BAR_HEIGHT, color);
        }

        this.renderArrow(graphics);
        this.renderOneKey(graphics);
    }

    /** Arrow between the material slot and the result slot, positioned by code. */
    private void renderArrow(GuiGraphics graphics) {
        int x = this.leftPos + 100;
        int y = this.topPos + 48;
        int color = 0xFF6A6A6A;
        graphics.fill(x, y + 3, x + 11, y + 6, color);
        graphics.fill(x + 8, y + 1, x + 11, y + 8, color);
        graphics.fill(x + 11, y + 3, x + 14, y + 6, color);
    }

    private void renderOneKey(GuiGraphics graphics) {
        if (this.menu.getKind() != TableKind.UPGRADE) {
            return;
        }
        int x = this.leftPos + ONE_KEY_X;
        int y = this.topPos + ONE_KEY_Y;
        int serverOneKey = this.menu.getData().get(4);
        if (serverOneKey != this.lastServerOneKey) {
            this.lastServerOneKey = serverOneKey;
            this.oneKeyVisual = serverOneKey != 0;
        }

        graphics.fill(x, y, x + ONE_KEY_SIZE, y + ONE_KEY_SIZE, 0xFF8B8B8B);
        graphics.fill(x + 1, y + 1, x + ONE_KEY_SIZE - 1, y + ONE_KEY_SIZE - 1, 0xFF373737);
        if (this.oneKeyVisual) {
            // Draw an actual tick mark instead of filling the box, so the
            // checked state is readable even at low GUI scales.
            int check = 0xFFB9A2FF;
            graphics.fill(x + 2, y + 5, x + 3, y + 7, check);
            graphics.fill(x + 3, y + 6, x + 4, y + 8, check);
            graphics.fill(x + 4, y + 7, x + 5, y + 8, check);
            graphics.fill(x + 5, y + 5, x + 6, y + 8, check);
            graphics.fill(x + 6, y + 3, x + 7, y + 6, check);
            graphics.fill(x + 7, y + 2, x + 8, y + 5, check);
        }
        graphics.drawString(this.font, Component.translatable("gui.equipment_affix.one_key"),
                x + ONE_KEY_SIZE + 3, y + 2, 0x404040, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.menu.getKind() == TableKind.UPGRADE) {
            int x = this.leftPos + ONE_KEY_X;
            int y = this.topPos + ONE_KEY_Y;
            if (mouseX >= x && mouseX < x + ONE_KEY_SIZE && mouseY >= y && mouseY < y + ONE_KEY_SIZE) {
                this.oneKeyVisual = !this.oneKeyVisual;
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX,
                this.inventoryLabelY, 0x404040, false);

        ContainerData data = this.menu.getData();
        int level = data.get(0);
        int xp = data.get(1);
        int status = data.get(2);
        String levelText = Component.translatable("gui.equipment_affix.level", level, 20).getString();
        graphics.drawString(this.font, levelText, TEXT_X, LEVEL_Y, 0x404040, false);
        if (status == AffixTableBlockEntity.Status.READY.ordinal()) {
            String cost = Component.translatable("gui.equipment_affix.cost", xp).getString();
            graphics.drawString(this.font, cost, TEXT_X, STATUS_Y, 0x3F6E3F, false);
        } else {
            String key = "gui.equipment_affix.status."
                    + AffixTableBlockEntity.Status.values()[Math.max(0,
                    Math.min(AffixTableBlockEntity.Status.values().length - 1, data.get(2)))]
                    .name().toLowerCase(Locale.ROOT);
            String statusText = Component.translatable(key).getString();
            graphics.drawString(this.font, statusText, TEXT_X, STATUS_Y, 0x8A3B3B, false);
        }
    }
}
