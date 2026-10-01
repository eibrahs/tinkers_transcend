package com.eibrahs.tinkerstranscend;

import com.eibrahs.tinkerstranscend.block.TinkersTranscendBlocks;
import com.eibrahs.tinkerstranscend.block.entity.TinkersTranscendBlockEntities;
import com.eibrahs.tinkerstranscend.block.menu.TinkersTranscendMenus;
import com.eibrahs.tinkerstranscend.client.particle.TinkersTranscendParticles;
import com.eibrahs.tinkerstranscend.effect.TinkersTranscendEffects;
import com.eibrahs.tinkerstranscend.item.ChromaticAnvilHammerItem;
import com.eibrahs.tinkerstranscend.recipe.TinkersTranscendRecipes;
import com.eibrahs.tinkerstranscend.tinker.modifiers.TinkersTranscendModifiers;
import com.eibrahs.tinkerstranscend.tinker.TinkersTranscendTools;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TinkersTranscend.MODID)
public class TinkersTranscend {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "tinkers_transcend";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "TinkersTranscendium" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "TinkersTranscendium" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "TinkersTranscendium" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "tinkerstranscendium:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "tinkerstranscendium:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);
    public static final DeferredItem<ChromaticAnvilHammerItem> CHROMATIC_ANVIL_HAMMER = ITEMS.registerItem("chromatic_anvil_hammer",ChromaticAnvilHammerItem::new);
    // Creates a new food item with the id "tinkerstranscendium:example_id", nutrition 1 and saturation 2
    //public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
    //        .alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    // Creates a creative tab with the id "tinkerstranscendium:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tinkers_transcend")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> TinkersTranscendFluids.MOLTEN_TRANSCENDIUM.getBucket().getDefaultInstance())
            .displayItems((parameters, output) -> {
                //output.accept(EXAMPLE_ITEM.get());// Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(TinkersTranscendFluids.MOLTEN_TRANSCENDIUM.getBucket());
                output.accept(TinkersTranscendFluids.MOLTEN_ROYAL_STEEL.getBucket());
                output.accept(TinkersTranscendFluids.MOLTEN_EMBER_METAL.getBucket());
                output.accept(TinkersTranscendFluids.MOLTEN_UNTEMPERED_EMBER_METAL.getBucket());
                output.accept(TinkersTranscendFluids.MOLTEN_FROST_METAL.getBucket());
                output.accept(TinkersTranscendFluids.MOLTEN_UNTEMPERED_FROST_METAL.getBucket());
                output.accept(TinkersTranscendFluids.MOLTEN_EARTH_CORE.getBucket());
                output.accept(TinkersTranscendFluids.MOLTEN_CURSED_GOLD.getBucket());
                output.accept(TinkersTranscendFluids.KEROGEN.getBucket());
                output.accept(TinkersTranscendBlocks.SOURCE_ROCK_ITEM);
                output.accept(TinkersTranscendBlocks.TIME_WARP_CASTING_BASIN_ITEM.get());
                output.accept(TinkersTranscendBlocks.TIME_WARP_CASTING_TABLE_ITEM.get());
                output.accept(TinkersTranscendBlocks.SEARED_TIME_WARP_SMELTER_ITEM.get());
                output.accept(TinkersTranscendBlocks.SCORCHED_TIME_WARP_SMELTER_ITEM.get());
                output.accept(TinkersTranscendBlocks.ROYAL_TIME_WARP_SMELTER_ITEM.get());
                output.accept(TinkersTranscendBlocks.ROYAL_FOUNDRY_CONTROLLER_ITEM.get());
                output.accept(TinkersTranscendBlocks.ROYAL_STEEL_BRICK_ITEM.get());
                output.accept(TinkersTranscendBlocks.ROYAL_FOUNDRY_DRAIN_ITEM.get());
                output.accept(TinkersTranscendBlocks.ROYAL_FOUNDRY_DUCT_ITEM.get());
                output.accept(TinkersTranscendBlocks.ROYAL_FOUNDRY_CHUTE_ITEM.get());
                output.accept(TinkersTranscendBlocks.ROYAL_TANK_ITEM.get());
                output.accept(TinkersTranscendBlocks.ROYAL_LASER_HEATER_ITEM.get());
                output.accept(TinkersTranscendBlocks.CHROMATIC_ANVIL_ITEM.get());
                output.accept(CHROMATIC_ANVIL_HAMMER.get());
                output.accept(TinkersTranscendTools.HEAVY_HALBERD_GEAR_CAST.get());
                output.accept(TinkersTranscendTools.RESONATOR_SHELL_CAST.get());
                output.accept(CHROMATIC_ANVIL_HAMMER.get());
                TinkersTranscendTools.RESONATOR_SHELL.get().addVariants(output::accept, "");
                TinkersTranscendTools.RESONATOR_CORE.get().addVariants(output::accept, "");
                ToolBuildHandler.addVariants(output::accept,TinkersTranscendTools.RESONATOR.get(),"");
                TinkersTranscendTools.HEAVY_HALBERD_GEAR.get().addVariants(output::accept, "");
                TinkersTranscendTools.HEAVY_HALBERD_CORE.get().addVariants(output::accept, "");
                ToolBuildHandler.addVariants(output::accept,TinkersTranscendTools.HEAVY_HALBERD.get(),"");
                ToolBuildHandler.addVariants(output::accept,TinkersTranscendTools.WAR_AXE.get(),"");
                ToolBuildHandler.addVariants(output::accept,TinkersTranscendTools.HUNTING_KNIFE.get(),"");
            }).build());
    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public TinkersTranscend(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        TinkersTranscendFluids.FLUIDS.register(modEventBus);
        TinkersTranscendFluids.FLUID_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        TinkersTranscendModifiers.register(modEventBus);
        TinkersTranscendComponents.COMPONENTS.register(modEventBus);
        TinkersTranscendMenus.MENUS.register(modEventBus);
        TinkersTranscendBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        TinkersTranscendEntities.ENTITY_TYPES.register(modEventBus);
        TinkersTranscendEffects.EFFECTS.register(modEventBus);
        TinkersTranscendParticles.PARTICLE_TYPES.register(modEventBus);
        TinkersTranscendRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        TinkersTranscendRecipes.TYPES.register(modEventBus);
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (TinkersTranscendium) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        //modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, TTConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        //LOGGER.info("HELLO FROM COMMON SETUP");

        //if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
        //    LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        //}

        //LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        //Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        if (event.includeClient()) {
        }
        if (event.includeServer()) {
        }
    }
    public static ResourceLocation getResource(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

}
