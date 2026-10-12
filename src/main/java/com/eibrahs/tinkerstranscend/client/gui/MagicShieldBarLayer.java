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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = TinkersTranscend.MODID, value = Dist.CLIENT)
public class MagicShieldBarLayer implements LayeredDraw.Layer {
    private static final ResourceLocation SHIELD_BAR = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "hud/magic_shield_bar");
    private static final ResourceLocation SHIELD_BAR1 = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "hud/magic_shield_bar1");
    private static final ResourceLocation SHIELD_BAR2 = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "hud/magic_shield_bar2");

    private static final float BAR_SCALE = 0.6f;
    private static final int BAR_WEIGHT = (int) (219.0f * BAR_SCALE) ;
    private static final int BAR_HEIGHT = (int) (50.0f * BAR_SCALE);
    private static int heartStyle = 0;
    private static float ratio = 1.0f;
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (heartStyle==0) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui || player.isSpectator() || player.isCreative()) return;

        if (!(player.hasData(TinkersTranscendAttachments.MAGIC_SHIELD))) return;
        MagicShield shield = player.getData(TinkersTranscendAttachments.MAGIC_SHIELD);
        if (shield.isInvalid()) return;

        float value = shield.value();
        float max = shield.max();

        int x = guiGraphics.guiWidth() / 2 - 91 - (int)(48 * BAR_SCALE);
        int y = guiGraphics.guiHeight() - mc.gui.leftHeight-BAR_HEIGHT+(int)(2 * BAR_SCALE)+10;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        guiGraphics.blitSprite(SHIELD_BAR, x, y, BAR_WEIGHT, BAR_HEIGHT);

        int barX = x+(int)(52 * BAR_SCALE),barY = y+(int) (24 * BAR_SCALE),barWidth = (int) (156 * BAR_SCALE),barHeight = (int) (17 * BAR_SCALE);
        float newRatio = shield.max() > 0 ? shield.value() / shield.max() : 0;
        ratio = Mth.lerp(0.2f,ratio,newRatio);
        int fillWidth = (int) (barWidth * ratio);
        if (fillWidth > 0) {
            guiGraphics.blitSprite(SHIELD_BAR1,barWidth,barHeight,0,0,barX,barY,fillWidth,barHeight);
        }

        int hh = mc.font.lineHeight/2;
         String text = String.format("%.1f/%.1f", shield.value(), shield.max());
        int hw = mc.font.width(text)/2;
        guiGraphics.drawString(mc.font,text , x+75-hw, y+17-hh, 0xFFFFFF, true);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        mc.gui.leftHeight += BAR_HEIGHT + 1;
    }
    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == TTConfig.CLIENT_SPEC) {
            heartStyle = TTConfig.MAGIC_SHIELD_STYLE.getAsInt();
        }
    }
    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == TTConfig.CLIENT_SPEC) {
            heartStyle = TTConfig.MAGIC_SHIELD_STYLE.getAsInt();
        }
    }
}
