package com.eibrahs.tinkerstranscend.tinker.modifiers;

import dev.dubhe.anvilcraft.init.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.util.CombatHelper;
import slimeknights.tconstruct.library.modifiers.*;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipeCache;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Map;

public class ProvidenceModifier extends Modifier implements
        TooltipModifierHook,
        EnchantmentModifierHook,
        HarvestEnchantmentsModifierHook,
        ProcessLootModifierHook,
        OnAttackedModifierHook

{
    private static final String modifierName = "providence";
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addModule(new VolatileFlagModule(ResourceLocation.fromNamespaceAndPath("tconstruct","indestructible"))
        );
        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.TOOLTIP,
                ModifierHooks.ENCHANTMENTS,
                ModifierHooks.HARVEST_ENCHANTMENTS,
                ModifierHooks.PROCESS_LOOT,
                ModifierHooks.ON_ATTACKED
        });
    }
    public @NotNull Component getDisplayName(int level) {
        return applyStyle(Component.translatable(getTranslationKey()));
    }
    public int getPriority() {
        return 1001;
    }
    private boolean checkChance(){
        return Math.random()<0.25f;
    }
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Holder<Enchantment> enchantment, int level){
        if (!checkChance()) return level;
        if (enchantment.is(Enchantments.LUCK_OF_THE_SEA)||
            enchantment.is(ModEnchantments.BEHEADING_KEY)){
            level *= 2;
        }
        int luckLevel = tool.getModifierLevel(ModifierId.tryBuild("tconstruct","luck"));
        if (luckLevel>0&&
            (enchantment.is(Enchantments.LUCK_OF_THE_SEA)||
            enchantment.is(Enchantments.FORTUNE)||
            enchantment.is(Enchantments.LOOTING))){
            level +=luckLevel;
        }
        return level;
    }
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, HolderLookup.RegistryLookup<Enchantment> lookup, Map<Holder<Enchantment>, Integer> enchantments){
    }
    public void updateHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext toolContext, EquipmentContext equipmentContext, EquipmentSlot slot, Map<Holder<Enchantment>, Integer> enchantments){
        if (!checkChance()) return;
        if (slot.isArmor()) {
            int level = tool.getModifierLevel(ModifierId.tryBuild("tconstruct", "luck"));
            if (level > 0) {
                HolderLookup.RegistryLookup<Enchantment> lookup = toolContext.getWorld().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                EnchantmentModifierHook.addEnchantment(enchantments, lookup.getOrThrow(Enchantments.FORTUNE), level);
                EnchantmentModifierHook.addEnchantment(enchantments, lookup.getOrThrow(Enchantments.LOOTING), level);
                EnchantmentModifierHook.addEnchantment(enchantments, lookup.getOrThrow(Enchantments.LUCK_OF_THE_SEA), level);
            }
        }
    }

    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
        if (!checkChance()) return;
        if (context.hasParam(LootContextParams.DAMAGE_SOURCE)) {
            Entity entity = (Entity)context.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (entity != null && generatedLoot.stream().noneMatch((stack) -> stack.is(ItemTags.SKULLS))) {
                Level world = context.getLevel();
                List<SeveringRecipe> recipes = SeveringRecipeCache.findRecipe(world.getRecipeManager(), entity.getType());
                if (!recipes.isEmpty()) {
                    float level = tool.getModifierLevel(ModifierId.tryBuild("tconstruct","severing"));
                    if(level > 0) {
                        float looting = 0.0F;
                        Object var11 = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
                        if (var11 instanceof LivingEntity) {
                            LivingEntity attacker = (LivingEntity) var11;
                            looting = (float) EnchantmentHelper.getEnchantmentLevel(context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING), attacker);
                        }
                        float chanceMultiplier = entity.getType().is(Tags.EntityTypes.BOSSES) ? 2.0F : 1.0F;

                        for (SeveringRecipe recipe : recipes) {
                            if (world.random.nextFloat() < recipe.getChance(level, looting) * chanceMultiplier) {
                                ItemStack result = recipe.getOutput(entity);
                                if (!result.isEmpty()) {
                                    if (result.getCount() > 1) {
                                        result.setCount(world.random.nextInt(result.getCount()) + 1);
                                    }

                                    generatedLoot.add(result);
                                }
                            }
                        }
                    }
                }
            }

        }
    }
    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slot, DamageSource source, float amount, boolean isDirectDamage){
        if (!slot.isArmor()||!checkChance()) return;
        Entity attacker = source.getEntity();
        if (attacker == null || !attacker.isAlive()) return;
        int thornsLevel = tool.getModifierLevel(ModifierId.tryBuild("tconstruct", "thorns"));
        if (thornsLevel>0) {
            double chance = thornsLevel * 0.15D;
            if (Math.random() < chance * thornsLevel) {
                float damage = (float)Math.random()*3.0f+1.0f;
                attacker.hurt(CombatHelper.damageSource(DamageTypes.THORNS, context.getEntity()), (float)Math.round(damage));
            }
        }
        int fieryLevel =  tool.getModifierLevel(ModifierId.tryBuild("tconstruct", "fiery"));
        if (fieryLevel>0) {
            double chance = fieryLevel * 0.15D;
            if (Math.random() < chance * fieryLevel) {
                float time = (float)Math.random()*6.0f+1.0f;
                attacker.igniteForSeconds((float)Math.round(time));
            }
        }
        int freezingLevel =  tool.getModifierLevel(ModifierId.tryBuild("tconstruct", "freezing"));
        if (freezingLevel>0) {
            double chance = freezingLevel * 0.15D;
            if (Math.random() < chance * freezingLevel) {
                float time = (float)Math.random()*6.0f+2.0f;
                attacker.setTicksFrozen(Math.max(attacker.getTicksRequiredToFreeze(), attacker.getTicksFrozen()) + (int)(time * 40.0F));
                attacker.clearFire();
            }
        }

    }
    public void addTooltip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @org.jetbrains.annotations.Nullable Player player, @NotNull List<Component> tooltip, @NotNull TooltipKey tooltipKey, @NotNull TooltipFlag tooltipFlag) {
        if (player != null) {
            tooltip.add(Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.text").withColor(modifier.getModifier().getColor()));
        }
    }

}