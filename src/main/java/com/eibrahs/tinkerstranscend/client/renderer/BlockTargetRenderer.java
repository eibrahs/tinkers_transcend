/*package com.eibrahs.tinkerstranscend.client.renderer;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

@EventBusSubscriber(modid = TinkersTranscend.MODID, value = Dist.CLIENT)
public class BlockTargetRenderer {
    private static final Set<BlockPos> Blocks = new HashSet<>();
    private static RenderTarget renderTarget;

    // 模板缓冲的渲染类型：只写模板，不写颜色和深度
    public static final RenderType STENCIL_ONLY = RenderType.create(
            "stencil_only",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_SHADER)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE) // 占位，后续用 colorMask 控制
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .createCompositeState(false)
    );

    // 线框渲染类型：开启深度测试，只写颜色
    public static final RenderType DEPTH_LINES = RenderType.create(
            "depth_lines",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.MAIN_TARGET)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)
    );

    private static RenderTarget getRenderTarget() {
        var window = Minecraft.getInstance().getWindow();
        int w = window.getWidth();
        int h = window.getHeight();
        if (renderTarget == null) {
            renderTarget = new RenderTarget(true) {};
            renderTarget.resize(w, h, true);
            renderTarget.enableStencil(); // 启用模板缓冲
        } else if (renderTarget.width != w || renderTarget.height != h) {
            renderTarget.resize(w, h, true);
        }
        return renderTarget;
    }

    private record EdgeKey(int x1, int y1, int z1, int x2, int y2, int z2) {
        static EdgeKey of(double x1, double y1, double z1, double x2, double y2, double z2) {
            int a = (int) Math.round(x1), b = (int) Math.round(y1), c = (int) Math.round(z1);
            int d = (int) Math.round(x2), e = (int) Math.round(y2), f = (int) Math.round(z2);
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

        // 1. 收集边的可见性信息
        Map<EdgeKey, Integer> visibleEdgeCount = new HashMap<>();
        Set<EdgeKey> invisibleEdges = new HashSet<>();

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

        RenderTarget rt = getRenderTarget();
        rt.bindWrite(true);

        // 2. 清空颜色、深度和模板缓冲
        RenderSystem.clearColor(0, 0, 0, 0);
        RenderSystem.clearStencil(0);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT, true);

        // 3. 将目标方块写入模板缓冲
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        // 模板测试始终通过，将模板值写为 1
        RenderSystem.stencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        RenderSystem.stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        RenderSystem.stencilMask(0xFF);

        // 关闭颜色和深度写入，只写模板
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(false);

        // 绘制目标方块的几何体到模板缓冲
        MultiBufferSource.BufferSource stencilBuffer = mc.renderBuffers().bufferSource();
        VertexConsumer stencilConsumer = stencilBuffer.getBuffer(STENCIL_ONLY);

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (BlockPos pos : Blocks) {
            drawBlockStencil(poseStack, stencilConsumer, pos);
        }

        stencilBuffer.endBatch(STENCIL_ONLY);
        poseStack.popPose();

        // 4. 恢复颜色和深度写入
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);

        // 5. 只在模板标记的区域内渲染线框
        // 只允许模板值为 1 的像素通过
        RenderSystem.stencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        RenderSystem.stencilMask(0x00); // 不再修改模板值

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        MultiBufferSource.BufferSource lineBuffer = mc.renderBuffers().bufferSource();
        VertexConsumer lineConsumer = lineBuffer.getBuffer(DEPTH_LINES);

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (var entry : visibleEdgeCount.entrySet()) {
            EdgeKey key = entry.getKey();
            if (invisibleEdges.contains(key) || entry.getValue() == 1) {
                line(poseStack, lineConsumer, key);
            }
        }

        poseStack.popPose();
        lineBuffer.endBatch(DEPTH_LINES);

        // 6. 关闭模板测试，恢复默认状态
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        RenderSystem.stencilMask(0xFF);

        // 7. 恢复主渲染目标
        rt.unbindWrite();
        mc.getMainRenderTarget().bindWrite(true);

        // 8. 关闭深度测试，把结果贴到屏幕（启用混合，保留透明背景）
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        rt.blitToScreen(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
    }

    private static void drawBlockStencil(PoseStack poseStack, VertexConsumer consumer, BlockPos pos) {
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        double x2 = x + 1, y2 = y + 1, z2 = z + 1;
        PoseStack.Pose pose = poseStack.last();

        // 六个面，每个面两个三角形
        // DOWN
        addQuad(consumer, pose, x, y, z, x2, y, z, x2, y, z2, x, y, z2);
        // UP
        addQuad(consumer, pose, x, y2, z, x2, y2, z, x2, y2, z2, x, y2, z2);
        // NORTH
        addQuad(consumer, pose, x, y, z, x2, y, z, x2, y2, z, x, y2, z);
        // SOUTH
        addQuad(consumer, pose, x, y, z2, x2, y, z2, x2, y2, z2, x, y2, z2);
        // WEST
        addQuad(consumer, pose, x, y, z, x, y, z2, x, y2, z2, x, y2, z);
        // EAST
        addQuad(consumer, pose, x2, y, z, x2, y, z2, x2, y2, z2, x2, y2, z);
    }

    private static void addQuad(VertexConsumer consumer, PoseStack.Pose pose,
                                double x1, double y1, double z1,
                                double x2, double y2, double z2,
                                double x3, double y3, double z3,
                                double x4, double y4, double z4) {
        consumer.addVertex(pose, (float) x1, (float) y1, (float) z1);
        consumer.addVertex(pose, (float) x2, (float) y2, (float) z2);
        consumer.addVertex(pose, (float) x3, (float) y3, (float) z3);
        consumer.addVertex(pose, (float) x4, (float) y4, (float) z4);
    }

    private static void line(PoseStack poseStack, VertexConsumer consumer, EdgeKey key) {
        PoseStack.Pose pose = poseStack.last();
        float dx = key.x2 - key.x1, dy = key.y2 - key.y1, dz = key.z2 - key.z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0) return;
        dx /= len; dy /= len; dz /= len;
        float r = 250.0f / 255.0f, g = 214.0f / 255.0f, b = 74.0f / 255.0f, a = 1.0F;

        consumer.addVertex(pose, key.x1, key.y1, key.z1).setColor(r, g, b, a).setNormal(pose, dx, dy, dz);
        consumer.addVertex(pose, key.x2, key.y2, key.z2).setColor(r, g, b, a).setNormal(pose, dx, dy, dz);
    }

    public static void addBlocks(List<BlockPos> blockPos) {
        Blocks.addAll(blockPos);
    }

    public static void clear() {
        Blocks.clear();
    }
}*/