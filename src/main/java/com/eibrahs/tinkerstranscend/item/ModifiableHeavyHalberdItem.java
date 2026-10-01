package com.eibrahs.tinkerstranscend.item;

import com.eibrahs.tinkerstranscend.tinker.ThrownModifiableHeavyHalberdEntity;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.dubhe.anvilcraft.client.renderer.item.ItemUseAnimationTransform;
import dev.dubhe.anvilcraft.entity.ThrownEmberMetalHeavyHalberdEntity;
import dev.dubhe.anvilcraft.entity.ThrownHeavyHalberdEntity;
import dev.dubhe.anvilcraft.item.HeavyHalberdItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.*;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.display.ToolNameHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import slimeknights.tconstruct.library.tools.helper.*;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.item.TinkerTier;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.ItemStackUtil;
import slimeknights.tconstruct.tools.TinkerItemAbilities;
import slimeknights.tconstruct.tools.data.ModifierIds;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ModifiableHeavyHalberdItem extends HeavyHalberdItem implements IModifiableDisplay {
    private final ToolDefinition toolDefinition;
    private final int maxStackSize;
    private ItemStack toolForRendering;


    public ModifiableHeavyHalberdItem(Properties properties, ToolDefinition toolDefinition) {
        this(properties, toolDefinition, 1);
    }
    public ModifiableHeavyHalberdItem(Item.Properties properties, ToolDefinition toolDefinition, int maxStackSize) {
        super(TinkerTier.INSTANCE, properties.component(DataComponents.TOOL, createToolProperties(TinkerTier.INSTANCE)));
        this.toolDefinition = toolDefinition;
        this.maxStackSize = maxStackSize;
    }

    @Override
    protected double getBaseAttackDamage() {
        return 0;
    }

    @Override
    public ThrownHeavyHalberdEntity createThrown(Level level, LivingEntity shooter, ItemStack pickupItemStack) {
        return new ThrownModifiableHeavyHalberdEntity(level, shooter, pickupItemStack);
    }

    @Override
    public ThrownHeavyHalberdEntity createThrown(Level level, double x, double y, double z, ItemStack pickupItemStack) {
        return new ThrownModifiableHeavyHalberdEntity(level, x, y, z, pickupItemStack);
    }
    @Override
    public @NotNull Tier getTier(){
        return Tiers.DIAMOND;
    }

    public int getMaxStackSize(ItemStack stack) {
        return stack.isDamaged() ? 1 : this.maxStackSize;
    }
    public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
        return true;
    }

    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(EnchantmentTags.CURSE) && super.supportsEnchantment(stack, enchantment);
    }


    public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
        return EnchantmentModifierHook.getEnchantmentLevel(stack, enchantment);
    }

    public @NotNull ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
        return EnchantmentModifierHook.getAllEnchantments(stack, lookup);
    }

    public void verifyComponentsAfterLoad(ItemStack stack) {
        CompoundTag nbt = ItemStackUtil.getTag(stack);
        if (nbt != null) {
            ToolStack.verifyTag(this, nbt, this.getToolDefinition());
        }

    }

    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        ToolStack.ensureInitialized(stack, this.getToolDefinition());
    }
    public boolean isFoil(ItemStack stack) {
        return ModifierUtil.checkVolatileFlag(stack, SHINY);
    }

    public Rarity getRarity(ItemStack stack) {
        return RarityModule.getRarity(stack);
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return IndestructibleItemEntity.hasCustomEntity(stack);
    }

    @Nullable
    public Entity createEntity(Level world, Entity original, ItemStack stack) {
        return IndestructibleItemEntity.createFrom(world, original, stack);
    }

    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return false;
    }

    public boolean canBeDepleted() {
        return true;
    }

    public int getMaxDamage(ItemStack stack) {
        return ToolDamageUtil.getFakeMaxDamage(stack);
    }

    public int getDamage(ItemStack stack) {
        return !this.canBeDepleted() ? 0 : ToolStack.from(stack).getDamage();
    }

    public void setDamage(ItemStack stack, int damage) {
        if (this.canBeDepleted()) {
            ToolStack.from(stack).setDamage(damage);
            stack.set(DataComponents.DAMAGE, damage);
        }

    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<Item> onBroken) {
        int willDamage = super.damageItem(stack, amount, damager, onBroken);
        return ToolDamageUtil.handleDamageItem(stack, willDamage, damager, onBroken);
    }

    public boolean isBarVisible(ItemStack stack) {
        return stack.getCount() == 1 && DurabilityDisplayModifierHook.showDurabilityBar(stack);
    }

    public int getBarColor(ItemStack pStack) {
        return DurabilityDisplayModifierHook.getDurabilityRGB(pStack);
    }

    public int getBarWidth(ItemStack pStack) {
        return DurabilityDisplayModifierHook.getDurabilityWidth(pStack);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        return stack.getCount() > 1 || EntityInteractionModifierHook.leftClickEntity(stack, player, target);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
        return AttributesModifierHook.getHeldAttributeModifiers(tool, slot);
    }

    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        if (ItemStackUtil.getTag(stack) == null) {
            return ItemAttributeModifiers.EMPTY;
        } else {
            ToolStack tool = ToolStack.from(stack);
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
            addAttributeModifiers(builder, this.getAttributeModifiers(tool, EquipmentSlot.MAINHAND), EquipmentSlot.MAINHAND);
            addAttributeModifiers(builder, this.getAttributeModifiers(tool, EquipmentSlot.OFFHAND), EquipmentSlot.OFFHAND);
            return builder.build();
        }
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return this.canPerformAction(stack, TinkerItemAbilities.SHIELD_DISABLE);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack,target,attacker);
        ToolStack tool = ToolStack.from(stack);
        if (!tool.isBroken() && !tool.hasTag(TinkerTags.Items.UNARMED)) {
            int durabilityLost = 1;
            if (!tool.hasTag(TinkerTags.Items.MELEE_PRIMARY)) {
                durabilityLost = 2;
            }

            ToolDamageUtil.damageAnimated(tool, durabilityLost, attacker, EquipmentSlot.MAINHAND);
        }

    }

    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return IsEffectiveToolHook.isEffective(ToolStack.from(stack), state);
    }

    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        return ToolHarvestLogic.mineBlock(stack, worldIn, state, pos, entityLiving);
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return stack.getCount() == 1 ? MiningSpeedToolHook.getDestroySpeed(stack, state) : 0.0F;
    }

    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        return stack.getCount() > 1 || ToolHarvestLogic.handleBlockBreak(stack, pos, player);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        //super.inventoryTick(stack,worldIn,entityIn,itemSlot,isSelected);
        InventoryTickModifierHook.heldInventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public boolean overrideStackedOnOther(ItemStack held, Slot slot, ClickAction action, Player player) {
        return SlotStackModifierHook.overrideStackedOnOther(held, slot, action, player);
    }

    public boolean overrideOtherStackedOnMe(ItemStack slotStack, ItemStack held, Slot slot, ClickAction action, Player player, SlotAccess access) {
        return SlotStackModifierHook.overrideOtherStackedOnMe(slotStack, held, slot, action, player, access);
    }

    protected static boolean shouldInteract(@Nullable LivingEntity player, ToolStack toolStack, InteractionHand hand) {
        IModDataView volatileData = toolStack.getVolatileData();
        if (volatileData.getBoolean(NO_INTERACTION)) {
            return false;
        } else if (hand == InteractionHand.OFF_HAND) {
            return true;
        } else {
            return player == null || !volatileData.getBoolean(DEFER_OFFHAND) || player.getOffhandItem().isEmpty();
        }
    }

    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (stack.getCount() == 1) {
            ToolStack tool = ToolStack.from(stack);
            InteractionHand hand = context.getHand();
            if (shouldInteract(context.getPlayer(), tool, hand)) {
                for(ModifierEntry entry : tool.getModifierList()) {
                    InteractionResult result = ((BlockInteractionModifierHook)entry.getHook(ModifierHooks.BLOCK_INTERACT)).beforeBlockUse(tool, entry, context, InteractionSource.RIGHT_CLICK);
                    if (result.consumesAction()) {
                        return result;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (stack.getCount() == 1) {
            ToolStack tool = ToolStack.from(stack);
            InteractionHand hand = context.getHand();
            if (shouldInteract(context.getPlayer(), tool, hand)) {
                for (ModifierEntry entry : tool.getModifierList()) {
                    InteractionResult result = ((BlockInteractionModifierHook) entry.getHook(ModifierHooks.BLOCK_INTERACT)).afterBlockUse(tool, entry, context, InteractionSource.RIGHT_CLICK);
                    if (result.consumesAction()) {
                        return result;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        ToolStack tool = ToolStack.from(stack);
        if (shouldInteract(playerIn, tool, hand)) {
            for(ModifierEntry entry : tool.getModifierList()) {
                InteractionResult result = ((EntityInteractionModifierHook)entry.getHook(ModifierHooks.ENTITY_INTERACT)).afterEntityUse(tool, entry, playerIn, target, hand, InteractionSource.RIGHT_CLICK);
                if (result.consumesAction()) {
                    return result;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        InteractionResultHolder<ItemStack> resultHolder = super.use(worldIn, playerIn, hand);
        if (resultHolder.getResult()==InteractionResult.PASS){
            ItemStack stack = playerIn.getItemInHand(hand);
            if (stack.getCount() > 1) {
                return InteractionResultHolder.pass(stack);
            } else {
                ToolStack tool = ToolStack.from(stack);
                if (shouldInteract(playerIn, tool, hand)) {
                    for(ModifierEntry entry : tool.getModifierList()) {
                        InteractionResult result = ((GeneralInteractionModifierHook)entry.getHook(ModifierHooks.GENERAL_INTERACT)).onToolUse(tool, entry, playerIn, hand, InteractionSource.RIGHT_CLICK);
                        if (result.consumesAction()) {
                            return new InteractionResultHolder(result, stack);
                        }
                    }
                }

                return InteractionResultHolder.pass(stack);
            }
        }
        return resultHolder;
    }

    public void onUseTick(Level pLevel, LivingEntity entityLiving, ItemStack stack, int timeLeft) {
        ToolStack tool = ToolStack.from(stack);
        ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
        GeneralInteractionModifierHook hook = (GeneralInteractionModifierHook)activeModifier.getHook(ModifierHooks.GENERAL_INTERACT);
        int duration = hook.getUseDuration(tool, activeModifier);

        for(ModifierEntry entry : tool.getModifiers()) {
            ((UsingToolModifierHook)entry.getHook(ModifierHooks.TOOL_USING)).onUsingTick(tool, entry, entityLiving, duration, timeLeft, activeModifier);
        }

        hook.onUsingTick(tool, activeModifier, entityLiving, timeLeft);
    }

    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        if (super.canContinueUsing(oldStack, newStack) && oldStack != newStack) {
            GeneralInteractionModifierHook.finishUsing(ToolStack.from(oldStack));
        }

        return super.canContinueUsing(oldStack, newStack);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        ToolStack tool = ToolStack.from(stack);
        ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
        GeneralInteractionModifierHook hook = (GeneralInteractionModifierHook)activeModifier.getHook(ModifierHooks.GENERAL_INTERACT);
        int duration = hook.getUseDuration(tool, activeModifier);

        for(ModifierEntry entry : tool.getModifiers()) {
            ((UsingToolModifierHook)entry.getHook(ModifierHooks.TOOL_USING)).beforeReleaseUsing(tool, entry, entityLiving, duration, 0, activeModifier);
        }

        hook.onFinishUsing(tool, activeModifier, entityLiving);
        return stack;
    }
    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        super.releaseUsing(stack,worldIn,entityLiving,timeLeft);
        if (getMode(stack)!=TRIDENT_MODE) {
            ToolStack tool = ToolStack.from(stack);
            ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
            GeneralInteractionModifierHook hook = (GeneralInteractionModifierHook) activeModifier.getHook(ModifierHooks.GENERAL_INTERACT);
            int duration = hook.getUseDuration(tool, activeModifier);

            for (ModifierEntry entry : tool.getModifiers()) {
                ((UsingToolModifierHook) entry.getHook(ModifierHooks.TOOL_USING)).beforeReleaseUsing(tool, entry, entityLiving, duration, timeLeft, activeModifier);
            }

            hook.onStoppedUsing(tool, activeModifier, entityLiving, timeLeft);
        }
    }

    public void onStopUsing(ItemStack stack, LivingEntity entity, int timeLeft) {
        ToolStack tool = ToolStack.from(stack);
        UsingToolModifierHook.afterStopUsing(tool, entity, timeLeft);
        GeneralInteractionModifierHook.finishUsing(tool);
    }

    public int getUseDuration(ItemStack stack) {
        ToolStack tool = ToolStack.from(stack);
        ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
        return activeModifier != ModifierEntry.EMPTY ? ((GeneralInteractionModifierHook)activeModifier.getHook(ModifierHooks.GENERAL_INTERACT)).getUseDuration(tool, activeModifier) : 0;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        int useDuration = super.getUseDuration(stack,entity);
        if (useDuration==0) {
            return this.getUseDuration(stack);
        }
        return useDuration;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        UseAnim useAnim =  super.getUseAnimation(stack);
        if (useAnim==UseAnim.NONE) {
            ToolStack tool = ToolStack.from(stack);
            ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
            return activeModifier != ModifierEntry.EMPTY ? ((GeneralInteractionModifierHook)activeModifier.getHook(ModifierHooks.GENERAL_INTERACT)).getUseAction(tool, activeModifier) : UseAnim.NONE;
        }
        return useAnim;
    }
    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
        boolean bool = super.canPerformAction(stack, toolAction);
        if (!bool) {
            return stack.getCount() == 1 && ModifierUtil.canPerformAction(ToolStack.from(stack), toolAction);
        }
        return true;
    }

    public Component getName(ItemStack stack) {
        return ToolNameHook.getName(this.getToolDefinition(), stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack,context,tooltip,flag);
        TooltipUtil.addInformation(this, stack, context.level(), tooltip, SafeClientAccess.getTooltipKey(), flag);
    }

    public int getDefaultTooltipHideFlags(ItemStack stack) {
        return TooltipUtil.getModifierHideFlags(this.getToolDefinition());
    }

    public ItemStack getRenderTool() {
        if (this.toolForRendering == null) {
            this.toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
        }

        return this.toolForRendering;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProgress, float swingProgress) {
                return HeavyHalberdItem.getMode(stack) != 2 ? false : ItemUseAnimationTransform.applySwordBlock(poseStack, player, arm, equipProgress);
            }
        });
        ItemProperties.register(this, ResourceLocation.withDefaultNamespace("throwing"), (stack, level, entity, data) -> entity != null && getMode(stack) == 0 && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }

    public static boolean shouldCauseReequip(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack == newStack) {
            return false;
        } else if (!slotChanged && oldStack.getItem() == newStack.getItem()) {
            ToolStack oldTool = ToolStack.from(oldStack);
            ToolStack newTool = ToolStack.from(newStack);
            if (!oldTool.getMaterials().equals(newTool.getMaterials())) {
                return true;
            } else if (!oldTool.getModifierList().equals(newTool.getModifierList())) {
                return true;
            } else {
                return !newStack.getAttributeModifiers().equals(oldStack.getAttributeModifiers());
            }
        } else {
            return true;
        }
    }

    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return this.shouldCauseReequipAnimation(oldStack, newStack, false);
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return shouldCauseReequip(oldStack, newStack, slotChanged);
    }

    public static void addAttributeModifiers(ItemAttributeModifiers.Builder builder, Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlot slot) {
        EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(slot);
        modifiers.forEach((attribute, modifier) -> builder.add(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute), modifier, group));
    }

    public static BlockHitResult blockRayTrace(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
        return Item.getPlayerPOVHitResult(worldIn, player, fluidMode);
    }

    public ToolDefinition getToolDefinition() {
        return this.toolDefinition;
    }

}
