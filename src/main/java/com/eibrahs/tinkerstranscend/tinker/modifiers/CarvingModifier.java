package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.effect.TinkersTranscendEffects;
import com.eibrahs.tinkerstranscend.recipe.CarvingRecipe;
import com.eibrahs.tinkerstranscend.recipe.TinkersTranscendRecipes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class CarvingModifier extends Modifier implements
        ProcessLootModifierHook,
        EntityInteractionModifierHook
{
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addModule(EnchantmentModule.builder(Enchantments.LOOTING).lootingLevel(LevelingInt.eachLevel(3)).toolTag(TinkerTags.Items.MELEE).constant());
        //hookBuilder.addModule(LootingModule.builder().lootingLevel(LevelingInt.eachLevel(3)).toolTag(TinkerTags.Items.MELEE).weapon());

        hookBuilder.addHook(this,new ModuleHook[]{
                ModifierHooks.ENTITY_INTERACT,
                ModifierHooks.PROCESS_LOOT
        });
    }
    public int getPriority() {
        return 998;
    }


    @Override
    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
        if (context.hasParam(LootContextParams.DAMAGE_SOURCE)) {
            Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);

            if (entity != null){
                for(ItemStack stack : generatedLoot){
                    stack.setCount(stack.getCount() + context.getRandom().nextInt(getBounceCount(stack)*modifier.getLevel()));

                }
            }
        }
    }
    private int getBounceCount(ItemStack stack){
        if(stack.is(Items.FEATHER)) return 12;
        if(stack.is(Items.LEATHER)||stack.is(Items.RABBIT_HIDE)) return 3;
        if(stack.is(TagKey.create(Registries.ITEM,ResourceLocation.parse("minecraft:meat")))) return 6;
        return 1;
    }

    @Override
    public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
        Holder<MobEffect> effect = TinkersTranscendEffects.CARVED;
        Level world = player.level();
        if (world.isClientSide)return InteractionResult.PASS;

        int amplifier = -1;
        if ((target.hasEffect(effect))) {
            amplifier = target.getEffect(effect).getAmplifier();
        }
        if (!player.isCreative()) {
        if (tool.getCurrentDurability() <= 0) return InteractionResult.PASS;
        tool.setDamage(tool.getDamage()+1);
        }

        List <RecipeHolder<CarvingRecipe>> recipes = world.getRecipeManager().getAllRecipesFor(TinkersTranscendRecipes.CARVING_TYPE.get());

        for(RecipeHolder<CarvingRecipe> holder : recipes){
            CarvingRecipe recipe = holder.value();
            if (amplifier>=(recipe.getCount()+modifier.getLevel()-2)) continue;

            if (recipe.isRequiresWeakness()&&!isWeakness(target,player,0.1f*modifier.getLevel())) return InteractionResult.PASS;
            if (!recipe.matches(target.getType())) continue;

            target.addEffect(new MobEffectInstance(effect,20000,amplifier+1));

            Pair<ItemStack,Float> pair = recipe.rollOutput(world.getRandom());
            ItemStack stack = pair.getFirst();
            float damage = pair.getSecond();
            SoundEvent sound = SoundEvents.SHEEP_SHEAR;
            if (damage>0.0f){
                target.hurt(player.damageSources().playerAttack(player),damage);
                sound = SoundEvents.SLIME_HURT;
                tool.setDamage(tool.getDamage()+1);
            }
            double dx = world.getRandom().nextDouble()*0.2;
            double dz = world.getRandom().nextDouble()*0.2;
            world.addFreshEntity(new ItemEntity(target.level(),target.getX(),target.getY()+0.5,target.getZ(),stack,dx,0.2,dz));
            world.playSound(null,target.getX(),target.getY()+0.5,target.getZ(),sound, SoundSource.VOICE,1.0f,1.0f);
            return InteractionResult.SUCCESS;

        }


        return InteractionResult.PASS;
    }
    private boolean isWeakness(LivingEntity target,Player player,float healthRate){
        return ((Mob)target).getTarget()!=player || target.hasEffect(MobEffects.WEAKNESS) || target.getHealth()/target.getMaxHealth()<healthRate;
    }
}
