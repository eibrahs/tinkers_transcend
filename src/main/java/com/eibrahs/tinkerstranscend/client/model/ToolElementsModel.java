package com.eibrahs.tinkerstranscend.client.model;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.*;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.tools.NestedOverrides;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.*;
import net.minecraft.client.renderer.block.model.BlockModel;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class ToolElementsModel implements IUnbakedGeometry<ToolElementsModel> {
    private final List<BlockElement> elements;
    private final List<ToolPart> toolParts;
    private final List<ItemOverride> itemOverrides;

    public ToolElementsModel(List<BlockElement> elements, List<ToolPart> toolParts) {
        this.elements = elements;
        this.toolParts = toolParts;
        this.itemOverrides = new ArrayList<>();
    }
    public ToolElementsModel(List<BlockElement> elements, List<ToolPart> toolParts,List<ItemOverride> itemOverrides) {
        this.elements = elements;
        this.toolParts = toolParts;
        this.itemOverrides = itemOverrides;
    }

    protected void addQuads(IGeometryBakingContext context, @NotNull IModelBuilder<?> modelBuilder, @NotNull ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelState,ItemOverrides overrides,List<MaterialVariantId> materials) {
        // If there is a root transform, undo the ModelState transform, apply it, then re-apply the ModelState transform.
        // This is necessary because of things like UV locking, which should only respond to the ModelState, and as such
        // that is the only transform that should be applied during face bake.
        var rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        }

        for (BlockElement element : elements) {
            for (Direction direction : element.faces.keySet()) {
                var face = element.faces.get(direction);
                var sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                var quad = BlockModel.bakeFace(element, face, sprite, direction, modelState);

                if (face.cullForDirection() == null)
                    modelBuilder.addUnculledFace(quad);
                else
                    modelBuilder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection()), quad);
            }
        }
    }
    public static BakedQuad addColor(BakedQuad quad,MaterialVariantId material){
        Optional<MaterialRenderInfo> renderInfo = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
        if (renderInfo.isEmpty()) {
            return quad;
        }
        int argb = renderInfo.get().vertexColor();
        int abgr = ColoredBlockModel.swapColorRedBlue(argb);
        int[] vertexData = quad.getVertices().clone();
        for(int i=0;i<4;i++){
            int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
            vertexData[offset] = abgr;
        }
        return new BakedQuad(vertexData,quad.getTintIndex(),quad.getDirection(),quad.getSprite(),quad.isShade(),quad.hasAmbientOcclusion());
    }
    private static record ToolPart(String name, int index) {
        public static final ToolPart DEFAULT = new ToolPart("tool", -1);
        public static final List<ToolPart> DEFAULT_PARTS;

        public boolean hasMaterials() {
            return this.index >= 0;
        }

        public String getName(boolean isLarge) {
            return isLarge ? "large_" + this.name : this.name;
        }

        public static ToolPart read(JsonObject json) {
            String name = GsonHelper.getAsString(json, "name");
            int index = GsonHelper.getAsInt(json, "index", -1);
            return new ToolPart(name, index);
        }

        static {
            DEFAULT_PARTS = List.of(DEFAULT);
        }
    }
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));

        var renderTypeHint = context.getRenderTypeHint();
        var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;


        List<BakedModel> customModels = new ArrayList<>();
        for (ItemOverride override : this.itemOverrides) {
            ResourceLocation modelLocation = override.getModel();
            BakedModel baked = baker.bake(modelLocation, modelState, spriteGetter);
            if (baked == null) continue;
            customModels.add(baked);
        }

        ItemOverrides materialOverrides = new MaterialOverrideHandler(context, toolParts, overrides,customModels);

        IModelBuilder<?> builder = IModelBuilder.of(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(),
                context.getTransforms(), materialOverrides, particle, renderTypes);

        addQuads(context, builder, baker, spriteGetter, modelState, materialOverrides, List.of());



        return builder.build();
    }

    public static final class Loader implements IGeometryLoader<ToolElementsModel> {
        public static final ToolElementsModel.Loader INSTANCE = new ToolElementsModel.Loader();

        private Loader() {}

        @Override
        public ToolElementsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
             if (!jsonObject.has("elements"))
                throw new JsonParseException("An element model must have an \"elements\" member.");

            List<BlockElement> elements = new ArrayList<>();
            for (JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "elements")) {
                elements.add(deserializationContext.deserialize(element, BlockElement.class));
            }

            return deserialize(jsonObject,deserializationContext,elements);
        }
        public static ToolElementsModel deserialize(JsonObject json, JsonDeserializationContext context,List<BlockElement> elements) {
            List<ToolPart> parts = Collections.emptyList();
            if (json.has("parts")) {
                parts = JsonHelper.parseList(json, "parts", ToolPart::read);
            }
            List<ItemOverride> itemOverrides = getOverrides(context,json);
            return new ToolElementsModel(elements,parts,itemOverrides);
        }
        public static List<ItemOverride> getOverrides(JsonDeserializationContext context, JsonObject json) {
            List<ItemOverride> list = Lists.newArrayList();
            if (json.has("overrides")) {
                for (JsonElement jsonelement : GsonHelper.getAsJsonArray(json, "overrides")) {
                    list.add(context.deserialize(jsonelement, ItemOverride.class));
                }
            }

            return list;
        }
    }
    public static final class MaterialOverrideHandler extends NestedOverrides {
        private final Cache<List <MaterialVariantId>, BakedModel> cache;
        private final List<Cache<List <MaterialVariantId>,BakedModel>> customCache;
        private final IGeometryBakingContext owner;
        private final List<ToolPart> toolParts;
        private final List<BakedModel> customModel;

        private MaterialOverrideHandler(IGeometryBakingContext owner, List<ToolPart> toolParts, ItemOverrides nest, List<BakedModel> customModels) {
            super(nest);
            this.cache = CacheBuilder.newBuilder().maximumSize((long)MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().size() * 3L / 2L).build();
            List<Cache<List <MaterialVariantId>,BakedModel>> list = new ArrayList<>();
            for(int i=0;i<customModels.size();i++){
                list.add(CacheBuilder.newBuilder().maximumSize((long)MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().size() * 3L / 2L).build());
            }
            this.customCache = list;
            this.owner = owner;
            this.toolParts = toolParts;
            this.customModel = customModels;
        }

        @Nullable
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            // 1. 获取材料数据
            List<MaterialVariantId> materials = MaterialIdNBT.from(stack).getMaterials();
            if (materials.isEmpty()) return originalModel;

            if (stack.has(DataComponents.CUSTOM_MODEL_DATA)) {
                CustomModelData customData = stack.get(DataComponents.CUSTOM_MODEL_DATA);
                int index = customData.value();
                if (index != 0) {
                    try {
                        return customCache.get(index-1).get(materials, () -> bakeWithMaterials(customModel.get(index - 1), materials,customModel.get(index-1).getTransforms()));
                    } catch (ExecutionException e) {
                        // 缓存加载失败，回退到原始模型
                        return originalModel;
                    }
                }
            }

            ImmutableList.Builder<Object> builder = ImmutableList.builder();
            // 2. 用材料列表作为缓存键，尝试从缓存获取
            try {
                return cache.get(materials, () -> bakeWithMaterials(originalModel, materials,null));
            } catch (ExecutionException e) {
                // 缓存加载失败，回退到原始模型
                return originalModel;
            }
        }
        private BakedModel bakeWithMaterials(BakedModel originalModel, List<MaterialVariantId> materials,ItemTransforms transforms) {
           if (materials.isEmpty()) return originalModel;

            List<BakedQuad> allQuads = new ArrayList<>();
            RandomSource random = RandomSource.create();

            for (Direction direction : Direction.values()) {
                allQuads.addAll(originalModel.getQuads(null, direction, random, ModelData.EMPTY, null));
            }
            allQuads.addAll(originalModel.getQuads(null, null, random, ModelData.EMPTY, null));

            List<BakedQuad> tintedQuads = new ArrayList<>();
            for (BakedQuad quad : allQuads) {
                int partIndex = quad.getTintIndex();
                if (partIndex >= 0 && partIndex < materials.size()) {
                    tintedQuads.add(addColor(quad, materials.get(partIndex)));
                }else{
                    tintedQuads.add(quad);
                }
            }

            Function<Material, TextureAtlasSprite> spriteGetter = Material::sprite;
            TextureAtlasSprite particle = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation()));

            IModelBuilder<?> builder = makeModelBuilder(owner,this,particle,transforms);
            for (BakedQuad quad : tintedQuads) {
                if (quad.getDirection() == null) {
                    builder.addUnculledFace(quad);  // 无方向的面
                } else {
                    builder.addUnculledFace(quad);  // 有方向的面（物品模型通常不需要剔除）
                }
            }
            // 4. 用染色后的 quad 构建新模型
            return builder.build();
        }
            private static IModelBuilder<?> makeModelBuilder(IGeometryBakingContext context, ItemOverrides overrides, TextureAtlasSprite particle,ItemTransforms transforms) {
            var renderTypeHint = context.getRenderTypeHint();
            var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;

            return IModelBuilder.of(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(), transforms==null?context.getTransforms():transforms, overrides, particle, renderTypes);
        }
    }
    private static record ToolCacheKey(List<MaterialVariantId> materials, List<Object> modifierData) {
    }
}
