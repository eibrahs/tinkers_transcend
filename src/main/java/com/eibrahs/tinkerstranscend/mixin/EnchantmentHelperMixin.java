package com.eibrahs.tinkerstranscend.mixin;

import com.eibrahs.tinkerstranscend.tinker.modifiers.TinkersTranscendModifiers;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.dubhe.anvilcraft.util.mixin.ProvidenceRef;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//强运对荆棘附魔的检查判定没有生效,所有装备的荆棘附魔都能触发
@Mixin({EnchantmentHelper.class})
abstract class EnchantmentHelperMixin {
    EnchantmentHelperMixin() {
    }

    @WrapOperation(
            method = {"runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;accept(Lnet/minecraft/core/Holder;ILnet/minecraft/world/item/enchantment/EnchantedItemInUse;)V"
            )}
    )
    private static void checkShouldTriggerProvidence(EnchantmentHelper.EnchantmentInSlotVisitor instance, Holder<Enchantment> holder, int i, EnchantedItemInUse enchantedItemInUse, Operation<Void> original, @Local Holder<Enchantment> enchantment) {
        if (!TinkersTranscendModifiers.hasModifier(enchantedItemInUse.itemStack(), TinkersTranscendModifiers.PROVIDENCE_MODIFIER.getId())) {
            original.call(new Object[]{instance, holder, i, enchantedItemInUse});
        } else {
            ProvidenceRef.shouldTrigger();
            original.call(new Object[]{instance, holder, i, enchantedItemInUse});
            ProvidenceRef.reset();
        }
    }
}
