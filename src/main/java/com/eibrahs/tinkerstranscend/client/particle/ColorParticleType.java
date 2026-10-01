package com.eibrahs.tinkerstranscend.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class ColorParticleType extends ParticleType<ColorParticleOption> {
    protected ColorParticleType(boolean overrideLimitter) {
        super(overrideLimitter);
    }

    @Override
    public @NotNull MapCodec<ColorParticleOption> codec() {
        return ColorParticleOption.codec(this);
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ColorParticleOption> streamCodec() {
        return ColorParticleOption.streamCodec(this);
    }
}
