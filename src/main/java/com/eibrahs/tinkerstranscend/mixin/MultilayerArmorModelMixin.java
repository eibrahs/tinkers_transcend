package com.eibrahs.tinkerstranscend.mixin;

import com.eibrahs.tinkerstranscend.client.state.ArmorFoilState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.client.armor.MultilayerArmorModel;

@Mixin(MultilayerArmorModel.class)
public class MultilayerArmorModelMixin {

    @Shadow
    protected ItemStack armorStack;

    @Inject(method = "renderToBuffer", at = @At("HEAD"))
    private void tinkersTranscend$captureArmorStack(
            PoseStack matrices, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, int color, CallbackInfo ci) {
        ArmorFoilState.set(this.armorStack);
    }
}