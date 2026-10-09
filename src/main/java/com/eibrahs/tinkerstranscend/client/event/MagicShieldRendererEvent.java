package com.eibrahs.tinkerstranscend.client.event;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.TinkersTranscendAttachments;
import com.eibrahs.tinkerstranscend.tinker.modifiers.handler.MagicShield;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;


@EventBusSubscriber(modid = TinkersTranscend.MODID, value = Dist.CLIENT)
public class MagicShieldRendererEvent {
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
        // === 手动重建 super.render() 内部的变换 ===

        // 1. 身体旋转（对应 LivingEntityRenderer 里的 Axis.YP.rotationDegrees(180 - bodyRot)）
        float bodyRot = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));

        // 2. 翻转 X 和 Y 轴（对应 super.render 里的 scale(-1, -1, 1)）
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        // 3. 玩家自身的缩放（对应 PlayerRenderer.scale 里的 scale(0.9375F, ...)）
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);

        // 4. 向上平移（对应 super.render 里的 translate(0, -1.501, 0)）
        poseStack.translate(0.0F, -1.501F, 0.0F);

        float scale = 1.35f;

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                TinkersTranscend.MODID, "textures/effect/shield.png");
        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityTranslucent(texture));

        renderPart(model.head, poseStack, consumer, packedLight, packedOverlay, 0.5f,scale);
        renderPart(model.body, poseStack, consumer, packedLight, packedOverlay, 0.5f,1.0f);
        renderPart(model.rightArm, poseStack, consumer, packedLight, packedOverlay, 0.5f,scale);
        renderPart(model.leftArm, poseStack, consumer, packedLight, packedOverlay, 0.5f,scale);
        renderPart(model.rightLeg, poseStack, consumer, packedLight, packedOverlay, 0.5f,scale,1.0f,scale);
        renderPart(model.leftLeg, poseStack, consumer, packedLight, packedOverlay, 0.5f,scale,1.0f,scale);
        poseStack.popPose();
    }
    private static void renderPart(ModelPart part, PoseStack poseStack,
                                   VertexConsumer consumer, int packedLight,
                                   int packedOverlay, float alpha,float scale){
        renderPart(part,poseStack,consumer,packedLight,packedOverlay,alpha,scale,scale,scale);
    }
    private static void renderPart(ModelPart part, PoseStack poseStack,
                                   VertexConsumer consumer, int packedLight,
                                   int packedOverlay, float alpha,float scaleX,float scaleY,float scaleZ) {
        poseStack.pushPose();
        // 1. 平移到该部件的枢轴点（像素转格）
        float pivotX = part.x / 16.0F;
        float pivotY = part.y / 16.0F;
        float pivotZ = part.z / 16.0F;
        poseStack.translate(pivotX, pivotY, pivotZ);

        // 2. 以枢轴点为中心进行缩放
        poseStack.scale(scaleX, scaleY, scaleZ);

        // 3. 平移回部件原点
        poseStack.translate(-pivotX, -pivotY, -pivotZ);
        // 重新渲染该部件，并应用指定的透明度
        int color = ((int)(alpha*255.0f) << 24) | 0xFFFFFF;
        part.render(poseStack, consumer, packedLight, packedOverlay,color);
        poseStack.popPose();
    }
}
