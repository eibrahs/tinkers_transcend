package com.eibrahs.tinkerstranscend.block.entity;

import com.eibrahs.tinkerstranscend.block.entity.controller.RoyalFoundryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentBlockEntity;


public class LaserHeaterBlockEntity extends SmelteryComponentBlockEntity implements ILaserLevelReceiver {
    private int receivedLaserLevel = 0;
    public LaserHeaterBlockEntity(BlockPos pos,BlockState blockstate){
        this(TinkersTranscendBlockEntities.LASER_HEATER_ENTITY.get(),pos,blockstate);
    }
    public LaserHeaterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void onLaserLevelReceived(int laserLevel) {
        this.receivedLaserLevel = laserLevel;
        this.setChanged();
        RoyalFoundryBlockEntity master = ((RoyalFoundryBlockEntity) this.level.getBlockEntity(getMasterPos()));
        if (master!= null ){
            master.setLaserLevel(laserLevel);
        }
    }
}
