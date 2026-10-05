package com.eibrahs.tinkerstranscend.item;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredItem;

@Mod(TinkersTranscend.MODID)
public class TinkersTranscendItems {
    public static final DeferredItem<FoxSoulBottleItem> FOX_SOUL_BOTTLE;
    public static final DeferredItem<Item> BROKEN_BOTTLE;
    public static final DeferredItem<Item> RESENTFUL_STEEL_INGOT;
    public static final DeferredItem<Item> RESENTFUL_STEEL_NUGGET;
    public static final DeferredItem<Item> FOX_SOUL_STEEL_INGOT;
    public static final DeferredItem<Item> FOX_SOUL_STEEL_NUGGET;
    public static final DeferredItem<FoxSoulArrowItem> FOX_SOUL_ARROW;
    public static final DeferredItem<ChromaticAnvilHammerItem> CHROMATIC_ANVIL_HAMMER;

    static{
        CHROMATIC_ANVIL_HAMMER  = TinkersTranscend.ITEMS.registerItem("chromatic_anvil_hammer",ChromaticAnvilHammerItem::new);
        FOX_SOUL_BOTTLE = TinkersTranscend.ITEMS.registerItem("fox_soul_bottle",FoxSoulBottleItem::new,new Item.Properties().rarity(Rarity.RARE).stacksTo(16));
        BROKEN_BOTTLE = TinkersTranscend.ITEMS.registerItem("broken_bottle",Item::new,new Item.Properties());
        RESENTFUL_STEEL_INGOT = TinkersTranscend.ITEMS.registerItem("resentful_steel_ingot",Item::new,new Item.Properties());
        RESENTFUL_STEEL_NUGGET = TinkersTranscend.ITEMS.registerItem("resentful_steel_nugget",Item::new,new Item.Properties());
        FOX_SOUL_STEEL_INGOT = TinkersTranscend.ITEMS.registerItem("fox_soul_steel_ingot",Item::new,new Item.Properties().rarity(Rarity.RARE));
        FOX_SOUL_STEEL_NUGGET = TinkersTranscend.ITEMS.registerItem("fox_soul_steel_nugget",Item::new,new Item.Properties().rarity(Rarity.RARE));
        FOX_SOUL_ARROW = TinkersTranscend.ITEMS.registerItem("fox_soul_arrow",FoxSoulArrowItem::new,new Item.Properties().rarity(Rarity.RARE));
    }
}
