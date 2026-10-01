package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = TinkersTranscend.MODID)
public class EternalModifierHandler {
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        if (TinkersTranscendModifiers.hasModifier(itemEntity.getItem(), TinkersTranscendModifiers.ETERNAL_MODIFIER.getId())) {
            //void protection
            double itemY = itemEntity.getY();
            double minY = itemEntity.level().dimensionType().minY() ;
            double targetY = minY + 5.0D;
            double g = itemEntity.getGravity();
            if (itemY < minY){
                itemEntity.setDeltaMovement(new Vec3(0.0D, g*2 , 0.0D));
            }
            if (itemY < targetY) {
                itemEntity.addDeltaMovement(new Vec3(0.0D, Math.min(0.01D+g, 10 * g * Math.sqrt(targetY - itemY) - g), 0.0D));
            }
            //float on liquid
            BlockState state = itemEntity.getInBlockState();
            if (state.getBlock() instanceof LiquidBlock) {
                itemEntity.addDeltaMovement(new Vec3(0.0D, 0.0025D, 0.0D));
            }
        }
    }
    @SubscribeEvent
    public static void onItemEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            if (TinkersTranscendModifiers.hasModifier(itemEntity.getItem(), TinkersTranscendModifiers.ETERNAL_MODIFIER.getId())) {
                itemEntity.setUnlimitedLifetime();
            }
        }
    }

}
