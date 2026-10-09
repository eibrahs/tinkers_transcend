package com.eibrahs.tinkerstranscend.event;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.item.TinkersTranscendItems;
import com.eibrahs.tinkerstranscend.tinker.modifiers.TinkersTranscendModifiers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@EventBusSubscriber(modid = TinkersTranscend.MODID)
public class MagicArrowEvent {
    @SubscribeEvent
    public static void onLivingGetProjectile(LivingGetProjectileEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        //if (player.level().isClientSide) return;
        ItemStack projectileStack = event.getProjectileItemStack();
        if (!projectileStack.isEmpty() && !(projectileStack.is(Items.ARROW))) return;

        if (!(TinkersTranscendModifiers.hasModifier(event.getProjectileWeaponItemStack(), TinkersTranscendModifiers.FOX_SOUL_MODIFIER.getId())))
           return;
        ItemStack customArrow = new ItemStack(TinkersTranscendItems.FOX_SOUL_ARROW.get());
        event.setProjectileItemStack(customArrow);
    }
}
