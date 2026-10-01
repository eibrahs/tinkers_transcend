package com.eibrahs.tinkerstranscend.client.particle;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.SimpleParticleType;

public class VisionEntityParticle extends VisionParticle {
    protected VisionEntityParticle(ClientLevel level, double x, double y, double z, ColorParticleOption option,SpriteSet spriteSet) {
        super(level, x, y, z,option,spriteSet);
        this.lifetime = 20; // 生命周期（单位：tick）
    }
    @Override
    public float getQuadSize(float scaleFactor) {
        return super.getQuadSize(scaleFactor)*5.0f;
    }

    @Override
    public void tick() {
        int frameIndex = this.age % 20;
        this.setSprite(this.spriteSet.get(frameIndex, 20));

        super.tick();
    }

    public static class Provider implements ParticleProvider<ColorParticleOption> {
        private final SpriteSet spriteSet;

        // 构造函数必须接收 SpriteSet，由 registerSpriteSet 自动传入
        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public TextureSheetParticle createParticle(ColorParticleOption option, ClientLevel level,
                                                   double x, double y, double z,
                                                   double xSpeed, double ySpeed, double zSpeed) {
            return new VisionEntityParticle(level, x, y, z, option,this.spriteSet);
        }
    }
}
