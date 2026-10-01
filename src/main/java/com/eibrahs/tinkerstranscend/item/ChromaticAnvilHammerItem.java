package com.eibrahs.tinkerstranscend.item;

import com.eibrahs.tinkerstranscend.block.menu.ChromaticAnvilMenu;
import dev.dubhe.anvilcraft.init.ModMenuTypes;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.inventory.*;
import dev.dubhe.anvilcraft.item.AnvilHammerItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ChromaticAnvilHammerItem extends AnvilHammerItem {

    public ChromaticAnvilHammerItem(Properties properties) {
        super(properties);
    }
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide && livingEntity instanceof ServerPlayer player) {
            int slot = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
            openPortableAnvil(player, slot);
        }

        return stack;
    }

    public static void openPortableAnvil(Player player, int inventorySlot) {
        if (player instanceof ServerPlayer serverPlayer) {
            OpenedHammerSource source = OpenedHammerSource.fromInventory(serverPlayer.getInventory(), inventorySlot);
            openPortableAnvil(serverPlayer, source);
        }
    }

    private static void openPortableAnvil(ServerPlayer serverPlayer, @Nullable OpenedHammerSource source) {
        if (source != null) {
            if (serverPlayer.containerMenu.getCarried().isEmpty()) {
                if (serverPlayer.containerMenu != serverPlayer.inventoryMenu) {
                    serverPlayer.closeContainer();
                }

                MenuProvider provider = new SimpleMenuProvider((id, playerInventory, menuPlayer) -> createPortableAnvilMenu(id, playerInventory, source), Component.translatable("container.repair"));
                ModMenuTypes.open(serverPlayer, provider);
            }
        }
    }

    public static void openPortableAnvilFromMenuSlot(Player player, int menuSlotId) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.containerMenu.getCarried().isEmpty()) {
                if (menuSlotId >= 0 && menuSlotId < serverPlayer.containerMenu.slots.size()) {
                    Slot slot = serverPlayer.containerMenu.getSlot(menuSlotId);
                    OpenedHammerSource source = OpenedHammerSource.fromMenuSlot(slot, serverPlayer.getInventory());
                    openPortableAnvil(serverPlayer, source);
                }
            }
        }
    }

    private static AbstractContainerMenu createPortableAnvilMenu(int id, Inventory playerInventory, OpenedHammerSource source) {
        Item hammerItem = source.openedHammerItem();
        return new ChromaticAnvilMenu(id,playerInventory);
    }
}
