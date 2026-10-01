package com.eibrahs.tinkerstranscend.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.AbstractCastingBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;

public class TimeWarpCasterBlock extends Block {
    public TimeWarpCasterBlock(Properties properties){
        super(properties);
    }
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            if (level.hasNeighborSignal(pos)) {
                if (neighborBlock instanceof AbstractCastingBlock) {
                    triggerLogic(level, pos);
                }
            }
        }
    }
    private void triggerLogic(Level level, BlockPos pos){
        BlockPos[] offsets = {
                pos.above(), pos.below(), pos.east(), pos.west(), pos.north(), pos.south()
        };

        for (BlockPos targetPos : offsets) {
            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.is(TinkerSmeltery.searedBasin.get())
                    ||targetState.is(TinkerSmeltery.searedTable.get())
                    ||targetState.is(TinkerSmeltery.scorchedBasin.get())
                    ||targetState.is(TinkerSmeltery.scorchedTable.get())
            ) {
                BlockEntity be = level.getBlockEntity(targetPos);
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
        }
    }
}
