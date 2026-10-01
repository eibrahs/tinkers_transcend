package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.TinkersTranscendComponents;
import dev.dubhe.anvilcraft.client.init.ModKeyMappings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.*;

import javax.annotation.Nullable;
import java.util.List;

public class MultiphaseModifier extends Modifier implements
        InventoryTickModifierHook,
        TooltipModifierHook,
        ModifierRemovalHook
{

    private static final String modifierName = "multiphase";
    private final static ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, modifierName);
    private final static ResourceLocation SELECTION = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "selection_phase");
    public final static String NBT_NAME = "upgradesNBT";

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.TOOLTIP,
                ModifierHooks.INVENTORY_TICK,
                ModifierHooks.REMOVE
        });
    }
    public int getPriority() {
        return 1000;
    }

    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level level, LivingEntity entity, int var5, boolean var6, boolean var7, ItemStack stack){
       if (!isInitPhase(stack)) {
           tool.getPersistentData().putString(KEY, stack.getDisplayName().getString().replace("[", "").replace("]", ""));
           stack.set(TinkersTranscendComponents.TTMULTIPHASE, TTMultiphase.create());
       }else{
           if (!tool.getPersistentData().contains(KEY)){
               stack.remove(TinkersTranscendComponents.TTMULTIPHASE);
           }else {
               TTMultiphase comp = stack.get(TinkersTranscendComponents.TTMULTIPHASE);
               int modifierLevel = Math.clamp(modifier.getLevel(),1,3);
               int upgradeLevel = tool.getUpgrades().getLevel(this.getId());
               if ((modifierLevel+1) < comp.size()){
                   comp.removePhase(stack);
                   TinkersTranscend.LOGGER.info("Size1:{}",comp.size());
                   TinkersTranscend.LOGGER.info("Size2:{}",comp.size());
               } else if ((modifierLevel+1) > comp.size()){
                   comp.addPhase(stack);
                   TinkersTranscend.LOGGER.info("Size3:{}",comp.size());
                   TinkersTranscend.LOGGER.info("Size4:{}",comp.size());
               }
           }
       }
    }
    public Component onRemoved(IToolStackView tool, Modifier modifier){
        tool.getPersistentData().remove(KEY);
        return Component.empty();
    };

    public static String getTinkersName(ItemStack stack){
        if (!isInitPhase(stack))return "";
        return getTinkersName(ToolStack.copyFrom(stack));
    }
    public static String getTinkersName(ToolStack tool){
        return tool.getPersistentData().getString(KEY);
    }
    public static CompoundTag createEmptyTag(){
        CompoundTag upgradesTag = new CompoundTag();
        upgradesTag.put(NBT_NAME,new ListTag());
        upgradesTag.putInt(SlotType.ABILITY.getName(),0);
        upgradesTag.putInt(SlotType.UPGRADE.getName(),0);
        upgradesTag.putInt(SlotType.DEFENSE.getName(),0);
        return upgradesTag;
    }
    public static boolean isInitPhase(ItemStack itemStack){
        return itemStack.getComponents().has(TinkersTranscendComponents.TTMULTIPHASE);
    }
    public static CompoundTag getUpgradeTag(ItemStack stack){
        if (!isInitPhase(stack)) return createEmptyTag();
        return getUpgradeTag(ToolStack.copyFrom(stack));
    }
    public static CompoundTag getUpgradeTag(ToolStack tool){
        CompoundTag upgradesTag = new CompoundTag();
        ToolDataNBT persistentData = tool.getPersistentData();
        upgradesTag.put(NBT_NAME,tool.getUpgrades().serializeToNBT());
        upgradesTag.putInt(SlotType.ABILITY.getName(),persistentData.getSlots(SlotType.ABILITY));
        upgradesTag.putInt(SlotType.UPGRADE.getName(),persistentData.getSlots(SlotType.UPGRADE));
        upgradesTag.putInt(SlotType.DEFENSE.getName(),persistentData.getSlots(SlotType.DEFENSE));
        return upgradesTag;
    }
    public static void ApplyUpgradeTag(ItemStack itemStack,CompoundTag upgradeTag){
        if (isInitPhase(itemStack)){
            ApplyUpgradeTag(ToolStack.from(itemStack),upgradeTag);
        }
    }
    public static void ApplyUpgradeTag(ToolStack tool,CompoundTag upgradeTag){
        CompoundTag tag = upgradeTag.copy();
        ToolDataNBT persistentData = tool.getPersistentData();
        tool.setUpgrades(ModifierNBT.readFromNBT(tag.getList(NBT_NAME,ListTag.TAG_COMPOUND)));
        persistentData.setSlots(SlotType.ABILITY,tag.getInt(SlotType.ABILITY.getName()));
        persistentData.setSlots(SlotType.UPGRADE,tag.getInt(SlotType.UPGRADE.getName()));
        persistentData.setSlots(SlotType.DEFENSE,tag.getInt(SlotType.DEFENSE.getName()));
    }
    public void addTooltip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, @NotNull List<Component> tooltip, @NotNull TooltipKey tooltipKey, @NotNull TooltipFlag var6) {
        if (player != null) {
            tooltip.add(Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.text", ModKeyMappings.SWITCH_PHASE.get().getKey().getDisplayName()).withColor(modifier.getModifier().getColor()));
        }
    }

}
