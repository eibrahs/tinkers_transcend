package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.TinkersTranscendAttachments;
import com.eibrahs.tinkerstranscend.tinker.modifiers.handler.MagicShield;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;

public class FoxSoulModifier extends Modifier implements
        MeleeHitModifierHook,
        ProjectileHitModifierHook,
        EquipmentChangeModifierHook,
        TooltipModifierHook
{
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.MELEE_HIT,
                ModifierHooks.PROJECTILE_HIT,
                ModifierHooks.EQUIPMENT_CHANGE,
                ModifierHooks.TOOLTIP
        });
    }
    private static final String modifierName = "fox_soul";
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "fox_soul")
    );
    public int getPriority() {
        return 991;
    }
    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt)  {
        LivingEntity target = context.getLivingTarget();
        if (target==null) return;
        LivingEntity attacker = context.getAttacker();
        if (attacker.level() instanceof  ServerLevel serverLevel) {
            Vec3 center = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, center.x,center.y,center.z, 8, 0.2, 0.2, 0.2, Math.min(0.05,0.01*modifier.getLevel()));

            DamageSource magicSource = new DamageSource(serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE_TYPE), attacker, attacker);
            target.hurt(magicSource, 1.0F * modifier.getLevel());
        }
        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.FOX_BITE, SoundSource.NEUTRAL, 1.0f, 0.5f + attacker.level().getRandom().nextFloat());
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (target==null) return false;
        attacker.level().playSound(null,attacker.getX(),attacker.getY(),attacker.getZ(),SoundEvents.FOX_AGGRO, SoundSource.NEUTRAL,0.8f,1.2f);
        return false;
    }
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (tool.isBroken()) return;
        LivingEntity entity = context.getEntity();
        if (entity.getEquipmentSlotForItem(context.getOriginal())==entity.getEquipmentSlotForItem(context.getReplacement())) return;
        if (entity.level().isClientSide()) return;
        updateLevel(entity);
    }

    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        if (entity.getEquipmentSlotForItem(context.getOriginal())==entity.getEquipmentSlotForItem(context.getReplacement())) return;
        if (entity.level().isClientSide()) return;
        updateLevel(entity);
    }

    public void onEquipmentChange(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, EquipmentSlot slotType) {
        LivingEntity entity = context.getEntity();
        if (entity.level().isClientSide()) return;
        updateLevel(entity);
    }


    private void addLevel(LivingEntity entity,int level){
        MagicShield shield = entity.getData(TinkersTranscendAttachments.MAGIC_SHIELD);
        entity.setData(TinkersTranscendAttachments.MAGIC_SHIELD,shield.addLevel(level));
    }
    private void reduceLevel(LivingEntity entity,int level){
        MagicShield shield = entity.getData(TinkersTranscendAttachments.MAGIC_SHIELD);
        MagicShield newShield = shield.reduceLevel(level);
        if (newShield.isInvalid()) {
            entity.removeData(TinkersTranscendAttachments.MAGIC_SHIELD);
        }else{
            entity.setData(TinkersTranscendAttachments.MAGIC_SHIELD, newShield);
        }
    }
    private void updateLevel(LivingEntity entity){
        ToolStack head = ToolStack.copyFrom(entity.getItemBySlot(EquipmentSlot.HEAD));
        ToolStack chest =ToolStack.copyFrom(entity.getItemBySlot(EquipmentSlot.CHEST));
        ToolStack legs =ToolStack.copyFrom(entity.getItemBySlot(EquipmentSlot.LEGS));
        ToolStack feet =ToolStack.copyFrom(entity.getItemBySlot(EquipmentSlot.FEET));
        int level = 0;
        if (!(head.isBroken())){
            level += head.getModifierLevel(this);
        }
        if (!(chest.isBroken())){
            level += chest.getModifierLevel(this);
        }
        if (!(legs.isBroken())){
            level += legs.getModifierLevel(this);
        }
        if (!(feet.isBroken())){
            level += feet.getModifierLevel(this);
        }
        if (level==0) entity.removeData(TinkersTranscendAttachments.MAGIC_SHIELD);
        else {
            MagicShield shield = entity.getData(TinkersTranscendAttachments.MAGIC_SHIELD);
            entity.setData(TinkersTranscendAttachments.MAGIC_SHIELD,shield.setLevel(level));
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @org.jetbrains.annotations.Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
        if (player != null) {
            if (tool.hasTag(TinkerTags.Items.ARMOR)) {
                TooltipModifierHook.addFlatBoost(modifier.getModifier(),Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.magic_shield"),2.5f*modifier.getLevel() ,tooltip);
            } else {
                TooltipModifierHook.addFlatBoost(modifier.getModifier(), Component.translatable("modifier.tinkers_transcend." + modifierName + ".tooltip.magic_damage"), modifier.getLevel(), tooltip);
            }
        }
    }
}
