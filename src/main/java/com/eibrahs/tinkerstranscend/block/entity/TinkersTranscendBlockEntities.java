package com.eibrahs.tinkerstranscend.block.entity;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.block.TinkersTranscendBlocks;
import com.eibrahs.tinkerstranscend.block.component.LaserHeaterBlock;
import com.eibrahs.tinkerstranscend.block.entity.controller.RoyalFoundryBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.function.Supplier;
@Mod(TinkersTranscend.MODID)
@EventBusSubscriber(modid = TinkersTranscend.MODID)
public class TinkersTranscendBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TinkersTranscend.MODID);

    public static final Supplier<BlockEntityType<ChromaticAnvilEntity>> CHROMATIC_ANVIL_ENTITY;
    public static final Supplier<BlockEntityType<TimeWarpSmelterEntity>> TIME_WARP_SMELTER_ENTITY;
    public static final Supplier<BlockEntityType<TimeWarpCastingBlockEntity.Table>> TIME_WARP_CASTING_TABLE_ENTITY;
    public static final Supplier<BlockEntityType<TimeWarpCastingBlockEntity.Basin>> TIME_WARP_CASTING_BASIN_ENTITY;
    public static final Supplier<BlockEntityType<RoyalFoundryBlockEntity>> ROYAL_FOUNDRY;
    public static final Supplier<BlockEntityType<RoyalFoundryComponentEntity>> ROYAL_FOUNDRY_COMPONENT;
    public static final Supplier<BlockEntityType<LaserHeaterBlockEntity>> LASER_HEATER_ENTITY;
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK, TIME_WARP_CASTING_BASIN_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getTank() // 返回你的方块实体内部的流体储罐
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK, TIME_WARP_CASTING_TABLE_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getTank()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK, ROYAL_FOUNDRY.get(),
                (blockEntity, side) -> blockEntity.getMeltingInventory()
        );
    }
    @SubscribeEvent
    public static void onAddBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(TinkerSmeltery.drain.get(), TinkersTranscendBlocks.ROYAL_FOUNDRY_DRAIN.get());
        event.modify(TinkerSmeltery.duct.get(),TinkersTranscendBlocks.ROYAL_FOUNDRY_DUCT.get());
        event.modify(TinkerSmeltery.chute.get(),TinkersTranscendBlocks.ROYAL_FOUNDRY_CHUTE.get());
        event.modify(TinkerSmeltery.tank.get(),TinkersTranscendBlocks.ROYAL_TANK.get());
    }
    static{
        CHROMATIC_ANVIL_ENTITY = BLOCK_ENTITIES.register("chromatic_anvil", () ->
                BlockEntityType.Builder.of(
                        ChromaticAnvilEntity::new,
                        TinkersTranscendBlocks.CHROMATIC_ANVIL.get()
                ).build(null)
        );
        TIME_WARP_SMELTER_ENTITY = BLOCK_ENTITIES.register("time_warp_smelter",()->
                        BlockEntityType.Builder.of(
                                TimeWarpSmelterEntity::new,
                                TinkersTranscendBlocks.SCORCHED_TIME_WARP_SMELTER.get(),
                                TinkersTranscendBlocks.SEARED_TIME_WARP_SMELTER.get(),
                                TinkersTranscendBlocks.ROYAL_TIME_WARP_SMELTER.get()
                        ).build(null)
                );
        TIME_WARP_CASTING_TABLE_ENTITY = BLOCK_ENTITIES.register("time_warp_casting_table",()->
                        BlockEntityType.Builder.of(
                                TimeWarpCastingBlockEntity.Table::new,
                                TinkersTranscendBlocks.TIME_WARP_CASTING_TABLE.get()
                        ).build(null)
                );
        TIME_WARP_CASTING_BASIN_ENTITY = BLOCK_ENTITIES.register("time_warp_casting_basin",()->
                BlockEntityType.Builder.of(
                        TimeWarpCastingBlockEntity.Basin::new,
                        TinkersTranscendBlocks.TIME_WARP_CASTING_BASIN.get()
                ).build(null)
        );
        ROYAL_FOUNDRY = BLOCK_ENTITIES.register("royal_foundry",()->
                BlockEntityType.Builder.of(
                        RoyalFoundryBlockEntity::new,
                        TinkersTranscendBlocks.ROYAL_FOUNDRY_CONTROLLER.get()
                ).build(null)
        );
        ROYAL_FOUNDRY_COMPONENT = BLOCK_ENTITIES.register("royal_foundry_component",()->
                BlockEntityType.Builder.of(
                        RoyalFoundryComponentEntity::new,
                        TinkersTranscendBlocks.ROYAL_STEEL_BRICK.get()
                ).build(null)
        );
        LASER_HEATER_ENTITY = BLOCK_ENTITIES.register("laser_heater",()->
                BlockEntityType.Builder.of(
                        LaserHeaterBlockEntity::new,
                        TinkersTranscendBlocks.ROYAL_LASER_HEATER.get()
                ).build(null)
        );
    }
}
