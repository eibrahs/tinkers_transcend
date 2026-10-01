package com.eibrahs.tinkerstranscend.client.particle;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.SimpleParticleType;

public class VisionParticle extends TextureSheetParticle {
    protected final SpriteSet spriteSet;

    protected VisionParticle(ClientLevel level, double x, double y, double z, ColorParticleOption option, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.gravity = 0;
        this.lifetime = 60; // 生命周期（单位：tick）
        this.spriteSet=spriteSet;
        this.setColor(option.getRed(), option.getGreen(), option.getBlue());
        //this.setAlpha(option.getAlpha());
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0;
    }
    @Override
    public float getQuadSize(float scaleFactor) {
        return super.getQuadSize(scaleFactor)*1.2f;
    }

    @Override
    public void tick() {
        int frameIndex = this.age % 20;
        this.setSprite(this.spriteSet.get(frameIndex, 20));

        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return  new ParticleRenderType() {
            @Override
            public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
                RenderSystem.disableBlend();
                RenderSystem.depthMask(false);
                RenderSystem.disableDepthTest();
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
                return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            }

            @Override
            public boolean isTranslucent() {
                return false;
            }
        };
    }

    public static class Provider implements ParticleProvider<ColorParticleOption> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public TextureSheetParticle createParticle(ColorParticleOption option, ClientLevel level,
                                                   double x, double y, double z,
                                                   double xSpeed, double ySpeed, double zSpeed) {
            return new VisionParticle(level, x, y, z, option, this.spriteSet);
        }
    }
}
