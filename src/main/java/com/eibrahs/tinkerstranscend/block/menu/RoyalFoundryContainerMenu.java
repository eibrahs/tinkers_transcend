package com.eibrahs.tinkerstranscend.block.menu;

import com.eibrahs.tinkerstranscend.block.entity.controller.RoyalFoundryBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer;
import slimeknights.mantle.util.sync.ValidZeroDataSlot;
import slimeknights.tconstruct.shared.inventory.TriggeringMultiModuleContainerMenu;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.module.MultitankFuelModule;
import slimeknights.tconstruct.smeltery.block.entity.tank.SmelteryTank;
import slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu;
import slimeknights.tconstruct.smeltery.menu.TransferDirectionSupplier;
import slimeknights.tconstruct.tables.menu.module.SideInventoryContainer;

import java.util.function.Consumer;

public class RoyalFoundryContainerMenu extends TriggeringMultiModuleContainerMenu<RoyalFoundryBlockEntity> implements TransferDirectionSupplier{
    private final SideInventoryContainer<RoyalFoundryBlockEntity> sideInventory;
    private final Container bucketContainer;
    private IFluidContainerTransfer.TransferDirection transferDirection;
    private final Slot bucketResultSlot;
    public static final int TANK_INDEX = 100;
    private static final IFluidContainerTransfer.TransferDirection[] TRANSFER_DIRECTIONS;


    public RoyalFoundryContainerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getTileEntityFromBuf(buf, RoyalFoundryBlockEntity.class));
    }
    public RoyalFoundryContainerMenu(int id, @javax.annotation.Nullable Inventory inv, @javax.annotation.Nullable RoyalFoundryBlockEntity structure) {
        super(TinkersTranscendMenus.ROYAL_FOUNDRY_MENU.get(), id, inv, structure);
        this.transferDirection = IFluidContainerTransfer.TransferDirection.AUTO;
        this.bucketContainer = new SimpleContainer(2);
        if (inv != null && structure != null) {
            if (!inv.player.level().isClientSide) {
                IFluidHandler tank = structure.getTank();
                this.addSlot(new HeatingStructureContainerMenu.BucketInputSlot(this.bucketContainer, 125, 46, tank, this, inv.player));
                this.bucketResultSlot = this.addSlot(new HeatingStructureContainerMenu.BucketResultSlot(this.bucketContainer, 125, 104, tank, this, inv.player));
            } else {
                this.addSlot(new HeatingStructureContainerMenu.BucketSlot(this.bucketContainer, 0, 125, 46));
                this.bucketResultSlot = this.addSlot(new HeatingStructureContainerMenu.ResultSlot(this.bucketContainer, 1, 125, 104));
            }

            MeltingModuleInventory inventory = structure.getMeltingInventory();
            this.sideInventory = new SideInventoryContainer(TinkersTranscendMenus.ROYAL_FOUNDRY_MENU.get(), id, inv, structure, 0, 0, calcColumns(inventory.getSlots()));
            this.addSubContainer(this.sideInventory, true);

            Consumer<DataSlot> referenceConsumer = this::addDataSlot;
            ValidZeroDataSlot.trackIntArray(referenceConsumer, structure.getFuelModule());
            inventory.trackInts((array) -> ValidZeroDataSlot.trackIntArray(referenceConsumer, array));
        } else {
            this.sideInventory = null;
            this.bucketResultSlot = null;
        }

        this.addInventorySlots();
    }

    protected int getInventoryYOffset() {
        return 138;
    }

    private void updateBucket(Player player) {
        if (!player.level().isClientSide && this.tile != null) {
            ItemStack bucket = this.bucketContainer.getItem(0);
            if (!bucket.isEmpty() && this.bucketContainer.getItem(1).isEmpty()) {
                IFluidContainerTransfer.TransferResult result = FluidTransferHelper.interactWithStack((this.tile).getTank(), bucket, this.transferDirection);
                if (result != null) {
                    FluidTransferHelper.playUISound(player, result.getSound());
                    this.bucketContainer.setItem(0, bucket);
                    this.bucketContainer.setItem(1, result.stack());
                    this.bucketContainer.setChanged();
                }
            }
        }

    }

    public boolean clickMenuButton(Player player, int id) {
        if (player.isSpectator()) {
            return false;
        } else if (id == 0) {
            this.transferDirection = TRANSFER_DIRECTIONS[(this.transferDirection.ordinal() + 1) % TRANSFER_DIRECTIONS.length];
            this.updateBucket(player);
            return true;
        } else if (1 <= id && id <= 3) {
            ItemStack held = this.getCarried();
            if (held.isEmpty()) {
                return false;
            } else {
                if (!player.level().isClientSide && this.tile != null) {
                    IFluidContainerTransfer.TransferResult result;
                    if (id == 1) {
                        MultitankFuelModule fuelModule = (this.tile).getFuelModule();
                        result = FluidTransferHelper.fillStack(fuelModule, held, fuelModule.getLastFluid());
                    } else {
                        result = FluidTransferHelper.interactWithStack((IFluidHandler)(id == 2 ? this.tile.getFuelModule() : this.tile.getTank()), held, IFluidContainerTransfer.TransferDirection.EMPTY_ITEM);
                    }

                    this.setCarried(FluidTransferHelper.handleUIResult(player, held, result));
                }

                return true;
            }
        } else if (id == 4) {
            if (!player.level().isClientSide && this.tile != null) {
                transferFluid(this.tile.getTank(), this.tile.getAlloyTank());
            }
            return true;
        } else if (id == 5) {
            if (!player.level().isClientSide && this.tile != null) {
                transferFluid(this.tile.getAlloyTank(), this.tile.getTank());
            }
            return true;
        } else if (id == 6) {
            if (!player.level().isClientSide && this.tile != null) {
                ItemStack held = this.getCarried();
                if (!held.isEmpty()) {
                    IFluidContainerTransfer.TransferResult result =
                            FluidTransferHelper.interactWithStack(
                                    this.tile.getAlloyTank(), held,
                                    IFluidContainerTransfer.TransferDirection.EMPTY_ITEM);
                    this.setCarried(FluidTransferHelper.handleUIResult(player, held, result));
                }
            }
            return true;
        } else {
            if (id >= TANK_INDEX && this.tile != null) {
                int index = id - TANK_INDEX;
                int mainTankSize = getMainTankSize();
                int alloyTankSize = getAlloyTankSize();
                if (index < mainTankSize) {
                    return handleTankClick(player, this.tile.getTank(), index);
                } else if (index < mainTankSize + alloyTankSize) {
                    return handleTankClick(player, this.tile.getAlloyTank(), index - mainTankSize);
                }

            }

            return false;
        }
    }
    private void transferFluid(SmelteryTank<?> from, SmelteryTank<?> to) {
        if (from.getFluids().isEmpty()) return;

        FluidStack fluid = from.getFluidInTank(0);
        if (fluid.isEmpty()) return;

        FluidStack toFill = fluid.copy();
        int filled = to.fill(toFill, IFluidHandler.FluidAction.EXECUTE);

        if (filled > 0) {
            from.drain(fluid.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
        }
    }
    private boolean handleTankClick(Player player, SmelteryTank<?> tank, int index) {
        FluidStack fluid = tank.getFluidInTank(index);
        if (fluid.isEmpty()) return false;
        if (!player.level().isClientSide) {
            ItemStack held = this.getCarried();
            if (!held.isEmpty()) {
                IFluidContainerTransfer.TransferResult result = FluidTransferHelper.fillStack(tank, held, fluid);
                this.setCarried(FluidTransferHelper.handleUIResult(player, held, result));
            } else {
                tank.moveFluidToBottom(index);
                this.updateBucket(player);
            }
        }
        return true;
    }
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            this.clearContainer(player, this.bucketContainer);
        }

    }

    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot != this.bucketResultSlot && super.canTakeItemForPickAll(pStack, pSlot);
    }

    public static int calcColumns(int slots) {
        return Math.min(4, (slots + 6) / 7);
    }

    public SideInventoryContainer<RoyalFoundryBlockEntity> getSideInventory() {
        return this.sideInventory;
    }

    public Container getBucketContainer() {
        return this.bucketContainer;
    }

    public IFluidContainerTransfer.TransferDirection getTransferDirection() {
        return this.transferDirection;
    }
    public int getMainTankSize() {
        return tile != null ? tile.getTank().getFluids().size() : 0;
    }
    public int getAlloyTankSize() {
        return tile != null ? tile.getAlloyTank().getFluids().size() : 0;
    }
    public int getTemperature(){
        return tile != null ? tile.getFuelModule().getTemperature() : 0;
    }
    static {
        TRANSFER_DIRECTIONS = new IFluidContainerTransfer.TransferDirection[]{IFluidContainerTransfer.TransferDirection.AUTO, IFluidContainerTransfer.TransferDirection.EMPTY_ITEM, IFluidContainerTransfer.TransferDirection.FILL_ITEM};
    }
}
