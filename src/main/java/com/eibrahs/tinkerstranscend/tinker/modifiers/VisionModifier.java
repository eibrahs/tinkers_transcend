package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.client.particle.TinkersTranscendParticles;
import com.eibrahs.tinkerstranscend.client.renderer.BlockOutlineRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.ArrayList;
import java.util.List;

public class VisionModifier extends Modifier implements
        GeneralInteractionModifierHook,
        SlotStackModifierHook,
        TooltipModifierHook
{
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.GENERAL_INTERACT,
                ModifierHooks.SLOT_STACK,
                ModifierHooks.TOOLTIP
        });
    }
    private static final String modifierName = "vision";
    private final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID, "scan_block");

    public int getPriority() {
        return 998;
    }
    public boolean overrideStackedOnOther(IToolStackView heldTool, ModifierEntry modifier, Slot slot, Player player) {
        return false;
    }

    public boolean overrideOtherStackedOnMe(IToolStackView tool, ModifierEntry modifier, ItemStack held, Slot slot, Player player, SlotAccess access) {
        if (tool.hasTag(TinkerTags.Items.SWORD)||tool.hasTag(TinkerTags.Items.RANGED)) return false;
        TagKey<Item> ores = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c","ores"));
        if (held.is(ores)){
            String key ;
            held.getTags().forEach(tag->{
                if (tag.location().getNamespace()=="c"
                        && tag.location().getPath().contains("ores/")
                ) tool.getPersistentData().putString(KEY, tag.location().toString());
            });
            //slotTool.getPersistentData().putString(KEY, BuiltInRegistries.ITEM.getKey(held.getItem()).toString());
            player.playSound(SoundEvents.AMETHYST_BLOCK_HIT);
            BlockOutlineRenderer.clear();
            return true;
        }else if(held.isEmpty()){
            tool.getPersistentData().remove(KEY);
            player.playSound(SoundEvents.AMETHYST_BLOCK_BREAK);
            BlockOutlineRenderer.clear();
            return true;
        }
        return false;
    }
    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifierEntry, Player player, InteractionHand interactionHand, InteractionSource interactionSource) {
        Level level = player.level();
        int modifierLevel = modifierEntry.getLevel();
        if (!player.isCreative()) {
            int toolDamage = modifierLevel * 3 - 2;
            if (tool.getCurrentDurability() < toolDamage) return InteractionResult.FAIL;
            tool.setDamage(tool.getDamage() + toolDamage);
        }
        if (!(level.isClientSide)) return InteractionResult.PASS;


        if(tool.hasTag(TinkerTags.Items.SWORD)||tool.hasTag(TinkerTags.Items.RANGED)) {
            findEntities(level, player, Math.min(16.0 * modifierEntry.getLevel(), 64.0));
            level.playLocalSound(player, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }

        if(tool.hasTag(TinkerTags.Items.HARVEST)) {
            double maxRange = Math.min(128.0, 5.0 * modifierEntry.getLevel());
            BlockHitResult result = blockRayTrace(level, player, maxRange);
            if (result == null) return InteractionResult.FAIL;
            Vec3 playerPos = player.getEyePosition();
            Vec3 resultPos = result.getLocation();
            level.addParticle(ParticleTypes.SONIC_BOOM, resultPos.x, resultPos.y, resultPos.z, 0, 0, 0);
            level.playLocalSound(result.getBlockPos(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0f, 1.0f, false);

            Vec3 targetPos = result.getBlockPos().getCenter();
            Vec3 normDir = (targetPos.subtract(playerPos)).normalize().multiply(0.5, 0.5, 0.5);
            Vec3 particlePos = playerPos.add(normDir);
            while (particlePos.distanceTo(targetPos) > 0.9 && particlePos.distanceTo(targetPos) < maxRange) {
                level.addParticle(ParticleTypes.HAPPY_VILLAGER, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
                particlePos = particlePos.add(normDir);
            }
            ModDataNBT persistentData = tool.getPersistentData();
            String filter = "c:ores";
            if (persistentData.contains(KEY)) filter=persistentData.getString(KEY);
            TagKey<Block> ores = TagKey.create(Registries.BLOCK, ResourceLocation.parse(filter));

            findOres(level, result.getBlockPos(), Math.min(2 + modifierEntry.getLevel(), 16),ores);
        }
        return InteractionResult.SUCCESS;
    }
    public static BlockHitResult blockRayTrace(Level worldIn, Player player, double distance) {
        if (player.pick(distance,1.0f,false) instanceof BlockHitResult result) return result;
        return null;
    }
    private void findEntities(Level level,Player player,double radius){
        AABB area = player.getBoundingBox().inflate(radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class,area,entity->!(entity instanceof Player));
        for(LivingEntity entity : entities){
            Vec3 particlePos = entity.getEyePosition();
            int color = getEntityColor(entity,player);
            level.addParticle(ColorParticleOption.create(TinkersTranscendParticles.VISION_ENTITY_PARTICLE.get(),color),particlePos.x,particlePos.y,particlePos.z,0,0,0);
        }
    };
    private void findOres(Level level,BlockPos blockPos,int radius,TagKey<Block> filter){
        Vec3i start = new Vec3i(blockPos.getX()-(radius-1),blockPos.getY()-(radius-1),blockPos.getZ()-(radius-1));
        int length = radius*2-1;

        BlockOutlineRenderer.clear();
        List<BlockPos> blocks = new ArrayList<>();
        for(int i=0;i<length;i++){
            for(int j=0;j<length;j++){
                for(int k=0;k<length;k++){
                    BlockPos scan = new BlockPos(start.getX()+i,start.getY()+j,start.getZ()+k);
                    BlockState state = level.getBlockState(scan);
                    if (state.is(filter)){
                        blocks.add(scan);
                        //Vec3 particlePos = scan.getCenter();
                        //int color = state.getMapColor(level,scan).col;
                        //level.addParticle(ColorParticleOption.create(TinkersTranscendParticles.VISION_PARTICLE.get(),color),particlePos.x,particlePos.y,particlePos.z,0,0,0);
                    }
                }
            }
        }
        BlockOutlineRenderer.addBlocks(blocks);
        VisionTimer.set(60);
    };
    private int getEntityColor(LivingEntity entity,Player player){
        if (entity.isInLava()) return MapColor.FIRE.col;
        if (entity.isInWater()) return MapColor.WATER.col;
        if (entity.isInWall()) return MapColor.STONE.col;
        if (entity instanceof ZombifiedPiglin|| entity instanceof Piglin) return MapColor.COLOR_PINK.col;
        if (entity instanceof EnderMan) return MapColor.COLOR_PURPLE.col;
        if (entity instanceof Blaze) return MapColor.FIRE.col;
        if (entity instanceof NeutralMob) return MapColor.COLOR_YELLOW.col;
        if (entity instanceof Enemy) return MapColor.COLOR_RED.col;
        return  MapColor.COLOR_LIGHT_GREEN.col;
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (player != null) {
            ModDataNBT persistentData = tool.getPersistentData();
            if (persistentData.contains(this.KEY)) {
                tooltip.add(Component.translatable("modifier.tinkers_transcend."+modifierName+".tooltip").append(persistentData.getString(KEY)).withColor(modifier.getModifier().getColor()));
            }
        }
    }
    @EventBusSubscriber
    public static class VisionTimer {
        private static int tickCounter = 0;

        public static void set(int ticks) {
            tickCounter = ticks;
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            if (tickCounter > 0) {
                tickCounter--;
                if (tickCounter == 0) {
                    BlockOutlineRenderer.clear();
                }
            }
        }
    }
}
