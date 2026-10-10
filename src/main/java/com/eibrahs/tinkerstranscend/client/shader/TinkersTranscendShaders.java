package com.eibrahs.tinkerstranscend.client.shader;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@Mod(value = TinkersTranscend.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = TinkersTranscend.MODID, value = Dist.CLIENT)
public class TinkersTranscendShaders {
    public static ShaderInstance colorfulGlintShader;
    public static ShaderInstance armorEntityColorfulGlintShader;
    public static ShaderInstance magicShieldShader;

    private static final RenderStateShard.ShaderStateShard COLORFUL_GLINT_SHADER = new RenderStateShard.ShaderStateShard(() -> colorfulGlintShader);
    private static final RenderStateShard.ShaderStateShard ARMOR_ENTITY_COLORFUL_GLINT_SHADER = new RenderStateShard.ShaderStateShard(() -> armorEntityColorfulGlintShader);
    private static final RenderStateShard.ShaderStateShard MAGIC_SHIELD_SHADER = new RenderStateShard.ShaderStateShard(()->magicShieldShader);

    public static final ResourceLocation COLORFUL_GLINT_ITEM =ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"textures/misc/colorful_glint_item.png");
    public static final ResourceLocation COLORFUL_GLINT_ENTITY =ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"textures/misc/colorful_glint_entity.png");
    public static final ResourceLocation MAGIC_SHIELD_BACKGROUND = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "textures/misc/magic_shield_bg.png");
    public static final ResourceLocation MAGIC_SHIELD_PARTICLES = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "textures/misc/magic_shield_particles.png");

    public static final RenderType COLORFUL_GLINT_RENDER_TYPE;
    public static final RenderType ARMOR_ENTITY_COLORFUL_GLINT_RENDER_TYPE;
    public static final RenderType MAGIC_SHIELD_RENDER_TYPE;

    public static RenderType getColorfulGlintRenderType(){
        return COLORFUL_GLINT_RENDER_TYPE;
    }
    public static RenderType getArmorEntityColorfulGlintRenderType(){
        return ARMOR_ENTITY_COLORFUL_GLINT_RENDER_TYPE;
    }
    public static void setColorfulGlintShaderColor(float r,float g,float b,float a){
        if (colorfulGlintShader != null) {
            colorfulGlintShader.safeGetUniform("GlintColor").set(r, g, b, a);
        }
    };
    public static void setArmorEntityColorfulGlintShaderColor(float r,float g,float b,float a){
        if (armorEntityColorfulGlintShader != null) {
            armorEntityColorfulGlintShader.safeGetUniform("GlintColor").set(r, g, b, a);
        }
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "colorful_glint"),
                    DefaultVertexFormat.POSITION_TEX
            ), shaderInstance -> {colorfulGlintShader = shaderInstance;});
            event.registerShader(new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "armor_entity_colorful_glint"),
                    DefaultVertexFormat.POSITION_TEX
            ), shaderInstance -> {armorEntityColorfulGlintShader = shaderInstance;});
            event.registerShader(new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"magic_shield"),
                    DefaultVertexFormat.POSITION
            ),shaderInstance -> {magicShieldShader = shaderInstance;});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shaders", e);
        }
    }
    @SubscribeEvent
    public static void onRegisterRenderBuffers(RegisterRenderBuffersEvent event) {
        event.registerRenderBuffer(COLORFUL_GLINT_RENDER_TYPE);
        event.registerRenderBuffer(ARMOR_ENTITY_COLORFUL_GLINT_RENDER_TYPE);
        event.registerRenderBuffer(MAGIC_SHIELD_RENDER_TYPE);
    }
    static{
        COLORFUL_GLINT_RENDER_TYPE = RenderType.create(
                "colorful_glint",
                DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.QUADS,
                1536,
                RenderType.CompositeState.builder()
                        .setShaderState(COLORFUL_GLINT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(COLORFUL_GLINT_ITEM, true, false))
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                        .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
                        .setTexturingState(RenderStateShard.GLINT_TEXTURING)
                        .createCompositeState(false)
        );
        ARMOR_ENTITY_COLORFUL_GLINT_RENDER_TYPE = RenderType.create(
                "aromr_entity_colorful_glint",
                DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.QUADS,
                1536,
                RenderType.CompositeState.builder()
                        .setShaderState(ARMOR_ENTITY_COLORFUL_GLINT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(COLORFUL_GLINT_ENTITY, true, false))
                        .setWriteMaskState(RenderType.COLOR_WRITE)
                        .setCullState(RenderType.NO_CULL)
                        .setDepthTestState(RenderType.EQUAL_DEPTH_TEST)
                        .setTransparencyState(RenderType.GLINT_TRANSPARENCY)
                        .setTexturingState(RenderType.ENTITY_GLINT_TEXTURING)
                        .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                        .createCompositeState(false)
        );
        MAGIC_SHIELD_RENDER_TYPE = RenderType.create(
                "magic_shield",
                DefaultVertexFormat.POSITION,
                VertexFormat.Mode.QUADS,
                1536, false, false,
                RenderType.CompositeState.builder()
                        .setShaderState(MAGIC_SHIELD_SHADER)
                        .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                                .add(MAGIC_SHIELD_BACKGROUND, false, false)
                                .add(MAGIC_SHIELD_PARTICLES, false, false)
                                .build())
                        .setWriteMaskState(RenderType.COLOR_WRITE)
                        .setCullState(RenderType.NO_CULL)
                        .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false)
        );


    }
}
