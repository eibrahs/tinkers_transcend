package com.eibrahs.tinkerstranscend.block;

import com.eibrahs.tinkerstranscend.block.entity.TimeWarpSmelterEntity;
import com.eibrahs.tinkerstranscend.block.entity.TinkersTranscendBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

import javax.annotation.Nullable;

public class TimeWarpSmelterBlock extends SearedBlock {
    private static final BooleanProperty  ACTIVE = BooleanProperty.create("active");
    public TimeWarpSmelterBlock(BlockBehaviour.Properties properties,boolean requiredBlockEntity) {
        super(properties,requiredBlockEntity);
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, true));
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != TinkersTranscendBlockEntities.TIME_WARP_SMELTER_ENTITY.get()) {
            return null;
        }
        return level.isClientSide ? null : TimeWarpSmelterEntity::tick;
    }
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
         return !(Boolean)state.getValue(IN_STRUCTURE) ? null : new TimeWarpSmelterEntity(pos, state);
    }
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state,level,pos,neighborBlock,neighborPos,movedByPiston);
        if (level.getGameTime()%40!=0&&!level.isClientSide&&state.getValue(ACTIVE)) {
            TimeWarpSmelterEntity e = ((TimeWarpSmelterEntity)level.getBlockEntity(pos));
            if (e != null) {
                e.timeWarpSmelt();
            }
        }
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.isClientSide)){
            level.setBlock(pos, state.cycle(ACTIVE), 3);
            SoundEvent event = state.getValue(ACTIVE)?SoundEvents.BEACON_DEACTIVATE:SoundEvents.BEACON_ACTIVATE;
            level.playSound(null, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }
    public static int lightLevel(BlockState state,int lightLevel){
        return state.getValue(ACTIVE)?lightLevel:0;
    }
    public static boolean isActive(BlockState state){
        return state.getValue(ACTIVE);
    }
    @Override
    public void onRemove(BlockState oldState, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(oldState,world,pos,newState,isMoving);
        if (oldState.getValue(ACTIVE)){
            world.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (state.getValue(ACTIVE)){
            world.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }
}
