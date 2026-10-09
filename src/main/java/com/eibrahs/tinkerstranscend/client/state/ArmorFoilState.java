package com.eibrahs.tinkerstranscend.client.state;

import net.minecraft.world.item.ItemStack;

public final class ArmorFoilState {

    private static ItemStack capturedArmorStack = ItemStack.EMPTY;

    private ArmorFoilState() {}

    public static ItemStack get() {
        return capturedArmorStack;
    }

    public static void set(ItemStack stack) {
        capturedArmorStack = stack == null ? ItemStack.EMPTY : stack;
    }
}