package com.eibrahs.tinkerstranscend.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;

@Mixin(ModifiableLauncherItem.class)
public class ModifiableLauncherItemMixin {
    @Inject(method = "isBookEnchantable", at = @At("HEAD"), cancellable = true)
    private void onIsBookEnchantable(ItemStack stack, ItemStack book, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
