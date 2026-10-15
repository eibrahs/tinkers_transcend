package com.eibrahs.tinkerstranscend.mixin;

import com.eibrahs.tinkerstranscend.TinkersTranscendAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "bobHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getLastDamageSource()Lnet/minecraft/world/damagesource/DamageSource;"
            ),
            cancellable = true
    )
    private void onBobHurtAfterNoFlinchCheck(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        Minecraft mc = ((GameRenderer)(Object)this).getMinecraft();
        if (!(mc.getCameraEntity() instanceof Player player)) return;
        if (!(player.hasData(TinkersTranscendAttachments.MAGIC_SHIELD))) return;
        if (player.getData(TinkersTranscendAttachments.MAGIC_SHIELD).isBroken()) return;
        ci.cancel();
    }
}
