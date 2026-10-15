package com.eibrahs.tinkerstranscend.event;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.TinkersTranscendAttachments;
import com.eibrahs.tinkerstranscend.tinker.modifiers.TinkersTranscendModifiers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = TinkersTranscend.MODID)
public class FoxProtectionEvent {
    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        if (! (entity instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        float damage = event.getNewDamage();
        if (Float.isNaN(damage)||damage <= 0|| event.getSource().is(DamageTypes.GENERIC_KILL)) return;

        int modifierLevel = getModifierLevel(player);
        if (modifierLevel<=0) return;

        float value = player.getData(TinkersTranscendAttachments.FOX_SOUL_VALUE);
        if (value<0.0f) return;

        float takeDamageRate = damage/player.getMaxHealth();//受到伤害与最大生命值的比例
        float maxDamageRate = Math.min(modifierLevel*0.1f,0.8f);//最大减伤倍率：等级*0.1
        float damageRate = Math.min(takeDamageRate,maxDamageRate);//实际减伤倍率
        float reduceDamage = damageRate * damage;//实际减少伤害，不超过实际value

        float reduceValue = reduceDamage;//计算实际value消耗
        float valueRate = modifierLevel * 0.1f;//value额外消耗阈值
        if (takeDamageRate>valueRate){
            reduceValue = (float)Math.pow(reduceValue,(float)Math.log(reduceValue));//若超过阈值则额外消耗
        }
        reduceValue = Math.min(reduceValue,value);
        float newDamage = damage - Math.min(value,reduceDamage);//实际伤害
        player.setData(TinkersTranscendAttachments.FOX_SOUL_VALUE, Math.clamp(value - reduceValue + newDamage,0.0f,100.0f));//应用value消耗
        event.setNewDamage(newDamage);//应用实际伤害
    }
    private static int getModifierLevel(ServerPlayer player){
        int level = 0;
        for(ItemStack slot :player.getArmorSlots()){
            level += TinkersTranscendModifiers.getModifierLevel(slot,TinkersTranscendModifiers.FOX_PROTECTION_MODIFIER.getId());
        }
        return level;
    }
}
