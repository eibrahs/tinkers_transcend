package com.eibrahs.tinkerstranscend.client.renderer;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.mojang.blaze3d.pipeline.RenderTarget;
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
public class BlockOutlineRenderer {
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
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)
    );

    private record EdgeKey(int x1, int y1, int z1, int x2, int y2, int z2) {
        static EdgeKey of(double x1, double y1, double z1, double x2, double y2, double z2) {
            int a = (int) x1, b = (int) y1, c = (int) z1;
            int d = (int) x2, e = (int) y2, f = (int) z2;
            if (a > d || (a == d && b > e) || (a == d && b == e && c > f)) {
                return new EdgeKey(d, e, f, a, b, c);
            }
            return new EdgeKey(a, b, c, d, e, f);
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) return;
        if (Blocks.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();

        // 1. 收集每条边的信息
        Map<EdgeKey, Integer> visibleEdgeCount = new HashMap<>(); // 被可见暴露面包含的次数
        Set<EdgeKey> invisibleEdges = new HashSet<>();            // 被不可见暴露面包含

        for (BlockPos pos : Blocks) {
            for (Direction dir : Direction.values()) {
                if (Blocks.contains(pos.relative(dir))) continue;

                Vec3 center = Vec3.atCenterOf(pos).add(
                        dir.getStepX() * 0.5, dir.getStepY() * 0.5, dir.getStepZ() * 0.5);
                Vec3 normal = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ());
                Vec3 toCamera = cameraPos.subtract(center).normalize();
                boolean visible = normal.dot(toCamera) > 0;

                double x = pos.getX(), y = pos.getY(), z = pos.getZ();
                double x2 = x + 1, y2 = y + 1, z2 = z + 1;
                double[][] verts = switch (dir) {
                    case UP -> new double[][]{{x, y2, z}, {x2, y2, z}, {x2, y2, z2}, {x, y2, z2}};
                    case DOWN -> new double[][]{{x, y, z}, {x2, y, z}, {x2, y, z2}, {x, y, z2}};
                    case NORTH -> new double[][]{{x, y, z}, {x2, y, z}, {x2, y2, z}, {x, y2, z}};
                    case SOUTH -> new double[][]{{x, y, z2}, {x2, y, z2}, {x2, y2, z2}, {x, y2, z2}};
                    case WEST -> new double[][]{{x, y, z}, {x, y, z2}, {x, y2, z2}, {x, y2, z}};
                    case EAST -> new double[][]{{x2, y, z}, {x2, y, z2}, {x2, y2, z2}, {x2, y2, z}};
                };

                for (int i = 0; i < 4; i++) {
                    double[] v1 = verts[i];
                    double[] v2 = verts[(i + 1) % 4];
                    EdgeKey key = EdgeKey.of(v1[0], v1[1], v1[2], v2[0], v2[1], v2[2]);
                    if (visible) {
                        visibleEdgeCount.merge(key, 1, Integer::sum);
                    } else {
                        invisibleEdges.add(key);
                    }
                }
            }
        }

        // 2. 确定需要绘制的边
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(XRAY_LINES);

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        for (var entry : visibleEdgeCount.entrySet()) {
            EdgeKey key = entry.getKey();
            int count = entry.getValue();
            // 轮廓边：同时被可见面和不可见面包含
            // 外边界：只被一个可见面包含
            if (invisibleEdges.contains(key) || count == 1) {
                line(poseStack, consumer, key);
            }
        }

        poseStack.popPose();
        bufferSource.endBatch(XRAY_LINES);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void line(PoseStack poseStack, VertexConsumer consumer, EdgeKey key) {
        PoseStack.Pose pose = poseStack.last();
        float dx = key.x2 - key.x1, dy = key.y2 - key.y1, dz = key.z2 - key.z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0) return;
        dx /= len; dy /= len; dz /= len;
        float r = 242.0f/255.0f, g = 190.0f/255.0f, b = 69.0f/255.0f, a = 1.0F;

        consumer.addVertex(pose, key.x1, key.y1, key.z1).setColor(r, g, b, a).setNormal(pose, dx, dy, dz);
        consumer.addVertex(pose, key.x2, key.y2, key.z2).setColor(r, g, b, a).setNormal(pose, dx, dy, dz);
    }

    public static void addBlocks(List<BlockPos> blockPos) {
        Blocks.addAll(blockPos);
    }

    public static void clear() {
        Blocks.clear();
    }
}