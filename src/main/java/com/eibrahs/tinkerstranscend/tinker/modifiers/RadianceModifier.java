package com.eibrahs.tinkerstranscend.tinker.modifiers;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.EffectImmunityModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;

public class RadianceModifier extends Modifier {

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addModule(new EffectImmunityModule(MobEffects.WEAKNESS.value()));
        hookBuilder.addModule(new EffectImmunityModule(MobEffects.MOVEMENT_SLOWDOWN.value()));
        hookBuilder.addModule(new EffectImmunityModule(MobEffects.HUNGER.value()));
        hookBuilder.addModule(AttributeModule.builder(Attributes.LUCK.value(), AttributeModifier.Operation.ADD_VALUE).amount(-1,1));
    }
    public int getPriority() {
        return 997;
    }
}
