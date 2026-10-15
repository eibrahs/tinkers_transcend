package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscendAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;

public class FoxCurseModifier extends Modifier implements
        TooltipModifierHook,
        MeleeDamageModifierHook,
        ProjectileHitModifierHook
{
    private static final String modifierName = "fox_curse";

    public int getPriority() {
        return 991;
    }

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.TOOLTIP,
                ModifierHooks.MELEE_DAMAGE,
                ModifierHooks.PROJECTILE_HIT
        });
    }
    public void addTooltip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, @NotNull List<Component> tooltip, @NotNull TooltipKey tooltipKey, @NotNull TooltipFlag var6) {
        if (player != null) {
            tooltip.add(Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip.text").append(String.format("%.0f",getValue(player))).withColor(modifier.getModifier().getColor()));
        }
    }
    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity entity = context.getLivingTarget();
        if (entity==null) return 0;
        Player player = context.getPlayerAttacker();
        return calculateDamage(player,entity,modifier,damage);
    }
    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (!(attacker instanceof Player player)) return false;
        if (target==null) return false;
        if (projectile instanceof AbstractArrow arrow){
            float damage = (float)arrow.getBaseDamage();
            arrow.setBaseDamage(calculateDamage(player,target,modifier,damage));
        }

        return false;
    }
    protected float calculateDamage(Player player,LivingEntity target,ModifierEntry modifier,float damage){
        float maxHealth = target.getMaxHealth();
        float takeDamageRate = Math.min(1.0f,damage/maxHealth);
        float maxDamageRate = modifier.getLevel()*0.2f;
        float damageRate = Math.min(maxDamageRate,(1.0f-takeDamageRate));
        float value = getValue(player);
        float addDamage = Math.min(value,damage * damageRate);
        float newDamage = damage+addDamage;

        float health = target.getHealth();
        float addValue = ((newDamage>health)&&(target instanceof Enemy))?newDamage-health:0;

        setValue(player,value - addDamage + addValue);
        return newDamage;
    }
    protected float getValue(Player player){
        return player.getData(TinkersTranscendAttachments.FOX_SOUL_VALUE);
    }
    protected void setValue(Player player,float value){
        player.setData(TinkersTranscendAttachments.FOX_SOUL_VALUE,Math.clamp(value,0.0f,100.0f));
    }
}
