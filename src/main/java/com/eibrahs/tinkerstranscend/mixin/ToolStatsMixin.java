package com.eibrahs.tinkerstranscend.mixin;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

@Mixin(ToolStats.class)
public class ToolStatsMixin {

    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lslimeknights/tconstruct/library/tools/stat/FloatToolStat;<init>(Lslimeknights/tconstruct/library/tools/stat/ToolStatId;IFFFLnet/minecraft/tags/TagKey;)V"
            )
    )
    private static void modifyArmorMaxValue(Args args) {
        ToolStatId id = args.get(0);
        if (id.equals(new ToolStatId(TConstruct.MOD_ID, "armor"))) {
            args.set(4, 100000f); // 把 maxValue 从 30 改成 100
        }
        if (id.equals(new ToolStatId(TConstruct.MOD_ID, "armor_toughness"))) {
            args.set(4, 100000f); // 把 maxValue 从 30 改成 100
        }
    }
}