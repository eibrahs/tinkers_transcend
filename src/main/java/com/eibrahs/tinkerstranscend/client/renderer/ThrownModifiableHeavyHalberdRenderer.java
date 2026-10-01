package com.eibrahs.tinkerstranscend.client.renderer;

import com.eibrahs.tinkerstranscend.tinker.ThrownModifiableHeavyHalberdEntity;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.dubhe.anvilcraft.client.renderer.entity.ThrownHeavyHalberdRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class ThrownModifiableHeavyHalberdRenderer extends ThrownHeavyHalberdRenderer<ThrownModifiableHeavyHalberdEntity> {
    private final Cache<Item, List<BakedQuad>> quadCache;
    public ThrownModifiableHeavyHalberdRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.quadCache = CacheBuilder.newBuilder()
                .maximumSize(32)
                .build();
    }
    @Override
    public void render(ThrownModifiableHeavyHalberdEntity entity, float yaw, float partialTicks,
                       PoseStack pose, MultiBufferSource buffer, int packedLight) {
        //获取模型Quad
        ItemStack weaponItem = entity.getWeaponItem();
        BakedModel model = Minecraft.getInstance().getItemRenderer()
                .getItemModelShaper().getItemModel(weaponItem);
        if (weaponItem.isEmpty()) return;
        List<BakedQuad> quads;
        try {
            quads = quadCache.get(weaponItem.getItem(), () -> bakeQuads(model));
        } catch (ExecutionException e) {
            return;
        }

        //调整变换
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) + 90.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(
                45.0F - Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        pose.translate(0.31F, -0.31F, 0.0F);
        ItemTransform fixedTransform = model.getTransforms().getTransform(ItemDisplayContext.FIXED);
        fixedTransform.apply(false, pose);
        pose.translate(-0.5F, -0.5F, -0.5F);

        //获取材料颜色
        List<MaterialVariantId> materials = entity.getMaterials();
        Map<Integer, float[]> colorCache = new HashMap<>();
        for (BakedQuad quad : quads) {
            int partIndex = quad.getTintIndex();
            if (partIndex >= 0 && partIndex < materials.size() && !colorCache.containsKey(partIndex)) {
                float[] color = getMaterialColor(materials.get(partIndex));
                colorCache.put(partIndex, color);
            }
        }

        //对Quad染色
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
        PoseStack.Pose lastPose  = pose.last();
        for (BakedQuad quad : quads) {
            int partIndex = quad.getTintIndex();
            float[] c = colorCache.get(partIndex);
            float r,g,b,a;
            if (c!=null) {
                r = c[0];g = c[1];b = c[2];a = c[3];
            }else{
                r= 1.0f;g=1.0f;b=1.0f;a=1.0f;
            }
            consumer.putBulkData(lastPose , quad, r, g, b, a, packedLight, OverlayTexture.NO_OVERLAY);
        }
        pose.popPose();
    }
    private static float[] getMaterialColor(MaterialVariantId material) {
        Optional<MaterialRenderInfo> renderInfo =
                MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
        if (renderInfo.isPresent()) {
            int argb = renderInfo.get().vertexColor();
            return new float[]{
                    ((argb >> 16) & 0xFF) / 255.0f,
                    ((argb >> 8) & 0xFF) / 255.0f,
                    (argb & 0xFF) / 255.0f,
                    ((argb >> 24) & 0xFF) / 255.0f
            };
        }
        return new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    }
    private List<BakedQuad> bakeQuads(BakedModel model) {
        List<BakedQuad> quads = new ArrayList<>();
        RandomSource random = RandomSource.create();
        for (Direction dir : Direction.values()) {
            quads.addAll(model.getQuads(null, dir, random, ModelData.EMPTY, null));
        }
        quads.addAll(model.getQuads(null, null, random, ModelData.EMPTY, null));
        return quads;
    }
}
