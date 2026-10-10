package com.eibrahs.tinkerstranscend;

import com.eibrahs.tinkerstranscend.block.entity.TimeWarpCastingBlockEntity;
import com.eibrahs.tinkerstranscend.block.entity.TinkersTranscendBlockEntities;
import com.eibrahs.tinkerstranscend.block.menu.TinkersTranscendMenus;
import com.eibrahs.tinkerstranscend.client.gui.MagicShieldBarLayer;
import com.eibrahs.tinkerstranscend.client.gui.MagicShieldLayer;
import com.eibrahs.tinkerstranscend.client.model.ToolElementsModel;
import com.eibrahs.tinkerstranscend.client.particle.TinkersTranscendParticles;
import com.eibrahs.tinkerstranscend.client.renderer.ThrownModifiableHeavyHalberdRenderer;
import com.eibrahs.tinkerstranscend.client.screen.ChromaticAnvilScreen;
import com.eibrahs.tinkerstranscend.client.screen.RoyalFoundryScreen;
import com.eibrahs.tinkerstranscend.entity.TinkersTranscendEntities;
import com.eibrahs.tinkerstranscend.entity.projectile.FoxSoulArrowEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import slimeknights.tconstruct.smeltery.client.render.HeatingStructureBlockEntityRenderer;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = TinkersTranscend.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = TinkersTranscend.MODID, value = Dist.CLIENT)
public class TinkersTranscendClient {
    public static int KEY_MULTIPHASE_PRESS_TICK = 0;
    public TinkersTranscendClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        TinkersTranscend.LOGGER.info("HELLO FROM CLIENT SETUP");
        TinkersTranscend.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        event.enqueueWork(() -> {
            TinkersTranscendFluids.RegisterList.forEach(TinkersTranscendFluids.FluidRegister::clientEvent);
        });
    }
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(TinkersTranscendMenus.EnchantmentSorterMenu.get(), ChromaticAnvilScreen::new);
        event.register(TinkersTranscendMenus.ROYAL_FOUNDRY_MENU.get(), RoyalFoundryScreen::new);
    }
    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath("tinkers_transcend","tool_elements"), ToolElementsModel.Loader.INSTANCE);
    }
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                TinkersTranscendEntities.THROWN_MODIFIABLE_HEAVY_HALBERD.get(),
                ThrownModifiableHeavyHalberdRenderer::new
        );
        TimeWarpCastingBlockEntity.registerRenderers(event);
        event.registerBlockEntityRenderer(TinkersTranscendBlockEntities.ROYAL_FOUNDRY.get(), HeatingStructureBlockEntityRenderer::new);
        event.registerEntityRenderer(
                TinkersTranscendEntities.FOX_SOUL_ARROW.get(),
    ctx -> new ArrowRenderer<>(ctx) {
            @Override
            public ResourceLocation getTextureLocation(FoxSoulArrowEntity foxSoulArrowEntity) {
                return ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"textures/entity/projectiles/fox_soul_arrow.png");
        }
    });

    }
    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        TinkersTranscendParticles.register(event);
    }
    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.PLAYER_HEALTH,
                ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "magic_shield"),
                new MagicShieldLayer()
        );
        event.registerAbove(
                VanillaGuiLayers.ARMOR_LEVEL,
                ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "magic_shield_bar"),
                new MagicShieldBarLayer()
        );
    }
}
