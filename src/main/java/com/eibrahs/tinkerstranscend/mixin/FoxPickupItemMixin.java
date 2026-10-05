package com.eibrahs.tinkerstranscend.mixin;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.item.TinkersTranscendItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Fox.class)
public class FoxPickupItemMixin {
    @Inject(method = "pickUpItem", at = @At("RETURN"))
    private void onPickUpItem(ItemEntity itemEntity, CallbackInfo ci) {
        Fox fox = (Fox) (Object) this;
        if (!(itemEntity.getOwner() instanceof Player)) return;
        ItemStack pickupItem = fox.getWeaponItem();
        if (!(pickupItem.is(Items.SWEET_BERRIES)||pickupItem.is(Items.CHICKEN))) return;
        TinkersTranscend.LOGGER.info("item:{}",pickupItem);
        float chance = fox.getVariant() == Fox.Type.SNOW? 0.2f : 0.1f;
        if (fox.level().getRandom().nextFloat()<chance){
            fox.getPersistentData().putBoolean("soul",true);
        }
    }
    @Inject(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;",
                    shift = At.Shift.AFTER
            )
    )
    private void onFoxFinishEating(CallbackInfo ci) {
        Fox fox = (Fox) (Object) this;
        if (fox.getPersistentData().getBoolean("soul")) {
            fox.getPersistentData().putBoolean("soul", false); // 重置，防止重复触发
            createItem(fox);
        }
    }
    private static void createItem(Fox fox){
        fox.level().addFreshEntity(new ItemEntity(fox.level(),fox.getX(),fox.getY(),fox.getZ(),new ItemStack(TinkersTranscendItems.FOX_SOUL_BOTTLE.get())));
    }
}
