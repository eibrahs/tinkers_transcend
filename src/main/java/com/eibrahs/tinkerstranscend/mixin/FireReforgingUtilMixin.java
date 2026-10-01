package com.eibrahs.tinkerstranscend.mixin;

import com.eibrahs.tinkerstranscend.tinker.modifiers.TinkersTranscendModifiers;
import dev.dubhe.anvilcraft.util.FireReforgingUtil;
import dev.dubhe.anvilcraft.util.TriggerUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireReforgingUtil.class)
public class FireReforgingUtilMixin {

    @Inject(method = "repair", at = @At("HEAD"), cancellable = true)
    private static void onRepair(ItemStack stack, int amount, Level level, @Nullable BlockPos pos,
                                 CallbackInfoReturnable<Boolean> cir) {
        if (!stack.isEmpty() && stack.isDamaged() &&
                (TinkersTranscendModifiers.hasModifier(stack,TinkersTranscendModifiers.FIRE_REFORGING_MODIFIER.getId()))) {
            stack.setDamageValue(Math.max(0,stack.getDamageValue() - amount));
            if (pos != null) {
                TriggerUtil.fireReforge(level, pos);
            }
            return;
        }

        cir.setReturnValue(false);
    }
}