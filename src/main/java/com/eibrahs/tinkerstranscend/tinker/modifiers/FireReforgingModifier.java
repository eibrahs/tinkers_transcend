package com.eibrahs.tinkerstranscend.tinker.modifiers;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
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
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class FireReforgingModifier extends Modifier implements
        TooltipModifierHook,
        InventoryTickModifierHook
{
    private static final String modifierName = "fire_reforging";
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addModule(new VolatileFlagModule(ResourceLocation.fromNamespaceAndPath("tconstruct","indestructible"))
        );
        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.TOOLTIP,
                ModifierHooks.INVENTORY_TICK
        });
    }
    public @NotNull Component getDisplayName(int level) {
        return applyStyle(Component.translatable(getTranslationKey()));
    }
    public int getPriority() {
        return 1005;
    }
    public void onInventoryTick(IToolStackView var1, ModifierEntry var2, Level var3, LivingEntity entity, int var5, boolean var6, boolean var7, ItemStack stack){
        if(!stack.has(DataComponents.FIRE_RESISTANT)){
            stack.set(DataComponents.FIRE_RESISTANT, Unit.INSTANCE);
        }
    }

    public void addTooltip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @org.jetbrains.annotations.Nullable Player player, @NotNull List<Component> tooltip, @NotNull TooltipKey tooltipKey, @NotNull TooltipFlag tooltipFlag) {
        if (player != null) {
            tooltip.add(Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.text").withColor(modifier.getModifier().getColor()));
        }
    }
}
