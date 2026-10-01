package com.eibrahs.tinkerstranscend.client.screen;

import com.eibrahs.tinkerstranscend.block.menu.ChromaticAnvilMenu;
import com.eibrahs.tinkerstranscend.network.RenameItemPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class ChromaticAnvilScreen extends AbstractContainerScreen<ChromaticAnvilMenu> {
    private EditBox nameInput;
    private ItemStack lastInputStack = ItemStack.EMPTY;
    public ChromaticAnvilScreen(ChromaticAnvilMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("tinkers_transcend", "textures/gui/crafting/background/chromatic_anvil.png");

    @Override
    protected void init() {
        super.init();
        // 创建输入框，位置根据你的 GUI 布局调整
        this.nameInput = new EditBox(this.font, this.leftPos + 60, this.topPos + 19, 108, 16, Component.literal("name"));
        // 设置初始值（可选：从 Menu 读取当前物品名）
        this.nameInput.setValue(this.menu.getCurrentName());
        this.addRenderableWidget(this.nameInput);
        this.nameInput.setResponder(text -> {
            // 这里可以调用你的网络发送方法
            PacketDistributor.sendToServer(new RenameItemPacket(this.menu.containerId, text));
        });
    }
    @Override
    protected void containerTick() {
        super.containerTick();
        // 每 tick 检查左侧槽位物品是否变化
        ItemStack current = this.menu.getSlot(0).getItem();
        if (current.isEmpty()) {
            this.nameInput.setValue("");
            lastInputStack = ItemStack.EMPTY;
            return;
        }
        if (lastInputStack.isEmpty()||!ItemStack.matches(current, lastInputStack)) {
            lastInputStack = current.copy();
            syncInputFromSlot();
        }
    }

    private void syncInputFromSlot() {
        ItemStack input = this.menu.getSlot(0).getItem();
        if (!input.isEmpty() && input.has(DataComponents.CUSTOM_NAME)) {
            this.nameInput.setValue(input.get(DataComponents.CUSTOM_NAME).getString());
        } else if (!input.isEmpty()) {
            this.nameInput.setValue(input.getHoverName().getString());
        } else {
            this.nameInput.setValue("");
        }
    }
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // leftPos 和 topPos 是基类根据图像尺寸自动算好的左上角坐标
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

    }
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(this.font, this.title, 87, 2, 0x404040);
    }
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
            renderTooltip(
                    graphics,
                    mouseX,
                    mouseY
            );
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        // 当输入框聚焦时，拦截 E 键
        if (this.nameInput != null && this.nameInput.isFocused()
            && (
                this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)
                ||this.minecraft.options.keyDrop.isActiveAndMatches(mouseKey)
                ||this.minecraft.options.keySwapOffhand.isActiveAndMatches(mouseKey)
                ||hotbarPressed(mouseKey)
            )
        ) {
            return true; // 吞掉按键，阻止关闭 GUI
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    private boolean hotbarPressed(InputConstants.Key mouseKey){
        for(KeyMapping k: this.minecraft.options.keyHotbarSlots){
            if (k.isActiveAndMatches(mouseKey)) return true;
        }
        return false;
    }
}
