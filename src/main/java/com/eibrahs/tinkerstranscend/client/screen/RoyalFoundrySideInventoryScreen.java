package com.eibrahs.tinkerstranscend.client.screen;

import com.eibrahs.tinkerstranscend.block.entity.controller.RoyalFoundryBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.smeltery.client.screen.HeatingStructureScreen;
import slimeknights.tconstruct.tables.client.inventory.module.SideInventoryScreen;
import slimeknights.tconstruct.tables.menu.module.SideInventoryContainer;

public class RoyalFoundrySideInventoryScreen  extends SideInventoryScreen<RoyalFoundryScreen, SideInventoryContainer<? extends RoyalFoundryBlockEntity>> {
    public static final ResourceLocation SLOT_LOCATION;

    public RoyalFoundrySideInventoryScreen(RoyalFoundryScreen parent, SideInventoryContainer<? extends RoyalFoundryBlockEntity> container, Inventory playerInventory, int slotCount, int columns) {
        super(parent, container, playerInventory,Component.empty(), slotCount, columns);
        this.slot = new ScalableElementScreen(SLOT_LOCATION, 0, 238, 22, 18, 256, 256);
        this.slotEmpty = new ScalableElementScreen(SLOT_LOCATION, 22, 238, 22, 18, 256, 256);
        this.yOffset = 4;
    }
    protected boolean shouldDrawName() {
        return false;
    }

    protected void updateSlots() {
        this.xOffset += 4;
        super.updateSlots();
        this.xOffset -= 4;
    }

    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        if ((this.parent).melting != null) {
            (this.parent).melting.drawHeatTooltips(graphics, mouseX, mouseY);
        }

    }
    static {
        SLOT_LOCATION = HeatingStructureScreen.BACKGROUND;
    }
}
