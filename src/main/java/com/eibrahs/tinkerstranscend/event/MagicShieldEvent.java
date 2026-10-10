package com.eibrahs.tinkerstranscend.event;

import com.eibrahs.tinkerstranscend.TTConfig;
import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.TinkersTranscendAttachments;
import com.eibrahs.tinkerstranscend.sound.TinkersTranscendSounds;
import com.eibrahs.tinkerstranscend.tinker.modifiers.handler.MagicShield;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
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
            entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),TinkersTranscendSounds.MAGIC_SHIELD_BREAK_C1, entity.getSoundSource(),1.0f,1.0f);
            entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),TinkersTranscendSounds.MAGIC_SHIELD_BREAK_C2, entity.getSoundSource(),0.5f,1.0f);

        } else {
            entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),TinkersTranscendSounds.MAGIC_SHIELD_HURT_C1, entity.getSoundSource(),0.6f,1.5f);
            entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),TinkersTranscendSounds.MAGIC_SHIELD_HURT_C2, entity.getSoundSource(),0.4f,1.8f);

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
        if (player.getArrowCount()>0) player.setArrowCount(0);
    }
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        player.removeData(TinkersTranscendAttachments.MAGIC_SHIELD);
    }
    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        if (!TTConfig.MAGIC_SHIELD_FOX_SOUND.get()) return;
        SoundInstance sound = event.getSound();
        Level level = Minecraft.getInstance().level;
        if (sound==null) return;
        ResourceLocation soundId = sound.getLocation();
        if (soundId.equals(TinkersTranscendSounds.MAGIC_SHIELD_HURT_C1.getId())) {
            level.playLocalSound(sound.getX(),sound.getY(),sound.getZ(),SoundEvents.FOX_HURT, sound.getSource(),0.8f,0.5f+level.getRandom().nextFloat(),false);

        } else if (soundId.equals(TinkersTranscendSounds.MAGIC_SHIELD_BREAK_C1.getId())) {
            level.playLocalSound( sound.getX(), sound.getY(), sound.getZ(), SoundEvents.FOX_DEATH, sound.getSource(), 1.0f, 1.0f,false);
        }
    }
}
