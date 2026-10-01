package com.eibrahs.tinkerstranscend.block;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.block.component.LaserHeaterBlock;
import com.eibrahs.tinkerstranscend.block.component.RoyalBlock;
import com.eibrahs.tinkerstranscend.block.controller.*;
import com.eibrahs.tinkerstranscend.item.BlockTooltipItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import slimeknights.tconstruct.smeltery.block.component.*;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryInputOutputBlockEntity;

@Mod(TinkersTranscend.MODID)
public class TinkersTranscendBlocks {
    public static final DeferredBlock<TimeWarpCastingTableBlock> TIME_WARP_CASTING_TABLE;
    public static final DeferredItem<BlockTooltipItem> TIME_WARP_CASTING_TABLE_ITEM;
    public static final DeferredBlock<TimeWarpCastingBasinBlock> TIME_WARP_CASTING_BASIN;
    public static final DeferredItem<BlockTooltipItem> TIME_WARP_CASTING_BASIN_ITEM;
    public static final DeferredBlock<TimeWarpSmelterBlock> SEARED_TIME_WARP_SMELTER;
    public static final DeferredItem<BlockTooltipItem> SEARED_TIME_WARP_SMELTER_ITEM;
    public static final DeferredBlock<TimeWarpSmelterBlock> SCORCHED_TIME_WARP_SMELTER;
    public static final DeferredItem<BlockTooltipItem> SCORCHED_TIME_WARP_SMELTER_ITEM;
    public static final DeferredBlock<TimeWarpSmelterBlock> ROYAL_TIME_WARP_SMELTER;
    public static final DeferredItem<BlockTooltipItem> ROYAL_TIME_WARP_SMELTER_ITEM;
    public static final DeferredBlock<Block> SOURCE_ROCK;
    public static final DeferredItem<BlockItem> SOURCE_ROCK_ITEM;

    public static final DeferredBlock<RoyalFoundryControllerBlock> ROYAL_FOUNDRY_CONTROLLER;
    public static final DeferredItem<BlockTooltipItem> ROYAL_FOUNDRY_CONTROLLER_ITEM;
    public static final DeferredBlock<RoyalBlock> ROYAL_STEEL_BRICK;
    public static final DeferredItem<BlockTooltipItem> ROYAL_STEEL_BRICK_ITEM;
    public static final DeferredBlock<SearedDrainBlock> ROYAL_FOUNDRY_DRAIN;
    public static final DeferredItem<BlockTooltipItem> ROYAL_FOUNDRY_DRAIN_ITEM;
    public static final DeferredBlock<SearedDuctBlock> ROYAL_FOUNDRY_DUCT;
    public static final DeferredItem<BlockTooltipItem> ROYAL_FOUNDRY_DUCT_ITEM;
    public static final DeferredBlock<RetexturedOrientableSmelteryBlock> ROYAL_FOUNDRY_CHUTE;
    public static final DeferredItem<BlockTooltipItem> ROYAL_FOUNDRY_CHUTE_ITEM;
    public static final DeferredBlock<SearedTankBlock> ROYAL_TANK;
    public static final DeferredItem<BlockTooltipItem> ROYAL_TANK_ITEM;
    public static final DeferredBlock<LaserHeaterBlock> ROYAL_LASER_HEATER;
    public static final DeferredItem<BlockTooltipItem> ROYAL_LASER_HEATER_ITEM;

    public static final DeferredBlock<ChromaticAnvilBlock> CHROMATIC_ANVIL;
    public static final DeferredItem<BlockTooltipItem> CHROMATIC_ANVIL_ITEM;

    private static final BlockBehaviour.StatePredicate NEVER = (state, level, pos) -> false;
    private static BlockBehaviour.Properties seared(){
        return BlockBehaviour.Properties.of().sound(SoundType.METAL).mapColor(MapColor.TERRACOTTA_BROWN).strength(2.5F, 8.0F).isValidSpawn(Blocks::never).isRedstoneConductor(NEVER).isSuffocating(NEVER).isViewBlocking(NEVER).noOcclusion().forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().isValidSpawn(SearedBlock.VALID_SPAWN);
    }
    private static BlockBehaviour.Properties scorched(){
        return BlockBehaviour.Properties.of().sound(SoundType.BASALT).mapColor(MapColor.TERRACOTTA_BROWN).strength(2.5F, 8.0F).isValidSpawn(Blocks::never).isRedstoneConductor(NEVER).isSuffocating(NEVER).isViewBlocking(NEVER).noOcclusion().forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().isValidSpawn(SearedBlock.VALID_SPAWN);
    }
    private static BlockBehaviour.Properties royal(){
        return BlockBehaviour.Properties.of().sound(SoundType.BASALT).mapColor(MapColor.TERRACOTTA_BROWN).strength(5F, 1200.0F).isValidSpawn(Blocks::never).isRedstoneConductor(NEVER).isSuffocating(NEVER).isViewBlocking(NEVER).noOcclusion().forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().isValidSpawn(SearedBlock.VALID_SPAWN);
    }
    static{
        TIME_WARP_CASTING_TABLE = TinkersTranscend.BLOCKS.register("time_warp_casting_table",
                ()-> new TimeWarpCastingTableBlock(seared(),false));
        TIME_WARP_CASTING_TABLE_ITEM = TinkersTranscend.ITEMS.register("time_warp_casting_table",
                key->new BlockTooltipItem(TIME_WARP_CASTING_TABLE.get(),new Item.Properties()));
        TIME_WARP_CASTING_BASIN =TinkersTranscend.BLOCKS.register("time_warp_casting_basin",
                ()-> new TimeWarpCastingBasinBlock(seared(),false));
        TIME_WARP_CASTING_BASIN_ITEM = TinkersTranscend.ITEMS.register("time_warp_casting_basin",
                key->new BlockTooltipItem(TIME_WARP_CASTING_BASIN.get(),new Item.Properties()));

        SEARED_TIME_WARP_SMELTER = TinkersTranscend.BLOCKS.register("seared_time_warp_smelter",
                ()-> new TimeWarpSmelterBlock(seared().noOcclusion().lightLevel(state->TimeWarpSmelterBlock.lightLevel(state,4)),false));
        SEARED_TIME_WARP_SMELTER_ITEM = TinkersTranscend.ITEMS.register("seared_time_warp_smelter", key->new BlockTooltipItem(SEARED_TIME_WARP_SMELTER.get(),new Item.Properties()));
        SCORCHED_TIME_WARP_SMELTER = TinkersTranscend.BLOCKS.register("scorched_time_warp_smelter",
                ()-> new TimeWarpSmelterBlock(scorched().noOcclusion().lightLevel(state->TimeWarpSmelterBlock.lightLevel(state,5)),false));
        SCORCHED_TIME_WARP_SMELTER_ITEM = TinkersTranscend.ITEMS.register("scorched_time_warp_smelter", key->new BlockTooltipItem(SCORCHED_TIME_WARP_SMELTER.get(), new Item.Properties()));

        ROYAL_FOUNDRY_CONTROLLER = TinkersTranscend.BLOCKS.register("royal_foundry_controller",
                ()->new RoyalFoundryControllerBlock(royal()));
        ROYAL_FOUNDRY_CONTROLLER_ITEM = TinkersTranscend.ITEMS.register("royal_foundry_controller",key->new BlockTooltipItem(ROYAL_FOUNDRY_CONTROLLER.get(),new Item.Properties()));
        ROYAL_STEEL_BRICK = TinkersTranscend.BLOCKS.register("royal_steel_brick",
                ()->new RoyalBlock(royal(),false));
        ROYAL_STEEL_BRICK_ITEM = TinkersTranscend.ITEMS.register("royal_steel_brick", key->new BlockTooltipItem(ROYAL_STEEL_BRICK.get(),new Item.Properties()));
        ROYAL_FOUNDRY_DRAIN = TinkersTranscend.BLOCKS.register("royal_foundry_drain",
                ()->new SearedDrainBlock(royal()));
        ROYAL_FOUNDRY_DRAIN_ITEM = TinkersTranscend.ITEMS.register("royal_foundry_drain",key->new BlockTooltipItem(ROYAL_FOUNDRY_DRAIN.get(),new Item.Properties()));
        ROYAL_FOUNDRY_DUCT = TinkersTranscend.BLOCKS.register("royal_foundry_duct",
                ()->new SearedDuctBlock(royal()));
        ROYAL_FOUNDRY_DUCT_ITEM = TinkersTranscend.ITEMS.register("royal_foundry_duct",key->new BlockTooltipItem(ROYAL_FOUNDRY_DUCT.get(),new Item.Properties()));
        ROYAL_FOUNDRY_CHUTE = TinkersTranscend.BLOCKS.register("royal_foundry_chute",
                ()->new RetexturedOrientableSmelteryBlock(royal(), SmelteryInputOutputBlockEntity.ChuteBlockEntity::new));
        ROYAL_FOUNDRY_CHUTE_ITEM = TinkersTranscend.ITEMS.register("royal_foundry_chute",key->new BlockTooltipItem(ROYAL_FOUNDRY_CHUTE.get(),new Item.Properties()));
        ROYAL_TIME_WARP_SMELTER = TinkersTranscend.BLOCKS.register("royal_time_warp_smelter",
                ()-> new TimeWarpSmelterBlock(royal().noOcclusion().lightLevel(state->TimeWarpSmelterBlock.lightLevel(state,7)),false));
        ROYAL_TIME_WARP_SMELTER_ITEM = TinkersTranscend.ITEMS.register("royal_time_warp_smelter", key->new BlockTooltipItem(ROYAL_TIME_WARP_SMELTER.get(), new Item.Properties()));
        ROYAL_TANK = TinkersTranscend.BLOCKS.register("royal_tank",
                ()->new SearedTankBlock(royal(),16000, PushReaction.DESTROY));
        ROYAL_TANK_ITEM = TinkersTranscend.ITEMS.register("royal_tank",key->new BlockTooltipItem(ROYAL_TANK.get(),new Item.Properties()));
        ROYAL_LASER_HEATER = TinkersTranscend.BLOCKS.register("royal_laser_heater",
                ()->new LaserHeaterBlock(royal(),false));
        ROYAL_LASER_HEATER_ITEM = TinkersTranscend.ITEMS.register("royal_laser_heater",key->new BlockTooltipItem(ROYAL_LASER_HEATER.get(),new Item.Properties()));

        CHROMATIC_ANVIL = TinkersTranscend.BLOCKS.register("chromatic_anvil",
                ()-> new ChromaticAnvilBlock(BlockBehaviour.Properties.of().isRedstoneConductor((state, level, pos) -> false).mapColor(MapColor.EMERALD).sound(SoundType.AMETHYST)));
        CHROMATIC_ANVIL_ITEM = TinkersTranscend.ITEMS.register("chromatic_anvil", key->new BlockTooltipItem(CHROMATIC_ANVIL.get(), new Item.Properties()));
        SOURCE_ROCK = TinkersTranscend.BLOCKS.registerSimpleBlock("source_rock");
        SOURCE_ROCK_ITEM = TinkersTranscend.ITEMS.registerSimpleBlockItem(SOURCE_ROCK);
    }
}
