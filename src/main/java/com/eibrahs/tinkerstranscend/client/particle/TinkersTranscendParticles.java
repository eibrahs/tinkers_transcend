package com.eibrahs.tinkerstranscend.client.particle;


import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(TinkersTranscend.MODID)
public class TinkersTranscendParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
        DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, TinkersTranscend.MODID);
    public static final DeferredHolder<ParticleType<?>, ColorParticleType> VISION_PARTICLE =
            PARTICLE_TYPES.register("vision", () -> new ColorParticleType(false));
    public static final DeferredHolder<ParticleType<?>, ColorParticleType> VISION_ENTITY_PARTICLE =
            PARTICLE_TYPES.register("vision_entity", () -> new ColorParticleType(false));
    public static void register(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(TinkersTranscendParticles.VISION_PARTICLE.get(), VisionParticle.Provider::new);
        event.registerSpriteSet(TinkersTranscendParticles.VISION_ENTITY_PARTICLE.get(), VisionEntityParticle.Provider::new);

    }
}
