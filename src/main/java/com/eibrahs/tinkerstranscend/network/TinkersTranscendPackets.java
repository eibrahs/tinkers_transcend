package com.eibrahs.tinkerstranscend.network;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.tinker.modifiers.MultiphaseModifierHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = TinkersTranscend.MODID)
public class TinkersTranscendPackets {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(RenameItemPacket.TYPE, RenameItemPacket.STREAM_CODEC,
                RenameItemPacket::handle
        );
        registrar.playToServer(
                MultiphaseModifierHandler.SwitchPhasePacket.TYPE,
                MultiphaseModifierHandler.SwitchPhasePacket.CODEC,
                MultiphaseModifierHandler::handleSwitchPhase
        );
        registrar.playToServer(
                MultiphaseModifierHandler.SelectPhasePacket.TYPE,
                MultiphaseModifierHandler.SelectPhasePacket.STREAM_CODEC,
                MultiphaseModifierHandler::handleSelectPhase
        );

        registrar.playToClient(
                AlloyTankSyncPacket.TYPE,
                AlloyTankSyncPacket.STREAM_CODEC,
                AlloyTankSyncPacket::handle
        );
    }

}
