package com.eibrahs.tinkerstranscend.block.entity.multiblock;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.block.entity.controller.RoyalFoundryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.MultiblockCuboid;

public class RoyalFoundryMultiblock extends HeatingStructureMultiblock<RoyalFoundryBlockEntity> {
    public static final TagKey<Block> BLOCK = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"royal_foundry"));
    public static final TagKey<Block> FLOOR = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"royal_foundry/floor"));
    public static final TagKey<Block> TANKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"royal_foundry/tanks"));
    public static final TagKey<Block> WALL = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"royal_foundry/wall"));
    public static final TagKey<Block> CEIL = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"royal_foundry/ceil"));


    public RoyalFoundryMultiblock(RoyalFoundryBlockEntity royal) {
        super(royal, true, true, false);
    }

    protected boolean isValidBlock(Block block) {
        return block.builtInRegistryHolder().is(BLOCK);
    }

    protected boolean isValidFloor(Block block) {
        return block.builtInRegistryHolder().is(FLOOR);
    }

    protected boolean isValidTank(Block block) {
        return block.builtInRegistryHolder().is(TANKS);
    }

    protected boolean isValidWall(Block block) {
        return block.builtInRegistryHolder().is(WALL);
    }
    @Override
    protected boolean isValidBlock(Level world, BlockPos pos, MultiblockCuboid.CuboidSide side, boolean isFrame) {
        if (pos.equals(this.parent.getBlockPos())) {
            return true;
        } else {
            BlockState state = world.getBlockState(pos);
            if (!this.isValidSlave(world, pos)) {
                return false;
            } else if (this.isValidTank(state.getBlock())) {
                this.tanks.add(pos.immutable());
                return true;
            } else if (side == CuboidSide.FLOOR && !isFrame) {
                return this.isValidFloor(state.getBlock());
            } else {
                return this.isValidWall(state.getBlock());
            }
        }
    }
}