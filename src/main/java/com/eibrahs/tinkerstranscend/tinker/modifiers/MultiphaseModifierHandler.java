package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.TinkersTranscendClient;
import com.eibrahs.tinkerstranscend.TinkersTranscendComponents;
import dev.dubhe.anvilcraft.client.init.ModKeyMappings;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@EventBusSubscriber(modid = TinkersTranscend.MODID)
public class MultiphaseModifierHandler {
    public static void handleSwitchPhase(SwitchPhasePacket payload, IPayloadContext context){
        context.enqueueWork(() -> {
            TinkersTranscend.LOGGER.info("SwitchPhasePacket Payload");
            ServerPlayer player = (ServerPlayer) context.player();
            switchPhase(player.getWeaponItem());
        });
    }
    public static void handleSelectPhase(SelectPhasePacket payload, IPayloadContext context){
        ServerPlayer player = (ServerPlayer) context.player();
        ItemStack item = player.getItemBySlot(getEquipmentSlotByInt(payload.slot));
        if (!MultiphaseModifier.isInitPhase(item)) return;
        if (payload.isUpgrade) {
            item.get(TinkersTranscendComponents.TTMULTIPHASE).selectUpgrade(item,payload.phaseIndex);
        }else{
            item.get(TinkersTranscendComponents.TTMULTIPHASE).selectEnchantment(item,payload.phaseIndex);
        }
    }
    public record SwitchPhasePacket() implements CustomPacketPayload {
        public static final Type<SwitchPhasePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "switch_phase"));
        public static final StreamCodec<ByteBuf, SwitchPhasePacket> CODEC = StreamCodec.unit(new SwitchPhasePacket());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    public record SelectPhasePacket(int hash,int slot, int phaseIndex, boolean isUpgrade) implements CustomPacketPayload{
        public static final Type<SelectPhasePacket> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "select_phase"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SelectPhasePacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                SelectPhasePacket::hash,
                ByteBufCodecs.INT,
                SelectPhasePacket::slot,
                ByteBufCodecs.INT,
                SelectPhasePacket::phaseIndex,
                ByteBufCodecs.BOOL,
                SelectPhasePacket::isUpgrade,
                SelectPhasePacket::new
        );
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            KeyMapping key = ModKeyMappings.SWITCH_PHASE.get();
            if (key.isDown()&& TinkersTranscendClient.KEY_MULTIPHASE_PRESS_TICK<10){
                    TinkersTranscendClient.KEY_MULTIPHASE_PRESS_TICK ++;
            } else if (TinkersTranscendClient.KEY_MULTIPHASE_PRESS_TICK>=10){
                TinkersTranscendClient.KEY_MULTIPHASE_PRESS_TICK = 0;
                Minecraft.getInstance().setScreen(new MultiphaseModifierUI(player));
            }else if (TinkersTranscendClient.KEY_MULTIPHASE_PRESS_TICK>0&& TinkersTranscendClient.KEY_MULTIPHASE_PRESS_TICK<10){
                TinkersTranscendClient.KEY_MULTIPHASE_PRESS_TICK = 0;
                PacketDistributor.sendToServer(new SwitchPhasePacket());
                switchPhase(player.getWeaponItem());
            }
        }
    }
    private static void switchPhase(ItemStack stack){
        if(!ToolStack.isInitialized(stack)) return;
        if(!MultiphaseModifier.isInitPhase(stack)) return;
        ToolStack tool = ToolStack.from(stack);
        stack.get(TinkersTranscendComponents.TTMULTIPHASE).cycleEnchantments(stack);
        stack.get(TinkersTranscendComponents.TTMULTIPHASE).cycleUpgrades(stack);
        //TinkersTranscendium.LOGGER.info("SwitchPhase");
    }
    public static EquipmentSlot getEquipmentSlotByInt(int slotIndex){
        switch(slotIndex){
            case 1: return EquipmentSlot.OFFHAND;
            case 2: return EquipmentSlot.FEET;
            case 3: return EquipmentSlot.LEGS;
            case 4: return EquipmentSlot.CHEST;
            case 5: return EquipmentSlot.HEAD;
            case 0:
            default: return EquipmentSlot.MAINHAND;
        }
    }
    /*@SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(
                SwitchPhasePacket.TYPE,
                SwitchPhasePacket.CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        TinkersTranscend.LOGGER.info("SwitchPhasePacket Payload");
                        ServerPlayer player = (ServerPlayer) context.player();
                        switchPhase(player.getWeaponItem());
                    });
                }
        );
        registrar.playToServer(
                SelectPhasePacket.TYPE,
                SelectPhasePacket.STREAM_CODEC,
                (payload,context)->{
                    ServerPlayer player = (ServerPlayer) context.player();
                    ItemStack item = player.getItemBySlot(getEquipmentSlotByInt(payload.slot));
                    if (!MultiphaseModifier.isInitPhase(item)) return;
                    if (payload.isUpgrade) {
                        item.get(TinkersTranscendComponents.TTMULTIPHASE).selectUpgrade(item,payload.phaseIndex);
                    }else{
                        item.get(TinkersTranscendComponents.TTMULTIPHASE).selectEnchantment(item,payload.phaseIndex);
                    }
                }
        );
    }*/
}
