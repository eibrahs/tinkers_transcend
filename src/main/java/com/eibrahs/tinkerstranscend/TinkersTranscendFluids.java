package com.eibrahs.tinkerstranscend;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TinkersTranscendFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, TinkersTranscend.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TinkersTranscend.MODID);
    public static final List<FluidRegister> RegisterList = new ArrayList<>();

    public static FluidRegister MOLTEN_TRANSCENDIUM = new FluidRegister("molten_transcendium")
            .color(0xFF27223D).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5);
    public static FluidRegister MOLTEN_ROYAL_STEEL = new FluidRegister("molten_royal_steel")
            .color(0xFF586967).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(40);
    public static FluidRegister MOLTEN_EMBER_METAL = new FluidRegister("molten_ember_metal")
            .color(0xFF291518).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(40);
    public static FluidRegister MOLTEN_FROST_METAL = new FluidRegister("molten_frost_metal")
            .color(0xFF7889a2).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(40);
    public static FluidRegister MOLTEN_UNTEMPERED_EMBER_METAL = new FluidRegister("molten_untempered_ember_metal")
            .color(0xFF955847).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(40);
    public static FluidRegister MOLTEN_UNTEMPERED_FROST_METAL = new FluidRegister("molten_untempered_frost_metal")
            .color(0xFFc1dcde).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(40);
    public static FluidRegister MOLTEN_EARTH_CORE = new FluidRegister("molten_earth_core")
            .color(0xFF6a1f06).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(40);
    public static FluidRegister MOLTEN_CURSED_GOLD = new FluidRegister("molten_cursed_gold")
            .color(0xFFaf5e1e).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(40);
    public static FluidRegister MOLTEN_ENCHANTED_GOLD = new FluidRegister("molten_enchanted_gold")
            .color(0xFFffd669).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(40);
    public static FluidRegister MOLTEN_TITANIUM = new FluidRegister("molten_titanium")
            .color(0xFF929ea9).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(40);
    public static FluidRegister KEROGEN = new FluidRegister("kerogen")
            .color(0xFFa89e65).init()
            .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(40);

    static{
        RegisterList.forEach(FluidRegister::register);
    }

    public static class FluidRegister {
        private final String name;
        public Supplier<FluidType> TYPE;
        private Supplier<FlowingFluid> SOURCE;
        private Supplier<FlowingFluid> FLOWING;
        private Supplier<LiquidBlock> BLOCK;
        private Supplier<BucketItem> BUCKET;
        private BaseFlowingFluid.Properties PROPERTIES;
        private int color;
        public FluidRegister (String name){
            this.name = name;
        }
        public FlowingFluid getSource() {
            return SOURCE.get();
        }
        public FlowingFluid getFlowing(){
            return FLOWING.get();
        }
        public LiquidBlock getBlock(){
            return BLOCK.get();
        }
        public BucketItem getBucket(){
            return BUCKET.get();
        }
        public FluidRegister init(){
            registerType();
            initProperties();
            RegisterList.add(this);
            return this;
        }
        public void clientEvent(){
            ItemBlockRenderTypes.setRenderLayer(getSource(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(getFlowing(), RenderType.translucent());

        }
        private void registerType(){
            TYPE = FLUID_TYPES.register(name,()->new RegisterFluidType(FluidType.Properties.create()
                    .canSwim(true)
                    .canDrown(true)
                    .canExtinguish(true)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY),
                    this));
        }
        private void initProperties(){
            PROPERTIES = new BaseFlowingFluid.Properties(
                    TYPE,
                    () -> SOURCE.get(),
                    () -> FLOWING.get()
            ).bucket(() -> BUCKET.get()).block(()-> BLOCK.get());
        }
        public FluidRegister slopeFindDistance(int slopeFindDistance){
            this.PROPERTIES = PROPERTIES.slopeFindDistance(slopeFindDistance);
            return this;
        }
        public FluidRegister levelDecreasePerBlock(int levelDecreasePerBlock){
            this.PROPERTIES = PROPERTIES.slopeFindDistance(levelDecreasePerBlock);
            return this;
        }
        public FluidRegister tickRate(int tickRate){
            this.PROPERTIES = PROPERTIES.tickRate(tickRate);
            return this;
        }
        public FluidRegister color(int color){
            this.color = color;
            return this;
        }
        public void register( ){
            SOURCE = FLUIDS.register(name,() -> new BaseFlowingFluid.Source(PROPERTIES));
            FLOWING = FLUIDS.register(name+"_flowing",()->new BaseFlowingFluid.Flowing(PROPERTIES));
            BLOCK = TinkersTranscend.BLOCKS.register(name, () -> new LiquidBlock(SOURCE.get(), BlockBehaviour.Properties.of().mapColor(MapColor.WATER)      // 材质颜色
                    .strength(100.0F).noCollission().replaceable().liquid()));
            BUCKET = TinkersTranscend.ITEMS.register(name+"_bucket", () -> new BucketItem(SOURCE.get(), new Item.Properties().stacksTo(1)));

        }
        private static class RegisterFluidType extends FluidType{
            private FluidRegister register;
            public RegisterFluidType(Properties properties,FluidRegister register){
                super(properties
                );
                this.register = register;
            }
            @Override
            public void initializeClient(@NotNull Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return ResourceLocation.fromNamespaceAndPath("tconstruct", "fluid/molten/stone/porcelain/still");
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return ResourceLocation.fromNamespaceAndPath("tconstruct", "fluid/molten/stone/porcelain/flowing");
                    }

                    @Override
                    public int getTintColor() {
                        return register.color;
                    }
                });
            }
        }
    }
}
