package com.eibrahs.tinkerstranscend.block.component;

import com.eibrahs.tinkerstranscend.block.entity.LaserHeaterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

import javax.annotation.Nullable;

public class LaserHeaterBlock extends SearedBlock {
    public LaserHeaterBlock(Properties properties, boolean requiredBlockEntity) {
        super(properties, requiredBlockEntity);
    }
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return !this.requiredBlockEntity && !(Boolean)state.getValue(IN_STRUCTURE) ? null : new LaserHeaterBlockEntity(pos, state);
    }
}
