package com.eibrahs.tinkerstranscend;

import com.eibrahs.tinkerstranscend.tinker.modifiers.handler.MagicShield;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class TinkersTranscendAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TinkersTranscend.MODID);
    public static final Supplier<AttachmentType<MagicShield>> MAGIC_SHIELD = ATTACHMENT_TYPES.register(
            "magic_shield",
            () -> AttachmentType.builder(MagicShield::createDefault)
                    .serialize(MagicShield.CODEC)
                    .sync(MagicShield.STREAM_CODEC)
                    .build()
    );
    public static final Supplier<AttachmentType<Float>> FOX_SOUL_VALUE = ATTACHMENT_TYPES.register(
            "fox_soul_value",
            () -> AttachmentType.builder(()->0.0f)
                    .serialize(Codec.FLOAT)
                    .copyOnDeath()
                    .sync(ByteBufCodecs.FLOAT)
                    .build()
    );
}
