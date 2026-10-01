package com.eibrahs.tinkerstranscend.block.menu;

import com.eibrahs.tinkerstranscend.block.TinkersTranscendBlocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
public class ChromaticAnvilMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    public ChromaticAnvilMenu(int containerId, Inventory playerInventory) {
        this(containerId,playerInventory,new ItemStackHandler(2),ContainerLevelAccess.NULL);
    }
    public ChromaticAnvilMenu(int containerId, Inventory playerInventory, IItemHandlerModifiable blockInventory, ContainerLevelAccess access){
        super(TinkersTranscendMenus.EnchantmentSorterMenu.get(),containerId);
        this.access = access;
        // 输入槽：玩家可以自由放入/取出
        this.addSlot(new SlotItemHandler(blockInventory, 0, 27, 47){
            @Override
            public void setChanged() {
                super.setChanged();
                updateResult();
            }
            private void updateResult() {
                ItemStack input = blockInventory.getStackInSlot(0);
                if (!getSlot(0).hasItem()) {
                    getSlot(1).set(ItemStack.EMPTY);
                    return;
                }
                ItemStack result = input.copy()/* 计算 */;
                getSlot(1).set(result);
            }
        });

        this.addSlot(new SlotItemHandler(blockInventory, 1, 134, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;   // 关键：玩家不能手动放东西进输出槽
            }
            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                if (getSlot(1).hasItem()) {
                    getSlot(0).getItem().setCount(getSlot(1).getItem().getCount());
                }else{
                    getSlot(0).set(ItemStack.EMPTY);
                }
                // 取出后，输入槽内容没变，但需要刷新输出槽的显示
                // 强制同步一次所有槽位
                ChromaticAnvilMenu.this.broadcastChanges();
                player.level().playSound(
                        null,                    // 玩家（null 表示所有玩家都听到）
                        player.blockPosition(),  // 声音位置
                        SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,   // 音效事件
                        SoundSource.BLOCKS,      // 音效类别
                        1.0f,                    // 音量
                        1.0f                     // 音调
                );
            }
        });
        // 玩家主背包 3行9列 (索引 9-35)，起始坐标 (8, 84)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18));
            }
        }

        // 玩家快捷栏 1行9列 (索引 0-8)，起始坐标 (8, 142)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    8 + col * 18, 142));
        }
    }

    public String getCurrentName(){
        if (getSlot(0).hasItem()){
            return getSlot(0).getItem().getOrDefault(DataComponents.ITEM_NAME, Component.translatable("")).getString();
        }
        return "";
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        final int BLOCK_SLOTS = 2;                          // 方块容器 2 格
        final int PLAYER_START = BLOCK_SLOTS;               // 玩家背包起始
        final int PLAYER_END = BLOCK_SLOTS + 36;            // 玩家背包结束

        if (index < BLOCK_SLOTS) {
            // 从方块容器 → 玩家背包
            if (!this.moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, copy);
        } else {
            // 从玩家背包 → 只能进输入槽（索引 0）
            if (!this.moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access,player, TinkersTranscendBlocks.CHROMATIC_ANVIL.get());
    }
    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        // 检查：是否为右键（button == 1）的普通拾取（PICKUP）操作
        // 并且点击的是右侧输出槽（索引 1）
//        if (button == 1 && clickType == ClickType.PICKUP && slotId == 1) {
//            Slot slot = this.getSlot(slotId);
//            if (slot.hasItem()) {
//                // 模拟左键“全部取出”的行为
//                ItemStack stack = slot.getItem();
//                // 将物品放到玩家鼠标上
//                this.setCarried(stack.copy());
//                // 清空槽位
//                slot.set(ItemStack.EMPTY);
//                // 触发同步更新
//                this.broadcastChanges();
//                return;
//            }
            //super.clicked(1,0,ClickType.PICKUP,player);
 //       }
        // 其他情况交给原版逻辑处理
        super.clicked(slotId, button, clickType, player);
    }
    @Override
    public void removed(Player player) {
        super.removed(player);

        if (player instanceof ServerPlayer) {
            ItemStack stack = getSlot(0).getItem();
            if (!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack.copy());
                getSlot(0).set(ItemStack.EMPTY);
            }
        }
    }
}
