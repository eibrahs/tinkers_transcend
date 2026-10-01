package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class MercilessModifier extends Modifier implements
        EnchantmentModifierHook,
        ToolStatsModifierHook,
        TooltipModifierHook,
        ModifierRemovalHook

{
    private static final String modifierName = "merciless";
    private final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, modifierName);

    public MercilessModifier(){}
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.ENCHANTMENTS,
                ModifierHooks.TOOL_STATS,
                ModifierHooks.TOOLTIP,
                ModifierHooks.REMOVE
        });
    }
    public @NotNull Component getDisplayName(int level) {
        return applyStyle(Component.translatable(getTranslationKey()));
    }
    public int getPriority() {
        return 1004;
    }
    public Component onRemoved(IToolStackView tool, Modifier modifier){
        if (tool.getModifierLevel(this.getId()) == 0) {
            tool.getPersistentData().remove(this.KEY);
        }
        return null;
    }

    public int updateEnchantmentLevel(@NotNull IToolStackView var1, @NotNull ModifierEntry var2, @NotNull Holder< Enchantment > var3, int var4) {
        return 0;
    }

    public void updateEnchantments(IToolStackView tool, @NotNull ModifierEntry modifier, HolderLookup.@NotNull RegistryLookup<Enchantment> lookup, Map<Holder<Enchantment>, Integer> enchantments){
        ModDataNBT persistentData = tool.getPersistentData();
        int count = 0;

        for( Map.Entry<Holder<Enchantment>, Integer> entry:enchantments.entrySet()){
            count += entry.getValue();
        }
        persistentData.putInt(KEY,count);
    }
    public void addToolStats(IToolContext tool, @NotNull ModifierEntry modifier, @NotNull ModifierStatsBuilder builder){
        IModDataView persistentData = tool.getPersistentData();
        if (persistentData.contains(KEY, Tag.TAG_INT )) {
            int value = persistentData.getInt(KEY);
            if (tool.hasTag(TinkerTags.Items.HARVEST)||tool.hasTag(TinkerTags.Items.MELEE_WEAPON)) {
                ToolStats.ATTACK_DAMAGE.add(builder, (int) (2.0D * Math.sqrt(value) + 0.33D * (double) value));
                ToolStats.MINING_SPEED.add(builder, persistentData.getInt(KEY));
            }
            if (tool.hasTag(TinkerTags.Items.ARMOR)) {
                ToolStats.ARMOR.add(builder,(int) (2.0D * Math.sqrt(value) + 0.33D * (double) value));
                ToolStats.KNOCKBACK_RESISTANCE.add(builder, persistentData.getInt(KEY) * 0.01f);
            }
        }
    }

    public void addTooltip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, @NotNull List<Component> tooltip, @NotNull TooltipKey tooltipKey, @NotNull TooltipFlag var6){
        if (player != null) {

            ModDataNBT persistentData = tool.getPersistentData();
            if (persistentData.contains(this.KEY, Tag.TAG_INT )) {
                float value = persistentData.getInt(this.KEY);
                if (tool.hasTag(TinkerTags.Items.HARVEST)||tool.hasTag(TinkerTags.Items.MELEE_WEAPON)) {
                    tooltip.add(Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.text_tool").withColor(modifier.getModifier().getColor()));

                    TooltipModifierHook.addFlatBoost(modifier.getModifier(),Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.attack_damage"),(int)(2.0D * Math.sqrt(value)+0.33D * (double)value) ,tooltip);
                    TooltipModifierHook.addFlatBoost(modifier.getModifier(),Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.mining_speed"),value ,tooltip);
                }
                if (tool.hasTag(TinkerTags.Items.ARMOR)) {
                    tooltip.add(Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.text_armor").withColor(modifier.getModifier().getColor()));

                    TooltipModifierHook.addFlatBoost(modifier.getModifier(),Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.armor"),(int)(2.0D * Math.sqrt(value)+0.33D * (double)value) ,tooltip);
                    TooltipModifierHook.addPercentBoost(modifier.getModifier(),Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.knockback_resistance"),value * 0.01f ,tooltip);

                }
            }
        }
    }
}
