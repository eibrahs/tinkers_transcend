package com.eibrahs.tinkerstranscend.network;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.block.menu.ChromaticAnvilMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record RenameItemPacket(int containerId, String newName) implements CustomPacketPayload {
    public static final Type<RenameItemPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "rename_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RenameItemPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RenameItemPacket::containerId,  // 编码 containerId
            ByteBufCodecs.STRING_UTF8, RenameItemPacket::newName,  // 编码 newName
            RenameItemPacket::new                                  // 解码时用这些值构造对象
    );
    private void name(ItemStack stack){
        if (Objects.equals(this.newName, "")){
            stack.remove(DataComponents.CUSTOM_NAME);
            return;
        }
        stack.set(DataComponents.CUSTOM_NAME, comp(newName).withStyle(Style.EMPTY.withItalic(false)));
    }
    private MutableComponent comp(String str){
        MutableComponent comps = Component.empty();
        String[] strs= ("&r"+str+"&r").split("&");
        Style lastStyle = Style.EMPTY;
        for (int i = 0; i < strs.length; i++) {
            String s = strs[i];
            if (s.isEmpty()) {
                if (i > 0) comps.append(Component.literal("&").withStyle(lastStyle));
                continue;
            }
            ChatFormatting format = ChatFormatting.getByCode(s.charAt(0));
            if (format == null) {
                String prefix = (i > 0) ? "&" : "";
                comps.append(Component.literal(prefix + s).withStyle(lastStyle));
            } else {
                lastStyle = (format == ChatFormatting.RESET)
                        ? Style.EMPTY
                        : lastStyle.applyFormat(format);
                comps.append(Component.literal(s.substring(1)).withStyle(lastStyle));
            }
        }
        if(comps.getSiblings().getFirst().getString().isEmpty())
            comps.getSiblings().removeFirst();
        comps.getSiblings().removeLast();
        return comps;
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static void handle(RenameItemPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // 获取服务端玩家和菜单
            if (context.player() instanceof ServerPlayer serverPlayer) {
                AbstractContainerMenu menu = serverPlayer.containerMenu;
                if (menu instanceof ChromaticAnvilMenu sorterMenu) {
                    ItemStack stack = sorterMenu.getSlot(1).getItem();
                    if (!stack.isEmpty()) {
                        payload.name(stack);
                        // 修改物品名称
                        // 标记槽位变化，触发同步
                        sorterMenu.getSlot(1).setChanged();
                    }
                }
            }
        });
    }
}