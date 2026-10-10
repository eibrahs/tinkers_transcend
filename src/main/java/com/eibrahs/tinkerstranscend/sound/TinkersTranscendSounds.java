package com.eibrahs.tinkerstranscend.sound;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(TinkersTranscend.MODID)
public class TinkersTranscendSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, TinkersTranscend.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_SHIELD_HURT_C1;
    public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_SHIELD_HURT_C2;
    public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_SHIELD_BREAK_C1;
    public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_SHIELD_BREAK_C2;

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name){
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, name)));
    }
    static{
        MAGIC_SHIELD_HURT_C1 = register("magic_shield_hurt_c1");
        MAGIC_SHIELD_HURT_C2 = register("magic_shield_hurt_c2");
        MAGIC_SHIELD_BREAK_C1 = register("magic_shield_break_c1");
        MAGIC_SHIELD_BREAK_C2 = register("magic_shield_break_c2");
    }
}
