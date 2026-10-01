/*package com.eibrahs.tinkerstranscend.client.renderer;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

@EventBusSubscriber(modid = TinkersTranscend.MODID, value = Dist.CLIENT)
public class BlockShaderRenderer {

    private static final Set<BlockPos> Blocks = new HashSet<>();
    private static RenderTarget outlineTarget;
    public static ShaderInstance blockOutlineShader;

    private static final ResourceLocation OUTLINE_TARGET_ID =
            ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "block_outline_buffer");

    private static RenderTarget getOutlineTarget() {
        var window = Minecraft.getInstance().getWindow();
        int w = window.getWidth();
        int h = window.getHeight();
        if (outlineTarget == null) {
            outlineTarget = new TextureTarget(w, h, true, Minecraft.ON_OSX);
            outlineTarget.setClearColor(0, 0, 0, 0);
        } else if (outlineTarget.width != w || outlineTarget.height != h) {
            outlineTarget.resize(w, h, Minecraft.ON_OSX);
        }
        return outlineTarget;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (Blocks.isEmpty() || blockOutlineShader == null) return;

        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        RenderTarget target = getOutlineTarget();

        // ========== 第一步：把方块渲染到轮廓纹理 ==========
        target.bindWrite(true);
        RenderSystem.clearColor(0, 0, 0, 0);
        RenderSystem.clearDepth(1.0);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, true);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.LIGHTNING); // 使用一个简单的纯色渲染类型

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        for (BlockPos pos : Blocks) {
            // 绘制方块的线框或简单几何体
            // LevelRenderer.renderVoxelShape(poseStack, consumer, Shapes.block(), pos, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        poseStack.popPose();
        bufferSource.endBatch();

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        target.unbindWrite();
        mc.getMainRenderTarget().bindWrite(true);

        // ========== 第二步：用自定义着色器将轮廓叠加到屏幕 ==========
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 绑定轮廓纹理
        RenderSystem.setShaderTexture(0, target.getColorTextureId());
        RenderSystem.setShader(() -> blockOutlineShader);

        // 设置 Uniform
        blockOutlineShader.safeGetUniform("TexelSize").set(1.0f / target.width, 1.0f / target.height);

        // 绘制全屏四边形
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.addVertex(0, 0, 0).setUv(0, 1);
        builder.addVertex(mc.getWindow().getWidth(), 0, 0).setUv(1, 1);
        builder.addVertex(mc.getWindow().getWidth(), mc.getWindow().getHeight(), 0).setUv(1, 0);
        builder.addVertex(0, mc.getWindow().getHeight(), 0).setUv(0, 0);
        BufferUploader.drawWithShader(builder.build());

        // 恢复状态
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.setShader(GameRenderer::getPositionTexShader); // 恢复默认着色器
    }

    public static void addBlocks(List<BlockPos> blockPos) {
        Blocks.addAll(blockPos);
    }

    public static void clear() {
        Blocks.clear();
    }

}*/