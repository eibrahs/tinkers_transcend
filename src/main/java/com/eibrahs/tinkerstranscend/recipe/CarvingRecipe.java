package com.eibrahs.tinkerstranscend.recipe;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.container.IEmptyContainer;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;

import java.util.List;

public class CarvingRecipe implements ICustomOutputRecipe<IEmptyContainer> {
    private final ResourceLocation id;
    private final EntityIngredient ingredient;
    private final List<OutputEntry> outputs;
    private final int count;
    private final boolean requiresWeakness;
    public CarvingRecipe(ResourceLocation id, EntityIngredient ingredient, List<CarvingRecipe.OutputEntry> outputs,int count,boolean requiresWeakness) {
        this.id = id;
        this.ingredient = ingredient;
        this.outputs = outputs;
        this.count = count;
        this.requiresWeakness = requiresWeakness;
    }

    public boolean matches(EntityType<?> type) {
        return this.ingredient.test(type);
    }

    public Pair<ItemStack,Float> rollOutput(RandomSource random) {
        float total = 0.0f;
        for (OutputEntry entry : this.outputs) {
            total += entry.chance();
        }
        float roll = random.nextFloat() * total;
        for (OutputEntry entry : this.outputs) {
            roll -= entry.chance();
            if (roll <= 0.0f) {
                return Pair.of(entry.output().get().copy(),entry.damage());
            }
        }
        return Pair.of(ItemStack.EMPTY,0.0f);
    }

    @Deprecated
    public boolean matches(@NotNull IEmptyContainer inv, @NotNull Level level) {
        return false;
    }

    public @NotNull RecipeSerializer<?> getSerializer() { return TinkersTranscendRecipes.CARVING_SERIALIZER.get(); }

    public @NotNull RecipeType<?> getType() { return TinkersTranscendRecipes.CARVING_TYPE.get(); }

    public ResourceLocation getId() { return this.id; }

    public static final RecordLoadable<CarvingRecipe> LOADER = RecordLoadable.create(
            ContextKey.ID.requiredField(),
            EntityIngredient.LOADABLE.requiredField("entity", CarvingRecipe::getIngredient),
            OutputEntry.LOADER.list(1).requiredField("outputs", CarvingRecipe::getOutputs),
            IntLoadable.ANY_FULL.defaultField("count",3,CarvingRecipe::getCount),
            BooleanLoadable.DEFAULT.defaultField("weakness",false,CarvingRecipe::isRequiresWeakness),
            CarvingRecipe::new
    );
    public EntityIngredient getIngredient(){
        return ingredient;
    }
    public List<OutputEntry> getOutputs(){
        return outputs;
    }
    public int getCount(){return count;}
    public boolean isRequiresWeakness(){return requiresWeakness;}

    public record OutputEntry(ItemOutput output, float chance,float damage) {
        public static final RecordLoadable<OutputEntry> LOADER = RecordLoadable.create(
                ItemOutput.Loadable.REQUIRED_STACK.requiredField("item", OutputEntry::output),
                FloatLoadable.PERCENT.requiredField("chance", OutputEntry::chance),
                FloatLoadable.ANY.defaultField("damage",0.0f,OutputEntry::damage),
                OutputEntry::new
        );
    }
}
