package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.module.weapon.SweepWeaponAttack;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

public class WarAxeModifier extends Modifier implements
        AttributesModifierHook,
        MeleeDamageModifierHook,
        TooltipModifierHook,
        VolatileDataModifierHook

{
    private static final String modifierName = "war_axe";
    public WarAxeModifier(){};
    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.ATTRIBUTES,
                ModifierHooks.MELEE_DAMAGE,
                ModifierHooks.TOOLTIP,
                ModifierHooks.VOLATILE_DATA
        });
    }
    public @NotNull Component getDisplayName(int level) {
        return applyStyle(Component.translatable(getTranslationKey()));
    }
    public int getPriority() {return 10;}
    public float getMeleeDamage(IToolStackView var1, ModifierEntry var2, ToolAttackContext context, float baseDamage, float damage){
        float cd =  context.getCriticalModifier();
        return damage * cd;
    }
    //AttributesModifierHook
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot!= EquipmentSlot.OFFHAND) {
            consumer.accept(Attributes.ENTITY_INTERACTION_RANGE.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(
                    TinkersTranscend.MODID, "attribute_modifier.entity_interaction_range"), 1.5,
                    AttributeModifier.Operation.ADD_VALUE));
        }
    }
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
        volatileData.putFloat(SweepWeaponAttack.SWEEP_PERCENT, volatileData.getFloat(SweepWeaponAttack.SWEEP_PERCENT) + 1.0f);
    }
    public void addTooltip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, @NotNull List<Component> tooltip, @NotNull TooltipKey tooltipKey, @NotNull TooltipFlag var6) {
        if (tool.hasTag(TinkerTags.Items.HARVEST) || tool.hasTag(TinkerTags.Items.MELEE_WEAPON)) {
            TooltipModifierHook.addPercentBoost(modifier.getModifier(), Component.translatable("modifier."+TinkersTranscend.MODID+"." + modifierName + ".tooltip.critical_damage"),0.75f, tooltip);
            TooltipModifierHook.addPercentBoost(modifier.getModifier(), Component.translatable("modifier."+TinkersTranscend.MODID+"." + modifierName + ".tooltip.sweep_damage"),1.0f, tooltip);
        }
    }
}
