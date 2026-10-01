package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.TTConfig;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MatureModifier extends Modifier  implements
        ModifierRemovalHook,
        ToolDamageModifierHook,
        InventoryTickModifierHook,
        ToolStatsModifierHook,
        TooltipModifierHook
{
    private static final String modifierName = "mature";
    private final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, modifierName);

    public MatureModifier(){

    }
    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
//        hookBuilder.addModule(
//                StatBoostModule.add(ToolStats.ATTACK_DAMAGE).flat(1.0f)
//        );
//
//        hookBuilder.addModule(
//                StatBoostModule.add(ToolStats.ARMOR).flat(1.0f)
//        );
//
//        hookBuilder.addModule(
//                StatBoostModule.add(ToolStats.MINING_SPEED).eachLevel(1.0f)
//        );
//
//        hookBuilder.addModule(
//                StatBoostModule.add(ToolStats.DURABILITY).eachLevel(1000.0f)
//        );
//
//        hookBuilder.addModule(
//                ModifierSlotModule.slot(SlotType.UPGRADE).maxLevel(5).eachLevel(1)
//        );
//        hookBuilder.addModule(
//                new RarityModule(Rarity.COMMON)
//        );
        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.INVENTORY_TICK,
                ModifierHooks.TOOLTIP,
                ModifierHooks.TOOL_DAMAGE,
                ModifierHooks.TOOL_STATS
        });
    }
    public Component onRemoved(IToolStackView tool, @NotNull Modifier modifier) {
        if (tool.getModifierLevel(this.getId()) == 0) {
            tool.getPersistentData().remove(this.KEY);
        }

        return null;
    }
    public int getPriority() {
        return 120;
    }

    //override InventoryTickModifierHook
    //物品栏中Tick事件，用于更新进度
    public void onInventoryTick(@Nonnull IToolStackView tool, @NotNull ModifierEntry modifier, @Nonnull Level world, @Nonnull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack) {
        ModDataNBT persistentData = tool.getPersistentData();
        boolean equiped = itemSlot==36||itemSlot==37||itemSlot==38||itemSlot==39||itemSlot==40;
        int tickTime = TTConfig.MODIFIER_MATURE_TICKTIME.getAsInt();
        float maxEachLevel = (float) TTConfig.MODIFIER_MATURE_MAXEACHLEVEL.getAsDouble();
        if (!world.isClientSide() && holder.tickCount % tickTime == 0
                && (isSelected||equiped) && holder.isAlive()
                && persistentData.getFloat(this.KEY) < (maxEachLevel * modifier.getLevel())
                //&& RANDOM.nextFloat() <= 0.6F * (float)modifier.getLevel()
        ){
            persistentData.putFloat(this.KEY, persistentData.getFloat(this.KEY) + 1.0F);
        }

    }
    //override ToolDamageModifierHook
    public int onDamageTool(IToolStackView tool, @NotNull ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        ModDataNBT persistentData = tool.getPersistentData();
        if (persistentData.contains(this.KEY, Tag.TAG_FLOAT)) {
            float value = persistentData.getFloat(this.KEY);
            double rate = Math.min(TTConfig.MODIFIER_MATURE_UNBREAKING_MAX.getAsDouble(),value * Math.max((1.0f - TTConfig.MODIFIER_MATURE_UNBREAKING_VAL.getAsDouble()),0));

            if (amount > 1.0f){
                amount = (int)((double)amount * rate);
            }else{
                amount *= RANDOM.nextFloat()<rate?0:amount;
            }
        }
        return amount;
    }
    //override ToolStateHook
    public void addToolStats(IToolContext tool, @NotNull ModifierEntry modifier, @NotNull ModifierStatsBuilder builder){
        IModDataView persistentData = tool.getPersistentData();
        if (persistentData.contains(KEY, Tag.TAG_FLOAT )) {
            int value = persistentData.getInt(KEY);
            if (tool.hasTag(TinkerTags.Items.HARVEST)||tool.hasTag(TinkerTags.Items.MELEE_WEAPON)) {
                if (TTConfig.MODIFIER_MATURE_ATTACK_DAMAGE_RATE.getAsBoolean()){
                    ToolStats.ATTACK_DAMAGE.multiply(builder, 1.0f + value * TTConfig.MODIFIER_MATURE_ATTACK_DAMAGE_VAL.getAsDouble());
                } else {
                    ToolStats.ATTACK_DAMAGE.add(builder, value * TTConfig.MODIFIER_MATURE_ATTACK_DAMAGE_VAL.getAsDouble());
                }
                if (TTConfig.MODIFIER_MATURE_ATTACK_SPEED_RATE.getAsBoolean()){
                    ToolStats.ATTACK_SPEED.multiply(builder, 1.0f + value * TTConfig.MODIFIER_MATURE_ATTACK_SPEED_VAL.getAsDouble());
                } else {
                    ToolStats.ATTACK_SPEED.add(builder, value * TTConfig.MODIFIER_MATURE_ATTACK_SPEED_VAL.getAsDouble());
                }
                if (TTConfig.MODIFIER_MATURE_MINING_SPEED_RATE.getAsBoolean()){
                    ToolStats.MINING_SPEED.multiply(builder, 1.0f + value * TTConfig.MODIFIER_MATURE_MINING_SPEED_VAL.getAsDouble());
                } else {
                    ToolStats.MINING_SPEED.add(builder, value * TTConfig.MODIFIER_MATURE_MINING_SPEED_VAL.getAsDouble());
                }
            }
            if (tool.hasTag(TinkerTags.Items.ARMOR)) {
                if (TTConfig.MODIFIER_MATURE_ARMOR_RATE.getAsBoolean()) {
                    ToolStats.ARMOR.multiply(builder, 1.0f+value * TTConfig.MODIFIER_MATURE_ARMOR_VAL.getAsDouble());
                } else {
                    ToolStats.ARMOR.add(builder, value * TTConfig.MODIFIER_MATURE_ARMOR_VAL.getAsDouble());
                }
                ToolStats.ARMOR_TOUGHNESS.add(builder, value * TTConfig.MODIFIER_MATURE_ARMOR_TOUGHNESS_VAL.getAsDouble());
            }
        }
    }
    //override TooltipModifierHook
    public void addTooltip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @org.jetbrains.annotations.Nullable Player player, @NotNull List<Component> tooltip, @NotNull TooltipKey tooltipKey, @NotNull TooltipFlag tooltipFlag) {
        if (player != null) {
            ModDataNBT persistentData = tool.getPersistentData();
            if (persistentData.contains(this.KEY, Tag.TAG_FLOAT)) {
                float value = persistentData.getFloat(this.KEY);
                if (tool.hasTag(TinkerTags.Items.HARVEST)||tool.hasTag(TinkerTags.Items.MELEE_WEAPON)){
                    if (TTConfig.MODIFIER_MATURE_ATTACK_DAMAGE_RATE.getAsBoolean()) {
                        TooltipModifierHook.addPercentBoost(modifier.getModifier(), Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.attack_damage"), value * TTConfig.MODIFIER_MATURE_ATTACK_DAMAGE_VAL.getAsDouble(), tooltip);
                    }else {
                        TooltipModifierHook.addFlatBoost(modifier.getModifier(), Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.attack_damage"), value * TTConfig.MODIFIER_MATURE_ATTACK_DAMAGE_VAL.getAsDouble(), tooltip);
                    }
                    if (TTConfig.MODIFIER_MATURE_ATTACK_SPEED_RATE.getAsBoolean()) {
                        TooltipModifierHook.addPercentBoost(modifier.getModifier(), Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.attack_speed"), value * TTConfig.MODIFIER_MATURE_ATTACK_SPEED_VAL.getAsDouble(), tooltip);
                    }else {
                        TooltipModifierHook.addFlatBoost(modifier.getModifier(), Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.attack_speed"), value * TTConfig.MODIFIER_MATURE_ATTACK_SPEED_VAL.getAsDouble(), tooltip);
                    }
                    if (TTConfig.MODIFIER_MATURE_MINING_SPEED_RATE.getAsBoolean()){
                        TooltipModifierHook.addPercentBoost(modifier.getModifier(), Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.mining_speed"), value * TTConfig.MODIFIER_MATURE_MINING_SPEED_VAL.getAsDouble(), tooltip);
                    }else {
                        TooltipModifierHook.addFlatBoost(modifier.getModifier(), Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.mining_speed"), value * TTConfig.MODIFIER_MATURE_MINING_SPEED_VAL.getAsDouble(), tooltip);
                    }
                }
                if (tool.hasTag(TinkerTags.Items.ARMOR)){
                    if (TTConfig.MODIFIER_MATURE_ARMOR_RATE.getAsBoolean()) {
                        TooltipModifierHook.addPercentBoost(modifier.getModifier(), Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.armor"), value * TTConfig.MODIFIER_MATURE_ARMOR_VAL.getAsDouble(), tooltip);
                    }else {
                        TooltipModifierHook.addFlatBoost(modifier.getModifier(), Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.armor"), value * TTConfig.MODIFIER_MATURE_ARMOR_VAL.getAsDouble(), tooltip);
                    }
                    TooltipModifierHook.addFlatBoost(modifier.getModifier(), Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.armor_toughness"), value * TTConfig.MODIFIER_MATURE_ARMOR_TOUGHNESS_VAL.getAsDouble(), tooltip);

                }
                double rate = Math.min(TTConfig.MODIFIER_MATURE_UNBREAKING_MAX.getAsDouble(),value * Math.max((1.0f - TTConfig.MODIFIER_MATURE_UNBREAKING_VAL.getAsDouble()),0));
                //Component UNBREAKING = TConstruct.makeTranslation("modifier", "tooltip."+modifierName+".unbreaking");

                TooltipModifierHook.addPercentBoost(modifier.getModifier(),Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.unbreaking"),rate,tooltip);
                TooltipModifierHook.addPercentBoost(modifier.getModifier(),Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.value"),value / (TTConfig.MODIFIER_MATURE_MAXEACHLEVEL.getAsDouble()* modifier.getLevel()),tooltip);
                tooltip.add(Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.cap").withColor(modifier.getModifier().getColor()));
            }
        }
    }

    /*public boolean isArmor(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }
    //AttributesModifierHook
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        double current = (double)tool.getPersistentData().getFloat(KEY);

        if (this.isArmor(slot)) {

            if (current != 0.0D) {
                consumer.accept(Attributes.ARMOR.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(
                        "tinkers_transcend", "attribute_modifier.armor"), current * TTConfig.MODIFIER_MATURE_ARMOR_VAL.getAsDouble(),
                        TTConfig.MODIFIER_MATURE_ARMOR_RATE.getAsBoolean()?AttributeModifier.Operation.ADD_MULTIPLIED_BASE:AttributeModifier.Operation.ADD_VALUE));
                consumer.accept(Attributes.ARMOR_TOUGHNESS.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(
                        "tinkers_transcend", "attribute_modifier.armor_toughness"), current * TTConfig.MODIFIER_MATURE_ARMOR_TOUGHNESS_VAL.getAsDouble(),
                        AttributeModifier.Operation.ADD_VALUE));
                //consumer.accept(Attributes.KNOCKBACK_RESISTANCE.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(
                //        "tinkers_transcend", "attribute_modifier.mature.knockback_resistance"), (double)current,
                //        AttributeModifier.Operation.ADD_VALUE));
            }
        }
        if (tool.hasTag(TinkerTags.Items.MELEE_WEAPON)){
            if (current != 0.0D){
                consumer.accept(Attributes.ATTACK_DAMAGE.value(),new AttributeModifier(ResourceLocation.fromNamespaceAndPath(
                        "tinkers_transcend", "attribute_modifier.melee_attack"),current * TTConfig.MODIFIER_MATURE_MELEE_DAMAGE_VAL.getAsDouble(),
                        TTConfig.MODIFIER_MATURE_MELEE_DAMAGE_RATE.getAsBoolean()?AttributeModifier.Operation.ADD_MULTIPLIED_BASE:AttributeModifier.Operation.ADD_VALUE));
            }
        }
        if (tool.hasTag(TinkerTags.Items.HARVEST)){
            if (current != 0.0D){
                consumer.accept(Attributes.BLOCK_BREAK_SPEED.value(),new AttributeModifier(ResourceLocation.fromNamespaceAndPath(
                        "tinkers_transcend", "attribute_modifier.block_break_speed"),current * TTConfig.MODIFIER_MATURE_BLOCK_BREAK_SPEED_VAL.getAsDouble(),
                        TTConfig.MODIFIER_MATURE_BLOCK_BREAK_SPEED_RATE.getAsBoolean()?AttributeModifier.Operation.ADD_MULTIPLIED_BASE:AttributeModifier.Operation.ADD_VALUE));

            }
        }
    }*/
    /*//override ConditionalStatModifierHook
    public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
        ModDataNBT persistentData = tool.getPersistentData();
        if (persistentData.contains(this.KEY, Tag.TAG_FLOAT)) {
            float value = persistentData.getFloat(this.KEY);
            if (stat == ToolStats.DRAW_SPEED) {
                return (float)((double)baseValue * ((double)1.0F + (double)value * 0.1));
            }
            if (stat == ToolStats.PROJECTILE_DAMAGE) {
                return (float)((double)baseValue * ((double)1.0F + (double)value * 0.1));
            }
        }
        return baseValue;
    }*/
    /*//override BreakSpeedModifierHook
    public void onBreakSpeed(@Nonnull IToolStackView tool, ModifierEntry modifier, @Nonnull PlayerEvent.BreakSpeed event, @Nonnull Direction sideHit, boolean isEffective, float miningSpeedModifier) {
        ModDataNBT persistentData = tool.getPersistentData();
        if (persistentData.contains(this.KEY, Tag.TAG_FLOAT)) {
            float value = persistentData.getFloat(this.KEY);
              event.setNewSpeed((float)((double)event.getOriginalSpeed() * ((double)1.0F + (double)value * 0.1)));
         }
    }*/

    /*//override MeleeDamageModifierHook
    public float getMeleeDamage(@Nonnull IToolStackView tool, ModifierEntry modifier, @Nonnull ToolAttackContext context, float baseDamage, float damage) {
        ModDataNBT persistentData = tool.getPersistentData();
        if (persistentData.contains(this.KEY, 5)) {
            float value = persistentData.getFloat(this.KEY);
            return (float)((double)damage * ((double)1.0F + (double)value * 0.1));
        } else {
            return damage;
        }
        return damage;
    }
    //ModifyDamageModifierHook,
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slot, DamageSource source, float damage, boolean bDir){
        return damage;
    }

    //DamageBlockModifierHook,
    public boolean isDamageBlocked(IToolStackView var1, ModifierEntry var2, EquipmentContext var3, EquipmentSlot var4, DamageSource var5, float var6){
        return false;
    }*/
}
