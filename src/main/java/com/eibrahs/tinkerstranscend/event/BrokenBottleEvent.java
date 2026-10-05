package com.eibrahs.tinkerstranscend.event;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.item.FoxSoulBottleItem;
import com.eibrahs.tinkerstranscend.item.TinkersTranscendItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;


@EventBusSubscriber(modid = TinkersTranscend.MODID)
public class BrokenBottleEvent {
    @SubscribeEvent
    public static void onPlayerHurt(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (stack.is(TinkersTranscendItems.FOX_SOUL_BOTTLE.get())) {
                float chance = event.getNewDamage()/player.getMaxHealth();
                if (player.getRandom().nextFloat() < chance) {
                    stack.setCount(stack.getCount()-1);
                    player.getInventory().add(new ItemStack(TinkersTranscendItems.BROKEN_BOTTLE.get()));
                    FoxSoulBottleItem.applyEffect(player);
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.5f, 1.0f);
                    break;
                }
            }
        }
    }
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Fox fox)) {
            return;
        }
        if (event.getSource().getEntity() instanceof Player player) {
            Inventory inventory = player.getInventory();
            int count = 0;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);

                if (stack.is(TinkersTranscendItems.FOX_SOUL_BOTTLE.get())) {
                    count+=stack.getCount();
                    stack.shrink(stack.getCount());
                }
            }
            if (count>0){
                FoxSoulBottleItem.applyCurse(player);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.5f, 1.0f);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.FOX_SCREECH, SoundSource.PLAYERS, 1.5f, 1.0f);
                ItemStack bottle = new ItemStack(TinkersTranscendItems.BROKEN_BOTTLE.get());
                bottle.setCount(count);
                inventory.add(bottle);
            }
        }
    }
}
