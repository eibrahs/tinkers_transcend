package com.eibrahs.tinkerstranscend;

import com.eibrahs.tinkerstranscend.tinker.ThrownModifiableHeavyHalberdEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


@Mod(TinkersTranscend.MODID)
public class TinkersTranscendEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TinkersTranscend.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownModifiableHeavyHalberdEntity>> THROWN_MODIFIABLE_HEAVY_HALBERD;

    static{
        THROWN_MODIFIABLE_HEAVY_HALBERD = ENTITY_TYPES.register(
                "heavy_halberd_throwing",
                ()->EntityType.Builder.<ThrownModifiableHeavyHalberdEntity>of(ThrownModifiableHeavyHalberdEntity::new, MobCategory.MISC)
                    .sized(0.5F,0.5F)
                    .build("tinkers_transcend:heavy_halberd_throwing")
        );
    }
}
