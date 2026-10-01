package com.eibrahs.tinkerstranscend.tinker;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.item.ModifiableHeavyHalberdItem;
import com.eibrahs.tinkerstranscend.item.ModifiableResonatorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredItem;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

@Mod(TinkersTranscend.MODID)
public class TinkersTranscendTools {
    protected static final Item.Properties UNSTACKABLE_PROPS;
    public static final DeferredItem<ModifiableItem> WAR_AXE;

    public static final DeferredItem<ToolPartItem> RESONATOR_CORE;
    public static final DeferredItem<ToolPartItem> RESONATOR_SHELL;
    public static final DeferredItem<ModifiableResonatorItem> RESONATOR;
    public static final DeferredItem<ToolPartItem> HEAVY_HALBERD_CORE;
    public static final DeferredItem<ToolPartItem> HEAVY_HALBERD_GEAR;
    public static final DeferredItem<ModifiableHeavyHalberdItem> HEAVY_HALBERD;
    public static final DeferredItem<Item> HEAVY_HALBERD_GEAR_CAST;
    public static final DeferredItem<Item> RESONATOR_SHELL_CAST;
    public static final DeferredItem<ModifiableItem> HUNTING_KNIFE;

    static{
        UNSTACKABLE_PROPS = (new Item.Properties()).stacksTo(1);
        WAR_AXE = TinkersTranscend.ITEMS.register("war_axe", () -> new ModifiableItem(
                UNSTACKABLE_PROPS,
                ToolDefinition.create(ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "war_axe"))
        ));
        RESONATOR = TinkersTranscend.ITEMS.register("resonator", () -> new ModifiableResonatorItem(
                UNSTACKABLE_PROPS,
                ToolDefinition.create(ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "resonator"))
        ));
        RESONATOR_CORE = TinkersTranscend.ITEMS.register("resonator_core",()->new ToolPartItem(
                new Item.Properties(),
                StatlessMaterialStats.BINDING.getIdentifier()
        ));
        RESONATOR_SHELL = TinkersTranscend.ITEMS.register("resonator_shell",()->new ToolPartItem(
                new Item.Properties(),
                HeadMaterialStats.ID
        ));
        HEAVY_HALBERD_CORE = TinkersTranscend.ITEMS.register("heavy_halberd_core",()->new ToolPartItem(
                new Item.Properties(),
                StatlessMaterialStats.BINDING.getIdentifier()
        ));
        HEAVY_HALBERD_GEAR = TinkersTranscend.ITEMS.register("heavy_halberd_gear",()->new ToolPartItem(
                new Item.Properties(),
                HeadMaterialStats.ID
        ));
        HEAVY_HALBERD = TinkersTranscend.ITEMS.register("heavy_halberd",()->new ModifiableHeavyHalberdItem(
                UNSTACKABLE_PROPS,
                ToolDefinition.create(ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"heavy_halberd"))
        ));
        HEAVY_HALBERD_GEAR_CAST = TinkersTranscend.ITEMS.registerSimpleItem("heavy_halberd_gear_cast");
        RESONATOR_SHELL_CAST = TinkersTranscend.ITEMS.registerSimpleItem("resonator_shell_cast");
        HUNTING_KNIFE = TinkersTranscend.ITEMS.register("hunting_knife", () -> new ModifiableItem(
                UNSTACKABLE_PROPS,
                ToolDefinition.create(ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "hunting_knife"))
        ));
    }
}
/*
新增工具：
1.注册工具类型
2.定义工具数据
    data/modid/tinkering/tool_definitions
3.定义工匠站/工匠砧外观、添加配方
    data/modid/tinkering/station_layouts
    data/modid/tools/building
4.创建模型和贴图
    asset/modid/models/item 物品模型
    asset/modid/textures/item/tool 灰度贴图
5.添加Tags
    data/tconstruct/tags/item/modifiable
    data/modid/tags/block/mineable
6.加入创造模式物品栏
7.添加百科全书内容

新增部件：
1.注册部件
    注册时继承部件类型以获取部件数据
2.创建部件模型和贴图
    asset/modid/textures/item/tool/parts 部件贴图
    asset/modid/textures/gui/tinker_pattern 部件UI图标
    asset/modid/models/item 物品模型
3.添加Tags
    data/tconstruct/tags/item/parts.json
制作配方...?/所有材料的批量配方...?
5.创造模式物品栏
* */