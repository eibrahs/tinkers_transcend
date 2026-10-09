package com.eibrahs.tinkerstranscend.mixin;

import com.eibrahs.tinkerstranscend.client.shader.TinkersTranscendShaders;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Unique
    private static ItemStack capturedArmorStack = ItemStack.EMPTY;
    @ModifyArg(
            method = "renderGlint(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            ),
            index = 0
    )
    private RenderType replaceArmorGlint(RenderType original) {
        if (!capturedArmorStack.isEmpty() && capturedArmorStack.has(DataComponents.CUSTOM_DATA)) {
            CustomData customData = capturedArmorStack.get(DataComponents.CUSTOM_DATA);
            if (!(customData.contains("glint_color"))) return original;
            int color = customData.copyTag().getInt("glint_color");
            float a = ((color >> 24) & 0xFF)+128.0f / 255.0f;
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            TinkersTranscendShaders.setArmorEntityColorfulGlintShaderColor(r,g,b,a);
            return TinkersTranscendShaders.getArmorEntityColorfulGlintRenderType();
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z"
            )
    )
    private boolean tinkersTranscend$addCustomGlintCondition(
            boolean original,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            LivingEntity livingEntity,
            EquipmentSlot slot) {

        ItemStack stack = livingEntity.getItemBySlot(slot);
        capturedArmorStack = stack;

        if (original) return true;
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.contains("glint_color");
    }
}