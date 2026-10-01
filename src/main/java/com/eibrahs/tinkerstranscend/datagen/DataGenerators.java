package com.eibrahs.tinkerstranscend.datagen;
import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.recipe.CarvingRecipeProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = TinkersTranscend.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<CarvingRecipeProvider>) (PackOutput output) -> new CarvingRecipeProvider(output, event.getLookupProvider())
        );
    }
}