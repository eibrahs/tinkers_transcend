package com.eibrahs.tinkerstranscend.client.event;

import com.eibrahs.tinkerstranscend.TTConfig;
import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.TinkersTranscendAttachments;
import com.eibrahs.tinkerstranscend.client.shader.TinkersTranscendShaders;
import com.eibrahs.tinkerstranscend.tinker.modifiers.handler.MagicShield;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import java.lang.reflect.Method;


@EventBusSubscriber(modid = TinkersTranscend.MODID, value = Dist.CLIENT)
public class MagicShieldRendererEvent {
    private static int color = 0;
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        if (!player.hasData(TinkersTranscendAttachments.MAGIC_SHIELD)) return;
        MagicShield shield = player.getData(TinkersTranscendAttachments.MAGIC_SHIELD);
        if (shield.value()<=0) return;
        PlayerRenderer renderer = event.getRenderer();
        PlayerModel<?> model = renderer.getModel();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();
        int packedLight = event.getPackedLight();
        int packedOverlay = OverlayTexture.NO_OVERLAY;
        float partialTick = event.getPartialTick();

        poseStack.pushPose();

        float bodyRot = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        applySetupRotations(renderer, player, poseStack, bodyRot, partialTick);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        if (player.isSleeping()) {
            poseStack.translate(0.0F, 1.6, 0.0F);
        }
        float scale = 0.6f;
        float alpha = 0.5f;
        Minecraft mc = Minecraft.getInstance();
        TinkersTranscendShaders.setMagicShieldShader(color,mc.getWindow().getWidth(),mc.getWindow().getHeight());
        VertexConsumer consumer = buffer.getBuffer(
                TinkersTranscendShaders.MAGIC_SHIELD_RENDER_TYPE);
        renderPart(model.head, poseStack, consumer, packedLight, packedOverlay, alpha,0.25f*scale,0.0f,-4.0f,0.0f);
        renderPart(model.body, poseStack, consumer, packedLight, packedOverlay, alpha,0.25f*scale,0.16f*scale,0.5f*scale,0.0f,6.0f,0.0f);
        renderPart(model.rightArm, poseStack, consumer, packedLight, packedOverlay, alpha,0.5f*scale,0.16f*scale,0.5f*scale,-1.0f,4.0f,0.0f);
        renderPart(model.leftArm, poseStack, consumer, packedLight, packedOverlay, alpha,0.5f*scale,0.16f*scale,0.5f*scale,1.0f,4.0f,0.0f);
        renderPart(model.rightLeg, poseStack, consumer, packedLight, packedOverlay, alpha,0.5f*scale,0.16f*scale,0.5f*scale,-0.1f,6.0f,0.0f);
        renderPart(model.leftLeg, poseStack, consumer, packedLight, packedOverlay, alpha,0.5f*scale,0.16f*scale,0.5f*scale,0.1f,6.0f,0.0f);
        poseStack.popPose();
    }
    private static void renderPart(ModelPart part, PoseStack poseStack,
                                   VertexConsumer consumer, int packedLight,
                                   int packedOverlay, float alpha,float scale,float dx,float dy,float dz){
        renderPart(part,poseStack,consumer,packedLight,packedOverlay,alpha,scale,scale,scale,dx,dy,dz);
    }
    private static void renderPart(ModelPart part, PoseStack poseStack,
                                   VertexConsumer consumer, int packedLight,
                                   int packedOverlay, float alpha,float scaleX,float scaleY,float scaleZ,float dx,float dy,float dz) {
        poseStack.pushPose();
        float pivotX = (part.x+dx) / 16.0F;
        float pivotY = (part.y+dy) / 16.0F;
        float pivotZ = (part.z+dz) / 16.0F;
        poseStack.translate(pivotX, pivotY, pivotZ);
        poseStack.scale(scaleX+1.0f, scaleY+1.0f, scaleZ+1.0f);
        poseStack.translate(-pivotX, -pivotY, -pivotZ);
        int color = ((int)(alpha*255.0f) << 24) | 0xFFFFFF;
        part.render(poseStack, consumer, packedLight, packedOverlay,color);

        poseStack.popPose();
    }
    private static void applySetupRotations(PlayerRenderer renderer, Player player,
                                            PoseStack poseStack, float bodyRot, float partialTick) {
        try {
            Method method = PlayerRenderer.class.getDeclaredMethod(
                    "setupRotations",
                    AbstractClientPlayer.class, PoseStack.class,
                    float.class, float.class, float.class, float.class);
            method.setAccessible(true);
            method.invoke(renderer, (AbstractClientPlayer) player, poseStack,
                    0.0F, bodyRot, partialTick, 1.0F);
        } catch (ReflectiveOperationException e) {
            // 回退到简单旋转
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
        }
    }
    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == TTConfig.CLIENT_SPEC) {
            color = (Integer.parseUnsignedInt(TTConfig.MAGIC_SHIELD_COLOR.get(), 16));
        }
    }
    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == TTConfig.CLIENT_SPEC) {
            color = (Integer.parseUnsignedInt(TTConfig.MAGIC_SHIELD_COLOR.get(), 16));
        }
    }
}
