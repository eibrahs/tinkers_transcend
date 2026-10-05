package com.eibrahs.tinkerstranscend.tinker.modifiers.handler;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MagicShield(float value, float max, float regeneration,long brokenTime) {
    private static final float REGEN_PER_LEVEL = 0.25f;
    private static final float MAX_PER_LEVEL = 2.5f;
    private static final long REGEN_TIME = 100;
    public static final Codec<MagicShield> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("value").forGetter(MagicShield::value),
            Codec.FLOAT.fieldOf("max").forGetter(MagicShield::max),
            Codec.FLOAT.fieldOf("regeneration").forGetter(MagicShield::regeneration),
            Codec.LONG.fieldOf("brokenTime").forGetter(MagicShield::brokenTime)
    ).apply(instance, MagicShield::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MagicShield> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, MagicShield::value,
                    ByteBufCodecs.FLOAT, MagicShield::max,
                    ByteBufCodecs.FLOAT, MagicShield::regeneration,
                    ByteBufCodecs.VAR_LONG,MagicShield::brokenTime,
                    MagicShield::new
            );
    public MagicShield takeDamage(float damage){
        return new MagicShield(Math.max(0.0f,value-damage),max,regeneration,brokenTime);
    }
    public boolean isBroken(){
        return value <= 0;
    }
    public boolean isFull(){
        return value >= max;
    }
    public MagicShield setLevel(int level){
        return new MagicShield(Math.min(value,level * MAX_PER_LEVEL),level * MAX_PER_LEVEL,level * REGEN_PER_LEVEL,brokenTime);
    }
    public MagicShield addLevel(int level){
        return new MagicShield(value,max+level*MAX_PER_LEVEL,regeneration+level*REGEN_PER_LEVEL,brokenTime);
    }
    public MagicShield reduceLevel(int level){
        return new MagicShield(value-level*MAX_PER_LEVEL,max-level*MAX_PER_LEVEL,regeneration-level*REGEN_PER_LEVEL,brokenTime);
    }
    public MagicShield regen(){
        return new MagicShield(Math.min(value+regeneration,max),max,regeneration,brokenTime);
    }
    public boolean isInvalid(){
        return max <=0;
    }
    public MagicShield setBrokenTime(long time){
        return new MagicShield(value,max,regeneration,time);
    }
    public boolean canRegen(long gameTime){
        return (gameTime - brokenTime) > REGEN_TIME;
    }
}
