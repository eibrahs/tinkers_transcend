package com.eibrahs.tinkerstranscend;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

public class TinkersTranscendMaterials {
    public static final MaterialId transcendium = createMaterial("transcendium");
    public static final MaterialId royal_steel = createMaterial("royal_steel");
    public static final MaterialId ember = createMaterial("ember_metal");
    public static final MaterialId frost_metal = createMaterial("frost_metal");
    public static final MaterialId transcendthread = createMaterial("transcendthread");
    public static final MaterialId emberthread = createMaterial("emberthread");
    public static final MaterialId frosthread = createMaterial("frosthread");

    public static MaterialId createMaterial(String name) {
        return new MaterialId(ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, name)); //*是你的模组主类名
    }
}
