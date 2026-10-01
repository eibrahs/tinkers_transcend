package com.eibrahs.tinkerstranscend.network;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.block.entity.controller.RoyalFoundryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record AlloyTankSyncPacket(BlockPos pos, List<FluidStack> fluids)
        implements CustomPacketPayload {

    public static final Type<AlloyTankSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "alloy_tank_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyTankSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, AlloyTankSyncPacket::pos,
                    FluidStack.STREAM_CODEC.apply(ByteBufCodecs.list()), AlloyTankSyncPacket::fluids,
                    AlloyTankSyncPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static void handle(AlloyTankSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            BlockEntity be = level.getBlockEntity(packet.pos());
            if (be instanceof RoyalFoundryBlockEntity controller) {
                // 直接更新 alloyTankSource 的液体列表
                controller.getAlloyTank().setFluids(packet.fluids());
            }
        });
    }
}