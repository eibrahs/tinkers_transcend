package com.eibrahs.tinkerstranscend.mixin;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.tinker.modifiers.TinkersTranscendModifiers;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ApplyBonusCount.class)
public class ApplyBonusCountMixin {
    @Shadow
    @Final
    private Holder<Enchantment> enchantment;


    @WrapOperation(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/loot/functions/ApplyBonusCount$Formula;"
                            +"calculateNewCount(Lnet/minecraft/util/RandomSource;II)I"
            )
    )

    private int calculateMultipleForProvidence(
            ApplyBonusCount.Formula instance, RandomSource random1, int count, int level, Operation<Integer> original,
            @Local(ordinal = 0, argsOnly = true) LootContext context
    ) {
        int result = (Integer)original.call(new Object[]{instance, random1, count, level});
        if (level != 0) {
            Object var9 = context.getParamOrNull(LootContextParams.TOOL);
            if (var9 instanceof ItemStack) {
                ItemStack stack = (ItemStack)var9;
                if (!TinkersTranscendModifiers.hasModifier(stack, TinkersTranscendModifiers.PROVIDENCE_MODIFIER.getId())) return result;
                TinkersTranscend.LOGGER.info("Mixin ApplyBonusCount triggered! level: {}", level);
                float random = random1.nextFloat();
                if (random >= 0.25F) return result;
                result += (Integer)original.call(new Object[]{instance, random1, count, level});
                if (random >= 0.05F) return result;
                result += (Integer)original.call(new Object[]{instance, random1, count, level});
            }
        }

        return result;
    }
}
