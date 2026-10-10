package com.eibrahs.tinkerstranscend.client.gui;

import com.eibrahs.tinkerstranscend.TTConfig;
import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.TinkersTranscendAttachments;
import com.eibrahs.tinkerstranscend.tinker.modifiers.handler.MagicShield;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class MagicShieldLayer implements LayeredDraw.Layer {
    private static final ResourceLocation SHIELD_FULL = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "hud/magic_shield_full");
    private static final ResourceLocation SHIELD_HALF = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "hud/magic_shield_half");
    private static final ResourceLocation SHIELD_EMPTY = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "hud/magic_shield_empty");
    private static final boolean heartStyle = TTConfig.MAGIC_SHIELD_STYLE.get();
    private static final int ICON_SIZE = 9; // 图标尺寸，通常和心形图标一致
    private static final int ICON_SPACING = 8; // 图标间距
    private static final float SHIELD_PER_ICON = 2.0f;
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!heartStyle) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui || player.isSpectator() || player.isCreative()) return;

        if (!(player.hasData(TinkersTranscendAttachments.MAGIC_SHIELD))) return;
        MagicShield shield = player.getData(TinkersTranscendAttachments.MAGIC_SHIELD);
        if (shield.isInvalid()) return;

        float value = shield.value();
        float max = shield.max();

        int totalIcons = (int) Math.min(10.0,Math.ceil(max / SHIELD_PER_ICON));   // 总图标数
        int x = guiGraphics.guiWidth() / 2 - 91;
        int y = guiGraphics.guiHeight() - mc.gui.leftHeight+10;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        for (int i = 0; i < totalIcons; i++) {
            int iconX = x + i * 8;   // 图标间距 8px

            // 这颗图标的两个半心槽位对应的护盾值
            float leftSlot  = i * SHIELD_PER_ICON;         // 左半心起始值
            float rightSlot = i * SHIELD_PER_ICON + 1.0f;  // 右半心起始值

            // 先画空底座
            guiGraphics.blitSprite(SHIELD_EMPTY, iconX, y, 9, 9);

            if (value >= rightSlot + 1.0f) {
                // 左、右半心都有值 → full
                guiGraphics.blitSprite(SHIELD_FULL, iconX, y, 9, 9);
            } else if (value >= rightSlot) {
                // 只有左半心有值 → half
                guiGraphics.blitSprite(SHIELD_HALF, iconX, y, 9, 9);
            }
            // 否则什么都不画，就保留 EMPTY 底座
        }
        if (shield.max()>20.0f) {
            guiGraphics.drawString(mc.font, String.format("%.0f/%.0f", shield.value(), shield.max()), x-30, y, 0x4988d7, true);
        }


        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        //mc.gui.leftHeight += 9 + 1;
    }
}