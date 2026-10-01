package com.eibrahs.tinkerstranscend.effect;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(TinkersTranscend.MODID)
public class TinkersTranscendEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, TinkersTranscend.MODID);
    public static final Holder<MobEffect> CARVED = EFFECTS.register("carved",()->new CarvedMobEffect(MobEffectCategory.NEUTRAL,0x85210d));
}
