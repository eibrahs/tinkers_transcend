package com.eibrahs.tinkerstranscend.client.renderer;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.client.shader.TinkersTranscendShaders;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = TinkersTranscend.MODID, value = Dist.CLIENT)
public class BlockOutlineRenderer {

    private static final Set<BlockPos> Blocks = ConcurrentHashMap.newKeySet();
    public static void addBlocks(List<BlockPos> blockPos) {Blocks.addAll(blockPos);}
    public static void clear() {Blocks.clear();}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (Blocks.isEmpty()) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        OutlineBufferSource outlineBuffer = mc.renderBuffers().outlineBufferSource();
        outlineBuffer.endOutlineBatch();
        outlineBuffer.setColor(242, 190, 69, 255);
        mc.levelRenderer.requestOutlineEffect();

        VertexConsumer consumer = outlineBuffer.getBuffer(TinkersTranscendShaders.getBlockOutline());

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();
        BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();

       for (BlockPos pos : Blocks) {
            BlockState state = level.getBlockState(pos);

            poseStack.pushPose();
            poseStack.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);

            blockRenderer.renderBatched(state, pos, level, poseStack, consumer, false,
                    level.getRandom(), ModelData.EMPTY, TinkersTranscendShaders.getBlockOutline());

            poseStack.popPose();
        }
        outlineBuffer.endOutlineBatch();
    }
}