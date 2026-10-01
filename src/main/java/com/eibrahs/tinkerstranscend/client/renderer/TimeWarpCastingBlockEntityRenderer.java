package com.eibrahs.tinkerstranscend.client.renderer;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.RenderItem;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.tank.CastingFluidHandler;
import slimeknights.tconstruct.smeltery.client.util.CastingItemRenderTypeBuffer;

import java.util.List;

public class TimeWarpCastingBlockEntityRenderer implements BlockEntityRenderer<CastingBlockEntity> {
    public TimeWarpCastingBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(CastingBlockEntity casting, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int light, int combinedOverlayIn) {
        BlockState state = casting.getBlockState();
        List<FluidCuboid> fluids = (List)FluidCuboid.REGISTRY.get(state, List.of());
        List<RenderItem> renderItems = (List)RenderItem.STATE_REGISTRY.get(state, List.of());
        TinkersTranscend.LOGGER.info("fluids: {},renderItems:{}",fluids,renderItems);

        if (!fluids.isEmpty() || !renderItems.isEmpty()) {
            boolean isRotated = RenderingHelper.applyRotation(matrices, state);
            int timer = casting.getTimer();
            int totalTime = casting.getCoolingTime();
            int itemOpacity = 0;
            int fluidOpacity = 255;
            if (timer > 0 && totalTime > 0) {
                int opacity = 1020 * timer / totalTime;
                itemOpacity = opacity / 4;
                if (opacity > 765) {
                    fluidOpacity = 1020 - opacity;
                }
            }

            if (!fluids.isEmpty()) {
                CastingFluidHandler tank = casting.getTank();
                FluidStack fluidStack = tank.getFluid();
                int capacity = tank.getCapacity();
                if (fluidStack.getAmount() == capacity) {
                    for(FluidCuboid fluid : fluids) {
                        RenderUtils.renderTransparentCuboid(matrices, buffer, fluid, fluidStack, fluidOpacity, light);
                    }
                } else {
                    for(FluidCuboid fluid : fluids) {
                        FluidRenderer.renderScaledCuboid(matrices, buffer, fluid, fluidStack, 0.0F, capacity, light, false);
                    }
                }
            }

            if (!renderItems.isEmpty()) {
                RenderingHelper.renderItem(matrices, buffer, casting.getItem(0), (RenderItem)renderItems.get(0), light);
                if (renderItems.size() >= 2) {
                    RenderItem outputModel = (RenderItem)renderItems.get(1);
                    if (!outputModel.isHidden()) {
                        ItemStack output = casting.getItem(1);
                        MultiBufferSource outputBuffer = buffer;
                        if (itemOpacity > 0 && output.isEmpty()) {
                            output = casting.getRecipeOutput();
                            outputBuffer = new CastingItemRenderTypeBuffer(buffer, itemOpacity, fluidOpacity);
                        }

                        RenderingHelper.renderItem(matrices, outputBuffer, output, outputModel, light);
                    }
                }
            }

            if (isRotated) {
                matrices.popPose();
            }
        }

    }
}
