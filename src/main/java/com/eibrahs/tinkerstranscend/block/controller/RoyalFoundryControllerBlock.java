package com.eibrahs.tinkerstranscend.block.controller;

//import com.eibrahs.tinkerstranscend.block.entity.controller.RoyalFoundryBlockEntity;
import com.eibrahs.tinkerstranscend.block.entity.TinkersTranscendBlockEntities;
import com.eibrahs.tinkerstranscend.block.entity.controller.RoyalFoundryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.controller.HeatingControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.controller.FoundryBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;

public class RoyalFoundryControllerBlock extends HeatingControllerBlock {
    public RoyalFoundryControllerBlock(Properties builder) {
        super(builder);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RoyalFoundryBlockEntity(pos,state);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type) {
        return HeatingStructureBlockEntity.getTicker(pLevel, type, TinkersTranscendBlockEntities.ROYAL_FOUNDRY.get());
    }
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @javax.annotation.Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockEntityHelper.get(RoyalFoundryBlockEntity.class, worldIn, pos).ifPresent(HeatingStructureBlockEntity::updateStructure);
    }
    @Override
    @Deprecated
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!newState.is(this)) {
            BlockEntityHelper.get(RoyalFoundryBlockEntity.class, worldIn, pos).ifPresent(foundry -> {
                dropInventory(worldIn, pos, foundry.getMeltingInventory());
                foundry.invalidateStructure();
            });
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (state.getValue(ACTIVE)) {
            double x = (double)pos.getX() + (double)0.5F;
            double y = (double)pos.getY() + (double)((rand.nextFloat() * 6.0F + 2.0F) / 16.0F);
            double z = (double)pos.getZ() + (double)0.5F;
            double frontOffset = 0.52;
            double sideOffset = rand.nextDouble() * 0.6 - 0.3;
            this.spawnFireParticles(world, state, x, y, z, frontOffset, sideOffset, ParticleTypes.SOUL_FIRE_FLAME);
        }

    }
    /** @deprecated */
    @Deprecated
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.getValue(IN_STRUCTURE) ? state : super.rotate(state, rotation);
    }

    /** @deprecated */
    @Deprecated
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.getValue(IN_STRUCTURE) ? state : super.mirror(state, mirror);
    }
}
