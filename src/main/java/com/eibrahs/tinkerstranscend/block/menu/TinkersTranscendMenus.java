package com.eibrahs.tinkerstranscend.block.menu;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
@Mod(TinkersTranscend.MODID)
    public class TinkersTranscendMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(Registries.MENU, TinkersTranscend.MODID);

    public static final Supplier<MenuType<ChromaticAnvilMenu>> EnchantmentSorterMenu =
        MENUS.register("chromatic_anvil", () -> IMenuTypeExtension.create((containerId, inventory, data) ->
            new ChromaticAnvilMenu(containerId, inventory)));
    public static final Supplier<MenuType<RoyalFoundryContainerMenu>> ROYAL_FOUNDRY_MENU =
        MENUS.register("royal_foundry", () -> IMenuTypeExtension.create((containerId,inventory,data)->
            new RoyalFoundryContainerMenu(containerId,inventory, data)));
}
