package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TinkersTranscendModifiers {
    public static final ModifierDeferredRegister MODIFIERS =
            ModifierDeferredRegister.create(TinkersTranscend.MODID);
    public static final ResourceKey<? extends Registry<Modifier>> REGISTRY_KEY = ResourceKey.createRegistryKey(TinkersTranscend.getResource("modifiers"));

    public static final StaticModifier<MatureModifier> MATURE_MODIFIER =
            MODIFIERS.register("mature", MatureModifier::new);
    public static final StaticModifier<FerociousModifier> FEROCIOUS_MODIFIER =
            MODIFIERS.register("ferocious", FerociousModifier::new);
    public static final StaticModifier<EternalModifier> ETERNAL_MODIFIER =
            MODIFIERS.register("eternal", EternalModifier::new);
    public static final StaticModifier<ProvidenceModifier> PROVIDENCE_MODIFIER =
            MODIFIERS.register("providence", ProvidenceModifier::new);
    public static final StaticModifier<MultiphaseModifier> MULTIPHASE_MODIFIER =
            MODIFIERS.register("multiphase", MultiphaseModifier::new);
    public static final StaticModifier<MercilessModifier> MERCILESS_MODIFIER =
            MODIFIERS.register("merciless", MercilessModifier::new);
    public static final StaticModifier<FireReforgingModifier> FIRE_REFORGING_MODIFIER =
            MODIFIERS.register("fire_reforging", FireReforgingModifier::new);
    public static final StaticModifier<WarAxeModifier> WAR_AXE_MODIFIER =
            MODIFIERS.register("war_axe", WarAxeModifier::new);
    public static final StaticModifier<VisionModifier> VISION_MODIFIER =
            MODIFIERS.register("vision",VisionModifier::new);
    public static final StaticModifier<RadianceModifier> RADIANCE_MODIFIER =
            MODIFIERS.register("radiance",RadianceModifier::new);
    public static final StaticModifier<CarvingModifier> CARVING_MODIFIER =
            MODIFIERS.register("carving", CarvingModifier::new);
    public static void register(IEventBus modEventBus) {
        MODIFIERS.register(modEventBus);
    }
    public static TagKey<Modifier> getTag(ResourceLocation id) {
        return TagKey.create(REGISTRY_KEY, id);
    }

    public static boolean hasModifier(ItemStack item, ModifierId modifierId){
        if (!ToolStack.isInitialized(item)) return false;
        return ToolStack.from(item).getModifier(modifierId)!=ModifierEntry.EMPTY;

    }
    public static boolean hasUpgrade(ItemStack item, ModifierId modifierId){
        if (!ToolStack.isInitialized(item)) return false;
        return ToolStack.copyFrom(item).getModifier(modifierId)!=ModifierEntry.EMPTY;

    }
}
/*
1.添加标签：
    "data\tconstruct\tinkering\tags\modifiers"
2.添加文本颜色：
    “assets\mantle\colors.json”
3.(可选)添加配方：
    “data\tinkers_transcend\recipe\tools\modifiers”
 4.(可选)添加到匠魂手册：
    “data\tconstruct\tinkering\tags\modifiers”中的对应类型json
    ”assets\tconstruct\book\“在对应书本的对应语言中添加额外描述(UTF-8编码)
*/