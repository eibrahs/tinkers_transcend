package com.eibrahs.tinkerstranscend.event;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.TinkersTranscendAttachments;
import com.eibrahs.tinkerstranscend.tinker.modifiers.handler.MagicShield;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = TinkersTranscend.MODID)
public class MagicShieldEvent {
    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (!(entity.hasData(TinkersTranscendAttachments.MAGIC_SHIELD))) return;
        MagicShield shield = entity.getData(TinkersTranscendAttachments.MAGIC_SHIELD);
        if (shield.value() <= 0) return;

        float damage = event.getNewDamage();
        if (damage <= 0) return;

        shield = shield.takeDamage(damage);
        if (shield.isBroken()) {
            shield = shield.setBrokenTime(entity.level().getGameTime());
            entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL,1.0f,1.0f);
            entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),SoundEvents.FOX_DEATH, SoundSource.NEUTRAL,1.0f,1.0f);
            entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),SoundEvents.WOLF_ARMOR_CRACK, SoundSource.NEUTRAL,0.5f,1.0f);
        } else {
            entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),SoundEvents.FOX_HURT, SoundSource.NEUTRAL,0.8f,0.5f+entity.level().getRandom().nextFloat());
        }
        entity.setData(TinkersTranscendAttachments.MAGIC_SHIELD, shield);
        event.setNewDamage(0);

    }
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (player.tickCount % 20 != 0) return;
        if (!(player.hasData(TinkersTranscendAttachments.MAGIC_SHIELD))) return;
        MagicShield shield = player.getData(TinkersTranscendAttachments.MAGIC_SHIELD);
        if (shield.isFull()) return;
        if (!(shield.canRegen(player.level().getGameTime()))) return;

        player.setData(TinkersTranscendAttachments.MAGIC_SHIELD, shield.regen());
    }
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        player.removeData(TinkersTranscendAttachments.MAGIC_SHIELD);
    }
}
