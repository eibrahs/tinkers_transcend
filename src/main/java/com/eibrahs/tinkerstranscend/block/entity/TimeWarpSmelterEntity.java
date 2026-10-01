package com.eibrahs.tinkerstranscend.block.entity;

import com.eibrahs.tinkerstranscend.block.TimeWarpSmelterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.common.multiblock.IMasterLogic;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;


public class TimeWarpSmelterEntity extends SmelteryComponentBlockEntity {
    public TimeWarpSmelterEntity(BlockPos pos, BlockState state) {
        super(TinkersTranscendBlockEntities.TIME_WARP_SMELTER_ENTITY.get(), pos, state);
    }
    @Override
    public void setPotentialMaster(IMasterLogic master) {
        super.setPotentialMaster(master);
   }
    private BlockEntity getMasterEntity(){
        if (this.getMasterPos() != null) {
            if (getLevel() != null) {
                return getLevel().getBlockEntity(this.getMasterPos());
            }
        }
        return null;
    }
    public void timeWarpSmelt(){
        BlockEntity e = getMasterEntity();
        if (e==null||level == null) return;
        HeatingStructureBlockEntity master = ((HeatingStructureBlockEntity)getMasterEntity());
        if (!(master.getMeltingInventory().canHeat(master.getFuelModule().getTemperature()))) return;
        CompoundTag tag = e.saveCustomAndMetadata(level.registryAccess());
        ListTag items = tag.getCompound("inventory").getList("items", CompoundTag.TAG_COMPOUND);

        if (items.isEmpty())return;
        for(int i=0;i<items.size();i++){
            CompoundTag item = items.getCompound(i);
            item.putInt("time",2000000000);
        }

        e.loadWithComponents(tag, level.registryAccess());
        e.setChanged();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if ((!level.isClientSide)
                &&(level.getGameTime()%40==0)
                &&(TimeWarpSmelterBlock.isActive(blockState))) {
            TimeWarpSmelterEntity e = ((TimeWarpSmelterEntity)level.getBlockEntity(blockPos));
            if (e != null) {
                e.timeWarpSmelt();
            }
        }
    }
}
