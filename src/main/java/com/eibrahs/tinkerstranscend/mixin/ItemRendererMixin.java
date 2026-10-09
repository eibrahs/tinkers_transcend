package com.eibrahs.tinkerstranscend.mixin;

import com.eibrahs.tinkerstranscend.client.shader.TinkersTranscendShaders;
import com.eibrahs.tinkerstranscend.client.state.ArmorFoilState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique
    private static ItemStack capturedStack = ItemStack.EMPTY;

    @Inject(method = "render", at = @At("HEAD"))
    private void captureStack(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci) {
        capturedStack = itemStack;
    }

    @Inject(method = "getFoilBufferDirect", at = @At("HEAD"), cancellable = true)
    private static void onGetFoilBufferDirect(MultiBufferSource bufferSource, RenderType renderType,
                                              boolean noEntity, boolean withGlint,
                                              CallbackInfoReturnable<VertexConsumer> cir) {
        if (capturedStack.isEmpty() || !capturedStack.has(DataComponents.CUSTOM_DATA)) return;
        CustomData data =capturedStack.get(DataComponents.CUSTOM_DATA);

        if (!data.contains("glint_color")) return;
        int color = data.copyTag().getInt("glint_color");
        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        TinkersTranscendShaders.setColorfulGlintShaderColor(r,g,b,a);
        VertexConsumer consumer = VertexMultiConsumer.create(
                bufferSource.getBuffer(TinkersTranscendShaders.getColorfulGlintRenderType()),
                bufferSource.getBuffer(renderType)
        );

        cir.setReturnValue(consumer);
    }
    @Inject(method = "getArmorFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void tinkersTranscend$getArmorFoilBuffer(
            MultiBufferSource bufferSource,
            RenderType renderType,
            boolean hasFoil,
            CallbackInfoReturnable<VertexConsumer> cir) {

        ItemStack capturedArmorStack = ArmorFoilState.get();
        if (capturedArmorStack.isEmpty() || !capturedArmorStack.has(DataComponents.CUSTOM_DATA)) return;
        CustomData customData = capturedArmorStack.get(DataComponents.CUSTOM_DATA);
        if (!customData.contains("glint_color")) return;

        int color = customData.copyTag().getInt("glint_color");
        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        TinkersTranscendShaders.setArmorEntityColorfulGlintShaderColor(r, g, b, a);

        VertexConsumer consumer = VertexMultiConsumer.create(
                bufferSource.getBuffer(renderType),
                bufferSource.getBuffer(TinkersTranscendShaders.getArmorEntityColorfulGlintRenderType())
        );
        cir.setReturnValue(consumer);
    }
}