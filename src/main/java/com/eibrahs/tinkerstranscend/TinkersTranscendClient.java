package com.eibrahs.tinkerstranscend;

import com.eibrahs.tinkerstranscend.block.entity.TimeWarpCastingBlockEntity;
import com.eibrahs.tinkerstranscend.block.entity.TinkersTranscendBlockEntities;
import com.eibrahs.tinkerstranscend.block.menu.TinkersTranscendMenus;
import com.eibrahs.tinkerstranscend.client.model.ToolElementsModel;
import com.eibrahs.tinkerstranscend.client.particle.TinkersTranscendParticles;
import com.eibrahs.tinkerstranscend.client.renderer.ThrownModifiableHeavyHalberdRenderer;
import com.eibrahs.tinkerstranscend.client.screen.ChromaticAnvilScreen;
import com.eibrahs.tinkerstranscend.client.screen.RoyalFoundryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
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
    }
    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        TinkersTranscendParticles.register(event);
    }
}
