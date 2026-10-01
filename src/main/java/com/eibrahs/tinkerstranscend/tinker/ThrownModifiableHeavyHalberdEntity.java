package com.eibrahs.tinkerstranscend.tinker;

import com.eibrahs.tinkerstranscend.TinkersTranscendEntities;
import dev.dubhe.anvilcraft.entity.ThrownHeavyHalberdEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ThrownModifiableHeavyHalberdEntity extends ThrownHeavyHalberdEntity {
    private static final EntityDataAccessor<String> DATA_MATERIALS =
            SynchedEntityData.defineId(ThrownModifiableHeavyHalberdEntity.class, EntityDataSerializers.STRING);
    public ThrownModifiableHeavyHalberdEntity(EntityType<? extends ThrownModifiableHeavyHalberdEntity> type, Level level) {
        super(type, level);
    }
    public ThrownModifiableHeavyHalberdEntity(Level level, LivingEntity shooter, ItemStack pickupItemStack) {
        super(TinkersTranscendEntities.THROWN_MODIFIABLE_HEAVY_HALBERD.get(), level, shooter, pickupItemStack);
        setMaterialsFromStack(pickupItemStack);
    }

    public ThrownModifiableHeavyHalberdEntity(Level level, double x, double y, double z, ItemStack pickupItemStack) {
        super(TinkersTranscendEntities.THROWN_MODIFIABLE_HEAVY_HALBERD.get(), level, x, y, z, pickupItemStack);
        setMaterialsFromStack(pickupItemStack);
    }
    @Override
    protected ItemStack getDefaultPickupItem() {
        return TinkersTranscendTools.HEAVY_HALBERD.get().getDefaultInstance();
    }
    public ItemStack getItem(){
        return getPickupItem();
    }
    public void setMaterialsFromStack(ItemStack stack) {
        List<MaterialVariantId> materials = MaterialIdNBT.from(stack).getMaterials();
        this.entityData.set(DATA_MATERIALS, serializeMaterials(materials));
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder); // ← 必须调用，否则父类的数据访问器不会被注册
        builder.define(DATA_MATERIALS, ""); // 你自定义的数据访问器
    }
    // 客户端/服务端读取材料
    public List<MaterialVariantId> getMaterials() {
        String serialized = this.entityData.get(DATA_MATERIALS);
        return deserializeMaterials(serialized);
    }

    // 序列化：把材料列表转成逗号分隔的字符串
    private static String serializeMaterials(List<MaterialVariantId> materials) {
        return materials.stream().map(MaterialVariantId::toString).collect(Collectors.joining(","));
    }

    // 反序列化：从字符串还原材料列表
    private static List<MaterialVariantId> deserializeMaterials(String serialized) {
        if (serialized.isEmpty()) return List.of();
        return Arrays.stream(serialized.split(","))
                .map(MaterialVariantId::tryParse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // 从 NBT 读取材料
        if (tag.contains("Materials")) {
            this.entityData.set(DATA_MATERIALS, tag.getString("Materials"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        // 把材料写入 NBT
        tag.putString("Materials", this.entityData.get(DATA_MATERIALS));
    }
}
