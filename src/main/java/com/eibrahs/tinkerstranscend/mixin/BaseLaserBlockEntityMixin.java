package com.eibrahs.tinkerstranscend.mixin;

import com.eibrahs.tinkerstranscend.block.entity.ILaserLevelReceiver;
import dev.dubhe.anvilcraft.block.entity.BaseLaserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseLaserBlockEntity.class)
public class BaseLaserBlockEntityMixin extends BlockEntity{

    @Shadow
    protected BlockPos irradiateBlockPos;

    public BaseLaserBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "emitLaser", at = @At("TAIL"))
    private void onEmitLaserTail(Direction direction, CallbackInfo ci) {
        if (this.level == null || this.irradiateBlockPos == null) return;

        BlockEntity be = this.level.getBlockEntity(this.irradiateBlockPos);
        if (be instanceof ILaserLevelReceiver receiver) {
            BaseLaserBlockEntity self = (BaseLaserBlockEntity) (Object) this;
            receiver.onLaserLevelReceived(self.getLaserLevel());
        }
    }
}