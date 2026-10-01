package com.eibrahs.tinkerstranscend.block;

import com.eibrahs.tinkerstranscend.block.entity.TimeWarpCastingBlockEntity;
import com.eibrahs.tinkerstranscend.block.entity.TinkersTranscendBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.smeltery.block.CastingTableBlock;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;

import javax.annotation.Nullable;

public class TimeWarpCastingTableBlock extends CastingTableBlock {
    public TimeWarpCastingTableBlock(Properties builder, boolean requireCast) {
        super(builder, requireCast);
    }
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TimeWarpCastingBlockEntity.Table(pPos, pState);
    }
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> check) {
        return (lvl, pos, st, be) -> {
            TimeWarpCastingBlockEntity.timeWarpCasting(lvl, pos);
            CastingBlockEntity.getTicker(pLevel, check, TinkersTranscendBlockEntities.TIME_WARP_CASTING_TABLE_ENTITY.get()).tick(lvl,pos,st,be);
        };
    }
}
