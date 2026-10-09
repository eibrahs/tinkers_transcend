package com.eibrahs.tinkerstranscend.entity.projectile;

import com.eibrahs.tinkerstranscend.TTConfig;
import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.tinker.modifiers.TinkersTranscendModifiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class FoxSoulArrowEntity extends AbstractArrow {
    private static final float MAGIC_DAMAGE = (float) (TTConfig.MODIFIER_FOX_SOUL_MAGIC_DAMAGE.getAsDouble());
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "fox_soul")
    );
    public FoxSoulArrowEntity(EntityType<FoxSoulArrowEntity> entityType, Level level) {
        super(entityType,level);
        this.pickup = Pickup.DISALLOWED;
    }
    public FoxSoulArrowEntity(EntityType<? extends FoxSoulArrowEntity> entityType,
                              LivingEntity shooter, Level level, ItemStack pickupItemStack,
                              ItemStack weapon) {
        super(entityType, shooter, level, pickupItemStack, weapon);
        this.pickup = Pickup.DISALLOWED;
    }
    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        super.doPostHurtEffects(target);
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0), this.getEffectSource());
    }
    @Override
    protected ItemStack getDefaultPickupItem() {
        return null;
    }
    @Override
    protected void onHitBlock(BlockHitResult result) {
        hitParticles(result.getLocation());
        super.onHitBlock(result);
        this.discard();
    }
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel serverLevel && result.getEntity() instanceof LivingEntity target) {
            Vec3 start = this.position();
            Vec3 end = start.add(this.getDeltaMovement());
            Vec3 hitPos = target.getBoundingBox().inflate(0.3).clip(start, end).orElse(end);
            hitParticles(hitPos);

            DamageSource magicSource = new DamageSource(serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE_TYPE), this, this.getOwner());
            target.hurt(magicSource, MAGIC_DAMAGE*getModifierLevel());
        }
    }
    private void hitParticles(Vec3 location) {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, location.x,location.y,location.z, 8, 0.2, 0.2, 0.2, Math.min(0.05,0.01*getModifierLevel()));
        }
    }
    @Override
    public void playSound(SoundEvent sound, float volume, float pitch){
        this.level().playSound(null,this.getX(),this.getY(),this.getZ(), SoundEvents.WOLF_ARMOR_BREAK, SoundSource.NEUTRAL,0.5f,1.5f);
        this.level().playSound(null,this.getX(),this.getY(),this.getZ(), SoundEvents.CHAIN_BREAK, SoundSource.NEUTRAL,1.5f,1.5f);

    }
    private int getModifierLevel(){
        if (this.getOwner() instanceof LivingEntity entity){
            return ToolStack.copyFrom(entity.getWeaponItem()).getModifierLevel(TinkersTranscendModifiers.FOX_SOUL_MODIFIER.get());
        }
        return 0;
    }

}
