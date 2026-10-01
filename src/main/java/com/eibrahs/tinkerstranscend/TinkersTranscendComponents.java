package com.eibrahs.tinkerstranscend;

import com.eibrahs.tinkerstranscend.tinker.modifiers.TTMultiphase;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class TinkersTranscendComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS;
    public static final DataComponentType<TTMultiphase> TTMULTIPHASE;
    public TinkersTranscendComponents(){};
    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        DataComponentType.Builder<T> builder = DataComponentType.builder();
        customizer.accept(builder);
        DataComponentType<T> componentType = builder.build();
        COMPONENTS.register(name, () -> componentType);
        return componentType;
    }
    static{
        COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TinkersTranscend.MODID);
        TTMULTIPHASE = register("ttmultiphase", (b) -> b.persistent(TTMultiphase.CODEC.codec()).networkSynchronized(TTMultiphase.STREAM_CODEC));
    }

}
