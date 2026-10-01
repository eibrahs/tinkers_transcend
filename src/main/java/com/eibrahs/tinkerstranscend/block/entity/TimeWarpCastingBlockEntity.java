package com.eibrahs.tinkerstranscend.block.entity;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;
import slimeknights.tconstruct.smeltery.client.render.CastingBlockEntityRenderer;

public class TimeWarpCastingBlockEntity {
    public static class Basin extends CastingBlockEntity {
        public Basin(BlockPos pos, BlockState state){
            super(TinkersTranscendBlockEntities.TIME_WARP_CASTING_BASIN_ENTITY.get(), pos, state, TinkerRecipeTypes.CASTING_BASIN.get(), TinkerRecipeTypes.MOLDING_BASIN.get(), TinkerTags.Items.BASIN_EMPTY_CASTS);
        }
    }
    public static class Table extends CastingBlockEntity {
        public Table(BlockPos pos, BlockState state) {
            super(TinkersTranscendBlockEntities.TIME_WARP_CASTING_TABLE_ENTITY.get(), pos, state, TinkerRecipeTypes.CASTING_TABLE.get(), TinkerRecipeTypes.MOLDING_TABLE.get(), TinkerTags.Items.TABLE_EMPTY_CASTS);
        }
    }

    public static void timeWarpCasting(Level level, BlockPos pos){
        BlockEntity be = level.getBlockEntity(pos);
        if (((CastingBlockEntity)be).getTimer()==0) return;
        if (be instanceof CastingBlockEntity targetBe) {
            CompoundTag tag = targetBe.saveCustomAndMetadata(level.registryAccess());
            int timer = tag.getInt("timer");
            if (timer>0&&timer<2000000000) {
                tag.putInt("timer", 2000000000);
                targetBe.loadWithComponents(tag, level.registryAccess());
                targetBe.setChanged();
            }
        }
    }
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(TinkersTranscendBlockEntities.TIME_WARP_CASTING_TABLE_ENTITY.get(), CastingBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TinkersTranscendBlockEntities.TIME_WARP_CASTING_BASIN_ENTITY.get(), CastingBlockEntityRenderer::new);

    }
}