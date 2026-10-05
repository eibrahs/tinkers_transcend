package com.eibrahs.tinkerstranscend.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class FoxSoulBottleItem extends Item {
    public FoxSoulBottleItem(Properties properties) {
        super(properties);
    }
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        super.finishUsingItem(stack, level, entityLiving);
        if (entityLiving instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide) {
            applyEffect(entityLiving);
        }
        stack.shrink(1);
        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (entityLiving instanceof Player) {
                Player player = (Player)entityLiving;
                if (!player.hasInfiniteMaterials()) {
                    ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
                    if (!player.getInventory().add(itemstack)) {
                        player.drop(itemstack, false);
                    }
                }
            }

            return stack;
        }
    }
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 40;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }
    public static void applyEffect(LivingEntity entity){
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 1));
    }
    public static void applyCurse(LivingEntity entity){
        entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1800, 2));
    }
}
