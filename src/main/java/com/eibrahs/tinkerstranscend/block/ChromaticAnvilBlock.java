package com.eibrahs.tinkerstranscend.block;

import com.eibrahs.tinkerstranscend.block.entity.ChromaticAnvilEntity;
import com.eibrahs.tinkerstranscend.block.entity.TinkersTranscendBlockEntities;
import com.eibrahs.tinkerstranscend.block.menu.ChromaticAnvilMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
public class ChromaticAnvilBlock extends AnvilBlock implements EntityBlock {
    public ChromaticAnvilBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // 用 SimpleMenuProvider 闭包捕获 pos，在这里查 BlockEntity
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChromaticAnvilEntity guiBe) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, inv, p) ->
                                new ChromaticAnvilMenu(containerId, inv, guiBe.getInventory(), ContainerLevelAccess.create(level,pos)),
                        Component.translatable("block.tinkers_transcend.chromatic_anvil")
                ), pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ChromaticAnvilEntity(TinkersTranscendBlockEntities.CHROMATIC_ANVIL_ENTITY.get(), blockPos,blockState);
    }
}
