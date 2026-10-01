package com.eibrahs.tinkerstranscend.block.entity.controller;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.block.controller.RoyalFoundryControllerBlock;
import com.eibrahs.tinkerstranscend.block.entity.TinkersTranscendBlockEntities;
import com.eibrahs.tinkerstranscend.block.entity.multiblock.RoyalFoundryMultiblock;
import com.eibrahs.tinkerstranscend.block.menu.RoyalFoundryContainerMenu;
import com.eibrahs.tinkerstranscend.network.AlloyTankSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.ByproductMeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.module.alloying.MultiAlloyingModule;
import slimeknights.tconstruct.smeltery.block.entity.module.alloying.SmelteryAlloyTank;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock;
import slimeknights.tconstruct.smeltery.block.entity.tank.ISmelteryTankHandler;
import slimeknights.tconstruct.smeltery.block.entity.tank.SmelteryTank;
import slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu;

import javax.annotation.Nullable;

public class RoyalFoundryBlockEntity extends HeatingStructureBlockEntity {
    private static final int CAPACITY_PER_BLOCK = 720;
    private static final Component NAME = TConstruct.makeTranslation("gui", "royal_foundry");
    private static final int BLOCKS_PER_FUEL = 18;
    private int laserLevel = 0;
    private int laserTick = 0;
    private final SmelteryTank<RoyalFoundryBlockEntity> alloyTankSource;
    private final SmelteryAlloyTank alloyTank;
    private final MultiAlloyingModule alloyingModule;
    private boolean alloyFluidUpdateQueued;
    public RoyalFoundryBlockEntity(BlockPos pos, BlockState state) {
        super(TinkersTranscendBlockEntities.ROYAL_FOUNDRY.get(), pos, state, NAME);
        this.alloyTankSource = new SmelteryTank<>(this);
        this.alloyTank = new SmelteryAlloyTank(alloyTankSource);
        this.alloyingModule = new MultiAlloyingModule(this, this.alloyTank);
        this.alloyFluidUpdateQueued = false;

    }

    @Override
    protected HeatingStructureMultiblock<?> createMultiblock() {
        return new RoyalFoundryMultiblock(this);
    }

    @Override
    protected MeltingModuleInventory createMeltingInventory() {
        return new ByproductMeltingModuleInventory(this, this.tank, Config.COMMON.foundryOreRate);
    }

    @Override
    protected boolean isDebugItem(ItemStack stack) {
        return stack.is(TinkerTags.Items.FOUNDRY_DEBUG);
    }

    @Override
    protected void heat() {
        if (this.structure != null && this.level != null) {
            if (this.structure.hasTanks()) {
                boolean entityMelted = false;
                if (this.tick == 12) {
                    entityMelted = this.entityModule.interactWithEntities();
                }

                switch (this.tick % 4) {
                    case 0:
                        if (!this.fuelModule.hasFuel()&&laserLevel==0) {
                            if (entityMelted) {
                                this.fuelModule.findFuel(true);
                            } else {
                                int possibleTemp = this.fuelModule.findFuel(false);
                                this.alloyTank.setTemperature(possibleTemp);
                                if (this.meltingInventory.canHeat(possibleTemp) || this.alloyingModule.canAlloy()) {
                                    this.fuelModule.findFuel(true);
                                }
                            }
                        }
                        break;
                    case 1:
                        if (this.fuelModule.hasFuel()) {
                            this.meltingInventory.heatItems(this.fuelModule.getTemperature(), this.fuelModule.getRate());
                        } else {
                            this.meltingInventory.coolItems();
                        }
                    case 2:
                        if (this.fuelModule.hasFuel()) {
                            this.alloyTank.setTemperature(this.fuelModule.getTemperature());
                            this.alloyingModule.doAlloy();
                        }
                        break;
                    case 3:
                        boolean hasFuel = this.fuelModule.hasFuel();
                        BlockState state = this.getBlockState();
                        if ((Boolean)state.getValue(ControllerBlock.ACTIVE) != hasFuel) {
                            this.level.setBlockAndUpdate(this.worldPosition, (BlockState)state.setValue(ControllerBlock.ACTIVE, hasFuel));
                        }

                        if (laserLevel==0) this.fuelModule.decreaseFuel(this.fuelRate);
                }
            } else {
                this.cool();
            }

        }
    }

    @Override
    protected void setStructure(@Nullable HeatingStructureMultiblock.StructureData structure) {
        super.setStructure(structure);
        if (structure != null) {
            int dx = structure.getInnerX();
            int dy = structure.getInnerY();
            int dz = structure.getInnerZ();
            this.tank.setCapacity(720 * (dx + 2) * (dy + 1) * (dz + 2));
            this.alloyTankSource.setCapacity(720 * (dx + 2) * (dy + 1) * (dz + 2));
            this.meltingInventory.resize(dx * dy * dz, this.dropItem);
            this.fuelRate = 1 + (2 * (dx + 2) * dy + 2 * dy * dz + (dx + 2) * (dz + 2)) / 18;
        }

    }
    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level,pos,state);
        if (!level.isClientSide && level.getGameTime() % 40 == 0) {
            this.checkStructure();
        }
        laserTick();
        if (state.hasProperty(ControllerBlock.IN_STRUCTURE)) {
            if (this.structure != null && state.getValue(RoyalFoundryControllerBlock.IN_STRUCTURE)) {
                if (this.tick % 4 == 3 && this.alloyFluidUpdateQueued) {
                    this.alloyFluidUpdateQueued = false;
                    // 不要用 syncFluids()，改用自定义包
                    PacketDistributor.sendToPlayersTrackingChunk(
                            (ServerLevel) level,
                            level.getChunkAt(pos).getPos(),
                            new AlloyTankSyncPacket(pos, this.alloyTankSource.getFluids())
                    );
                }
            }
        }
    }
    @Override
    public void notifyFluidsChanged(ISmelteryTankHandler.FluidChange type, FluidStack fluid) {
        super.notifyFluidsChanged(type, fluid);
        this.alloyFluidUpdateQueued = true;
        if (type == FluidChange.ADDED) {
            this.alloyingModule.clearCachedRecipes();
        }
    }
    public void setLaserLevel(int laserLevel){
        this.laserLevel = laserLevel;
        laserTick = 20;
        this.fuelModule.set(0,200);
        this.fuelModule.set(1,200);
        this.fuelModule.set(2,laserLevel*500);
        this.fuelModule.set(3,14+laserLevel);
    }
    public void resetLaserLevel(){
        laserLevel = 0;
        this.fuelModule.set(2,getFuelModule().getFuelInfo().getTemperature());
        this.updateStructure();
    }
    protected  void laserTick(){
        if (laserTick>0){
            laserTick--;
        }else{
            resetLaserLevel();
        }
    }
    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new RoyalFoundryContainerMenu(id, inv, this);
    }
    public SmelteryTank<RoyalFoundryBlockEntity> getAlloyTank(){
        return alloyTankSource;
    }
    @Override
    protected void saveSynced(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveSynced(compound,registries);
        compound.put("alloy_tank",this.alloyTankSource.write(new CompoundTag(),registries));
    }
    @Override
    public void load(CompoundTag nbt, HolderLookup.Provider registries) {
        super.load(nbt,registries);
        if(nbt.contains("alloy_tank",10)){
            this.alloyTankSource.read(nbt.getCompound("alloy_tank"), registries);
         }
    }

}
