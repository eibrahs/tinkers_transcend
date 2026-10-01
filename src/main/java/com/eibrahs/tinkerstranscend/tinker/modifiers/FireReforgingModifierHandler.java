/*package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import dev.dubhe.anvilcraft.block.entity.FishTankBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LavaCauldronBlock;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


@EventBusSubscriber(modid = TinkersTranscend.MODID)
public class FireReforgingModifierHandler {
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event){
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }
        ItemStack stack = itemEntity.getItem();
        if (!stack.isDamaged()) return;
        if (TinkersTranscendModifiers.hasModifier(stack, TinkersTranscendModifiers.FIRE_REFORGING_MODIFIER.getId())) {

        Level level = itemEntity.level();
        int fixDamage = 0;
        BlockState state = level.getBlockState(itemEntity.blockPosition());

        if (itemEntity.isOnFire()) {
            if (state.getBlock() instanceof SoulFireBlock){
                fixDamage = 5;
            }
            else {
                fixDamage = 2;
            }
        }else if (itemEntity.isInLava()
                ||state.getBlock() instanceof LavaCauldronBlock){
            fixDamage = 10;
        }
        stack.setDamageValue(Math.max(0,stack.getDamageValue()-fixDamage));
        }
    }
}
*/