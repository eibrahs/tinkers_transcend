package com.eibrahs.tinkerstranscend.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentBlockEntity;

public class RoyalFoundryComponentEntity extends SmelteryComponentBlockEntity {
    public RoyalFoundryComponentEntity(BlockPos pos, BlockState state) {
        this(TinkersTranscendBlockEntities.ROYAL_FOUNDRY_COMPONENT.get(),pos, state);
    }

    protected RoyalFoundryComponentEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
