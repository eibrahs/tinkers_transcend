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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class MagicShieldBarLayer implements LayeredDraw.Layer {
    private static final ResourceLocation SHIELD_BAR = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "hud/magic_shield_bar");
    private static final int BAR_WEIGHT = 130;
    private static final int BAR_HEIGHT = 30;
    private static final boolean heartStyle = TTConfig.MAGIC_SHIELD_STYLE.get();
    private static float ratio = 1.0f;
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (heartStyle) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui || player.isSpectator() || player.isCreative()) return;

        if (!(player.hasData(TinkersTranscendAttachments.MAGIC_SHIELD))) return;
        MagicShield shield = player.getData(TinkersTranscendAttachments.MAGIC_SHIELD);
        if (shield.isInvalid()) return;

        float value = shield.value();
        float max = shield.max();

        int x = guiGraphics.guiWidth() / 2 - 91 - 25;
        int y = guiGraphics.guiHeight() - mc.gui.leftHeight-BAR_HEIGHT+14;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        int barX = x+33,barY = y+10,barWidth = 94,barHeight = 7;
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0x80000000);

        float newRatio = shield.max() > 0 ? shield.value() / shield.max() : 0;
        ratio = Mth.lerp(0.2f,ratio,newRatio);
        int fillWidth = (int) (barWidth * ratio);
        if (fillWidth > 0) {
            guiGraphics.fill(barX, barY, barX + fillWidth, barY + barHeight, 0x8000faff);
        }
        guiGraphics.blitSprite(SHIELD_BAR, x, y, BAR_WEIGHT, BAR_HEIGHT);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        mc.gui.leftHeight += BAR_HEIGHT + 1;
    }
}
