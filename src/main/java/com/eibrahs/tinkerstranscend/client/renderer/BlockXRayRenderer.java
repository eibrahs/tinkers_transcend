/*package com.eibrahs.tinkerstranscend.client.renderer;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.*;

@EventBusSubscriber(modid = TinkersTranscend.MODID, value = Dist.CLIENT)
public class BlockXRayRenderer {
    private static final Set<BlockPos> Blocks = new HashSet<>();
    public static final RenderType XRAY_LINES = RenderType.create(
            "xray_lines",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)       // ← 只写颜色，不写深度
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)     // ← 关键：关闭深度测试
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)
    );

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        // 选择在方块实体渲染之后进行，避免被遮挡
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS ) return;

        // 你希望描边的方块位置列表
        if (Blocks.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();

        // 获取用于绘制轮廓的 VertexConsumer
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(XRAY_LINES);

        poseStack.pushPose();
        // 将坐标系原点移动到相机位置，这是世界渲染的标准做法
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        // 遍历每个需要描边的方块
        for (BlockPos pos : Blocks) {
            // 检查 6 个方向
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                // 如果邻居也在描边集合中，说明这个面是内部面，跳过
                if (Blocks.contains(neighbor)) continue;

                // 否则，这个面是暴露面，绘制它的 4 条边
                renderFaceEdges(poseStack, consumer, pos, dir);
            }
        }

        poseStack.popPose();
        bufferSource.endBatch(XRAY_LINES);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public static void addBlocks(List<BlockPos> blockPos){
        Blocks.addAll(blockPos);
    }
    public static void clear(){
        Blocks.clear();
    }
    private static void renderFaceOutline(PoseStack poseStack, VertexConsumer consumer,
                                          BlockPos pos, Direction dir) {
        // 方块的最小/最大边界
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        double x2 = x + 1;
        double y2 = y + 1;
        double z2 = z + 1;

        // 根据方向，确定这个面的 4 个顶点
        float r = 242.0f/255.0f, g = 202.0f/255.0f, b = 13.0f/255.0f, a = 0.2F;

        switch (dir) {
            case DOWN -> drawQuadEdges(poseStack, consumer,
                    x, y, z, x2, y, z, x2, y, z2, x, y, z2, r, g, b, a);
            case UP -> drawQuadEdges(poseStack, consumer,
                    x, y2, z, x2, y2, z, x2, y2, z2, x, y2, z2, r, g, b, a);
            case NORTH -> drawQuadEdges(poseStack, consumer,
                    x, y, z, x2, y, z, x2, y2, z, x, y2, z, r, g, b, a);
            case SOUTH -> drawQuadEdges(poseStack, consumer,
                    x, y, z2, x2, y, z2, x2, y2, z2, x, y2, z2, r, g, b, a);
            case WEST -> drawQuadEdges(poseStack, consumer,
                    x, y, z, x, y, z2, x, y2, z2, x, y2, z, r, g, b, a);
            case EAST -> drawQuadEdges(poseStack, consumer,
                    x2, y, z, x2, y, z2, x2, y2, z2, x2, y2, z, r, g, b, a);
        }
    }

    //绘制一个四边形的 4 条边
    private static void drawQuadEdges(PoseStack poseStack, VertexConsumer consumer,
                                      double x1, double y1, double z1,
                                      double x2, double y2, double z2,
                                      double x3, double y3, double z3,
                                      double x4, double y4, double z4,
                                      float r, float g, float b, float a) {
        line(poseStack, consumer, x1, y1, z1, x2, y2, z2, r, g, b, a);
        line(poseStack, consumer, x2, y2, z2, x3, y3, z3, r, g, b, a);
        line(poseStack, consumer, x3, y3, z3, x4, y4, z4, r, g, b, a);
        line(poseStack, consumer, x4, y4, z4, x1, y1, z1, r, g, b, a);
    }

    //绘制一条线段
    private static void line(PoseStack poseStack, VertexConsumer consumer,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             float r, float g, float b, float a) {
        PoseStack.Pose pose = poseStack.last();
        // 计算法线（线段方向）
        float dx = (float)(x2 - x1);
        float dy = (float)(y2 - y1);
        float dz = (float)(z2 - z1);
        float len = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (len == 0) return;
        dx /= len; dy /= len; dz /= len;

        consumer.addVertex(pose, (float)x1, (float)y1, (float)z1)
                .setColor(r, g, b, a)
                .setNormal(pose, dx, dy, dz);
        consumer.addVertex(pose, (float)x2, (float)y2, (float)z2)
                .setColor(r, g, b, a)
                .setNormal(pose, dx, dy, dz);
    }
    private static void renderFaceEdges(PoseStack poseStack, VertexConsumer consumer,
                                        BlockPos pos, Direction face) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        double x2 = x + 1, y2 = y + 1, z2 = z + 1;

        // 面的 4 个顶点（按顺序）
        double[][] verts = switch (face) {
            case UP    -> new double[][] {{x, y2, z}, {x2, y2, z}, {x2, y2, z2}, {x, y2, z2}};
            case DOWN  -> new double[][] {{x, y, z},  {x2, y, z},  {x2, y, z2},  {x, y, z2}};
            case NORTH -> new double[][] {{x, y, z},  {x2, y, z},  {x2, y2, z},  {x, y2, z}};
            case SOUTH -> new double[][] {{x, y, z2}, {x2, y, z2}, {x2, y2, z2}, {x, y2, z2}};
            case WEST  -> new double[][] {{x, y, z},  {x, y, z2},  {x, y2, z2},  {x, y2, z}};
            case EAST  -> new double[][] {{x2, y, z}, {x2, y, z2}, {x2, y2, z2}, {x2, y2, z}};
        };

        // 每条边对应的垂直方向（与顶点顺序严格对应）
        Direction[] edgeDirs = switch (face) {
            case UP, DOWN -> new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
            case NORTH, SOUTH -> new Direction[] {Direction.DOWN, Direction.EAST, Direction.UP, Direction.WEST};
            case WEST, EAST -> new Direction[] {Direction.DOWN, Direction.SOUTH, Direction.UP, Direction.NORTH};
        };

        for (int i = 0; i < 4; i++) {
            Direction edgeDir = edgeDirs[i];
            BlockPos sideNeighbor = pos.relative(edgeDir);

            // 如果邻居存在，并且它也有一个同方向的暴露面 → 这条边是共享的内部边，跳过
            if (Blocks.contains(sideNeighbor) && !Blocks.contains(sideNeighbor.relative(face))) {
                continue;
            }

            double[] v1 = verts[i];
            double[] v2 = verts[(i + 1) % 4];
            line(poseStack, consumer, v1[0], v1[1], v1[2], v2[0], v2[1], v2[2]);
        }
    }

    private static void line(PoseStack poseStack, VertexConsumer consumer,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2) {
        PoseStack.Pose pose = poseStack.last();
        float dx = (float)(x2 - x1), dy = (float)(y2 - y1), dz = (float)(z2 - z1);
        float len = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (len == 0) return;
        dx /= len; dy /= len; dz /= len;
        float r = 242.0f/255.0f, g = 202.0f/255.0f, b = 13.0f/255.0f, a = 1.0F;

        consumer.addVertex(pose, (float)x1, (float)y1, (float)z1).setColor(r, g, b, a).setNormal(pose, dx, dy, dz);
        consumer.addVertex(pose, (float)x2, (float)y2, (float)z2).setColor(r, g, b, a).setNormal(pose, dx, dy, dz);
    }

}*/