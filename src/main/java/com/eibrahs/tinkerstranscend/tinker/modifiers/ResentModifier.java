package com.eibrahs.tinkerstranscend.tinker.modifiers;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

public class ResentModifier extends Modifier implements
        MeleeDamageModifierHook,
        InventoryTickModifierHook,
        ProjectileHitModifierHook,
        OnAttackedModifierHook

{
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.MELEE_DAMAGE,
                ModifierHooks.INVENTORY_TICK,
                ModifierHooks.PROJECTILE_HIT,
                ModifierHooks.ON_ATTACKED
        });
    }
    public int getPriority() {
        return 990;
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext toolAttackContext, float baseDamage, float damage) {
        LivingEntity target = toolAttackContext.getLivingTarget();
        if (target==null) return 0;
        applyCurse(target,modifier.getLevel());
        return damage;
    }
    private void applyCurse(LivingEntity entity,int modifierLevel){
        entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, -1+modifierLevel));
        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, -1+modifierLevel));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, modifierLevel));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 180, 1+modifierLevel));
    }
    private void applyUser(LivingEntity user){
        user.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));
        user.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 0));
        user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        user.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 2));
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level level, LivingEntity entity, int i, boolean b, boolean b1, ItemStack itemStack) {
        if (level.getGameTime()%600!=0) return;
        if (level.getRandom().nextFloat()>0.005f*modifier.getLevel()) return;
        applyUser(entity);
    }
    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (target==null) return false;
        applyCurse(target,modifier.getLevel());
        return false;
    }

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext equipmentContext, EquipmentSlot equipmentSlot, DamageSource damageSource, float v, boolean b) {
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            int modifierLevel = modifier.getLevel();
            double chance = modifierLevel * 0.15D;
            if (Math.random() < chance) {
                applyCurse(attacker,modifierLevel);
            }
        }
    }
}
