package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscendComponents;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.item.property.component.Merciless;
import dev.dubhe.anvilcraft.util.EnchantmentUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record TTMultiphase(List<Phase> phases, int activePhase,List<UpgradePhase> upgradePhases,int activeUpgradePhase) {
    public static final int MIN_PHASE_COUNT =  2;
    public static final int MAX_PHASE_COUNT = 4;
    private static final List<String> DEFAULT_NAMES = List.of("α","β","γ","δ");
    private static final List<String> UPGRADE_NAMES = List.of("ε","λ","σ","ω");
    private static final MapCodec<TTMultiphase> DATA_CODEC = RecordCodecBuilder.mapCodec(
        (instance)->
            instance.group(
                    TTMultiphase.Phase.CODEC.codec().listOf().fieldOf("phases").forGetter(TTMultiphase::phases),
                    Codec.INT.fieldOf("active_phase").forGetter(TTMultiphase::activePhase),
                    TTMultiphase.UpgradePhase.CODEC.codec().listOf().fieldOf("upgradePhase").forGetter(TTMultiphase::upgradePhases),
                    Codec.INT.fieldOf("active_upgradePhase").forGetter(TTMultiphase::activeUpgradePhase),
                    Codec.BOOL.optionalFieldOf("merciless", false).forGetter((ignored) -> false)
                    ).apply(instance, (
                            phases,
                            activePhase,
                            upgradePhases,
                            activeUpgradePhase,
                            ignored) ->
                            new TTMultiphase(
                                    phases,
                                    activePhase,
                                    upgradePhases,
                                    activeUpgradePhase)
                    )
    );
    private static final MapCodec<TTMultiphase> LEGACY_CODEC;
    public static final MapCodec<TTMultiphase> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, TTMultiphase> STREAM_CODEC;

    public TTMultiphase(List<Phase> phases,int activePhase,List<UpgradePhase> upgradePhases,int activeUpgradePhase){
        phases = List.copyOf(phases);

        this.phases = phases;
        this.activePhase=activePhase;

        upgradePhases = List.copyOf(upgradePhases);
        this.upgradePhases = upgradePhases;
        this.activeUpgradePhase = activeUpgradePhase;
    }
    public int size(){
        return Math.min(this.phases.size(),this.upgradePhases.size());
    }
    public static TTMultiphase create(){
        return new TTMultiphase(
                List.of(TTMultiphase.Phase.EMPTY,TTMultiphase.Phase.EMPTY,TTMultiphase.Phase.EMPTY,TTMultiphase.Phase.EMPTY),
                0,
                List.of(UpgradePhase.EMPTY,UpgradePhase.EMPTY,UpgradePhase.EMPTY,UpgradePhase.EMPTY),
                0);
    }
    public static Component getEnchantmentPhaseName(int index){
        return Component.literal(DEFAULT_NAMES.get(index));
    }
    public static Component getUpgradePhaseName(int index){
        return Component.literal(UPGRADE_NAMES.get(index));
    }
    public static Component makeSuffix(int index,int upgradeIndex) {
        return Component.literal(" - ["+DEFAULT_NAMES.get(index)+","+UPGRADE_NAMES.get(upgradeIndex)+"]");
    }
    public static Component firstPhaseName(Component name) {
        return name.copy().append(makeSuffix(0,0));
    }
    public Component phaseDisplayName(int index) {
        Component name = (Component)(((TTMultiphase.Phase)this.phases.get(index)).customName().isPresent() ?
                ((Component)((TTMultiphase.Phase)this.phases.get(index)).customName().get()).copy()
                : getEnchantmentPhaseName(index));
        return name;
    }
    public Component upgradePhaseDisplayName(int upgradeIndex){
        Component name = (Component)(((TTMultiphase.UpgradePhase)this.upgradePhases.get(upgradeIndex)).customName().isPresent() ?
                ((Component)((TTMultiphase.UpgradePhase)this.upgradePhases.get(upgradeIndex)).customName().get()).copy()
                : getUpgradePhaseName(upgradeIndex));
        return name;
    }
    public TTMultiphase capture(ItemStack stack) {
        Merciless.disable(stack);
        ItemEnchantments enchantments = (ItemEnchantments)stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        List<Phase> captured = new ArrayList(this.phases);
        captured.set(this.activePhase, Phase.capture(stack, enchantments));

        CompoundTag upgradeTags = MultiphaseModifier.getUpgradeTag(stack);
        List<UpgradePhase> capturedUpgrade = new ArrayList(this.upgradePhases);
        capturedUpgrade.set(this.activeUpgradePhase,UpgradePhase.capture(stack,upgradeTags));
        return new TTMultiphase(captured, this.activePhase,capturedUpgrade,this.activeUpgradePhase);
    }

    public TTMultiphase forDisplay(ItemStack stack) {
        ItemEnchantments enchantments = (ItemEnchantments)stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        enchantments = EnchantmentUtil.merge(enchantments, (ItemEnchantments)stack.getOrDefault(ModComponents.MERCILESS_ENCHANTMENTS, ItemEnchantments.EMPTY));
        List<Phase> displayed = new ArrayList(this.phases);
        displayed.set(this.activePhase, Phase.capture(stack, enchantments));

        CompoundTag upgradeTags = MultiphaseModifier.getUpgradeTag(stack);
        List<UpgradePhase> displayedUpgrade = new ArrayList(this.upgradePhases);
        displayedUpgrade.set(this.activeUpgradePhase,UpgradePhase.capture(stack,upgradeTags));

        return new TTMultiphase(displayed, this.activePhase, displayedUpgrade,this.activeUpgradePhase);
    }

    public void select(ItemStack stack,int phaseIndex,int upgradePhaseIndex){
        if(phaseIndex<0||phaseIndex>=phases.size()
                ||upgradePhaseIndex<0 || upgradePhaseIndex>=upgradePhases.size()) return;
        TTMultiphase selected = this.capture(stack).withSelection(phaseIndex,upgradePhaseIndex);
        selected.applyToStack(stack);
        stack.set(TinkersTranscendComponents.TTMULTIPHASE,selected);
    }
    public void selectEnchantment(ItemStack stack,int phaseIndex){
        if(phaseIndex<0||phaseIndex>=phases.size()) return;
        TTMultiphase selected = this.capture(stack).withSelection(phaseIndex,this.activeUpgradePhase);
        selected.applyToStack(stack);
        stack.set(TinkersTranscendComponents.TTMULTIPHASE,selected);
    }
    public void selectUpgrade(ItemStack stack,int upgradePhaseIndex){
        if(upgradePhaseIndex<0||upgradePhaseIndex>=upgradePhases.size()) return;
        TTMultiphase selected = this.capture(stack).withSelection(this.activePhase,upgradePhaseIndex);
        selected.applyToStack(stack);
        stack.set(TinkersTranscendComponents.TTMULTIPHASE,selected);
    }
    public void cycleEnchantments(ItemStack stack){
        this.select(stack, (this.activePhase+1)%this.phases.size(), this.activeUpgradePhase);
    }
    public void cycleUpgrades(ItemStack stack){
        this.select(stack, this.activePhase, (this.activeUpgradePhase+1)%this.upgradePhases.size());
    }
    public void initialize(ItemStack stack){
        this.applyToStack(stack);
        stack.set(TinkersTranscendComponents.TTMULTIPHASE,this);
    }
    public boolean addPhase(ItemStack stack){
        TTMultiphase captured = this.capture(stack);
        if (captured.phases.size() >=4){
            return false;
        }else{
            List<Phase> expanded = new ArrayList<>(captured.phases);
            expanded.add(Phase.EMPTY);
            List<UpgradePhase> expandedUpgrade = new ArrayList<>(captured.upgradePhases);
            expandedUpgrade.add(UpgradePhase.EMPTY);
            TTMultiphase sel = new TTMultiphase(expanded,captured.activePhase,expandedUpgrade,captured.activeUpgradePhase)
                    .syncMultiphaseUpgrade(
                            ToolStack.from(stack).getUpgrades().getLevel(TinkersTranscendModifiers.MULTIPHASE_MODIFIER.getId()
                            ));
            stack.set(TinkersTranscendComponents.TTMULTIPHASE,sel);
            return true;
        }
    }
    public boolean removePhase(ItemStack stack){
        TTMultiphase captured = this.capture(stack);
        if (captured.phases.size() <=2){
            return false;
        }else{
            List<Phase> condensed = new ArrayList<>(captured.phases);
            condensed.removeLast();
            List<UpgradePhase> condensedUpgrade = new ArrayList<>(captured.upgradePhases);
            condensedUpgrade.removeLast();
            TTMultiphase sel = new TTMultiphase(condensed,captured.activePhase,condensedUpgrade,captured.activeUpgradePhase)
                    .syncMultiphaseUpgrade(
                            ToolStack.from(stack).getUpgrades().getLevel(TinkersTranscendModifiers.MULTIPHASE_MODIFIER.getId()
                            ))
                    .withSelection(0,0);
            sel.applyToStack(stack);
            stack.set(TinkersTranscendComponents.TTMULTIPHASE,sel);
            return true;
        }
    }
    public void applySelectionPreview(ItemStack stack,int phaseIndex,int upgraddPhaseIndex){
        if (phaseIndex>=0 && phaseIndex< this.phases.size()){
            this.withSelection(phaseIndex,upgraddPhaseIndex).applyToStack(stack);
        }
    }
    private TTMultiphase withSelection(int phaseIndex,int upgradePhaseIndex) {

        return new TTMultiphase(this.phases, phaseIndex, this.upgradePhases,upgradePhaseIndex);
    }
    private void applyToStack(ItemStack stack) {
        Merciless.disable(stack);
        (this.phases.get(this.activePhase)).applyToStack(stack);
        (this.upgradePhases.get(this.activeUpgradePhase)).applyToStack(stack);
        //stack.set(DataComponents.ITEM_NAME,stack.getItem().getDescription().copy().append(makeSuffix(this.activePhase,this.activeUpgradePhase)));

        stack.set(DataComponents.ITEM_NAME,Component.literal(MultiphaseModifier.getTinkersName(stack)).append(makeSuffix(this.activePhase,this.activeUpgradePhase)));
    }
    public TTMultiphase syncMultiphaseUpgrade(int level){
        List<UpgradePhase> list = new ArrayList<>();
        for(int i=0;i<this.upgradePhases.size();i++){
            //list.add(new UpgradePhase(this.upgradePhases.get(i).customName,this.upgradePhases.get(i).upgradesTag));
            list.add(this.upgradePhases.get(i).syncMultiphaseUpgrade(level));
        }
        return new TTMultiphase(this.phases, this.activePhase, list,this.activeUpgradePhase);
    }
    static {
        LEGACY_CODEC = Codec.PASSTHROUGH.fieldOf("id").xmap(
                (ignored) -> create(),
                (ignored) ->
                        new Dynamic(JsonOps.INSTANCE, (JsonElement)JsonOps.INSTANCE.emptyMap()));
        CODEC = Codec.mapEither(DATA_CODEC, LEGACY_CODEC).xmap(
                (either) ->
                        (TTMultiphase)either.map(
                                (multiphase) -> multiphase,
                                (multiphase) -> multiphase),
                Either::left);
        STREAM_CODEC = StreamCodec.composite(
                TTMultiphase.Phase.STREAM_CODEC.apply(ByteBufCodecs.list()),
                TTMultiphase::phases,
                ByteBufCodecs.VAR_INT,
                TTMultiphase::activePhase,
                TTMultiphase.UpgradePhase.STREAM_CODEC.apply(ByteBufCodecs.list()),
                TTMultiphase::upgradePhases,
                ByteBufCodecs.VAR_INT,
                TTMultiphase::activeUpgradePhase,
                TTMultiphase::new);
    }

    public record Phase(Optional<Component> customName, int repairCost, ItemEnchantments enchantments) {
        public static final Phase EMPTY;
        public static final MapCodec<Phase> CODEC;
        public static final StreamCodec<RegistryFriendlyByteBuf, Phase> STREAM_CODEC;

        public Phase(Optional<Component> customName, int repairCost, ItemEnchantments enchantments) {
            customName = customName.map(Component::copy);
            this.customName = customName;
            this.repairCost = repairCost;
            this.enchantments = enchantments;
        }
        public static Phase fromInput(ItemStack stack) {
            return capture(stack, EnchantmentUtil.merge((ItemEnchantments)stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY), (ItemEnchantments)stack.getOrDefault(ModComponents.MERCILESS_ENCHANTMENTS, ItemEnchantments.EMPTY)));
        }
        private static Phase capture(ItemStack stack, ItemEnchantments enchantments) {
            return new Phase(
                    Optional.ofNullable((Component)stack.get(DataComponents.CUSTOM_NAME)),
                    (Integer)stack.getOrDefault(DataComponents.REPAIR_COST, 0),
                    enchantments);
        }

        private void applyToStack(ItemStack stack) {
            if (this.customName.isPresent()) {
                stack.set(DataComponents.CUSTOM_NAME, ((Component)this.customName.get()).copy());
            } else {
                stack.remove(DataComponents.CUSTOM_NAME);
            }

            stack.set(DataComponents.REPAIR_COST, this.repairCost);
            stack.set(DataComponents.ENCHANTMENTS, this.enchantments);
        }
        static {
            EMPTY = new Phase(Optional.empty(), 0, ItemEnchantments.EMPTY);
            CODEC = RecordCodecBuilder.mapCodec((instance) ->
                    instance.group(
                            ComponentSerialization.FLAT_CODEC.
                                    optionalFieldOf("custom_name").
                                    forGetter(Phase::customName),
                            Codec.INT.fieldOf("repair_cost").
                                    forGetter(Phase::repairCost),
                            ItemEnchantments.CODEC.fieldOf("enchantments").
                                    forGetter(Phase::enchantments)
                    ).apply(instance, Phase::new));
            STREAM_CODEC = StreamCodec.composite(
                    ComponentSerialization.OPTIONAL_STREAM_CODEC,
                    Phase::customName,
                    ByteBufCodecs.VAR_INT,
                    Phase::repairCost,
                    ItemEnchantments.STREAM_CODEC,
                    Phase::enchantments,
                    Phase::new);
        }
    }
    public record UpgradePhase(Optional<Component> customName, CompoundTag upgradesTag){
        public static final UpgradePhase EMPTY;
        public static final MapCodec<UpgradePhase> CODEC;
        public static final StreamCodec<RegistryFriendlyByteBuf, UpgradePhase> STREAM_CODEC;
        public UpgradePhase(Optional<Component> customName, CompoundTag upgradesTag) {
            customName = customName.map(Component::copy);
            this.customName = customName;
            this.upgradesTag = upgradesTag;
        }
        public UpgradePhase syncMultiphaseUpgrade(int level){
            ModifierId id = TinkersTranscendModifiers.MULTIPHASE_MODIFIER.getId();
            CompoundTag tag = this.upgradesTag.copy();
            //TinkersTranscendium.LOGGER.info("Tag1:{}",tag);
            ModifierNBT nbt = ModifierNBT.readFromNBT(
                    tag.getList(
                            MultiphaseModifier.NBT_NAME,CompoundTag.TAG_COMPOUND));
            //TinkersTranscendium.LOGGER.info("Tag1:{}",tag);
            int thisLevel = nbt.getLevel(id);
            if (level>thisLevel){
                nbt = nbt.withModifier(id,level-thisLevel);
            }else if (thisLevel > level){
                nbt = nbt.withoutModifier(id,thisLevel-level);
            }
            tag.put(MultiphaseModifier.NBT_NAME,nbt.serializeToNBT());
            return new UpgradePhase(this.customName,tag);
        }
        public static UpgradePhase fromInput(ItemStack itemStack){
            return capture(itemStack,MultiphaseModifier.getUpgradeTag(itemStack));
        }
        public static UpgradePhase capture(ItemStack stack,CompoundTag upgradesTag){
            return new UpgradePhase(Optional.ofNullable((Component)stack.get(DataComponents.CUSTOM_NAME)),upgradesTag);
        }
        private void applyToStack(ItemStack stack) {
            if (this.customName.isPresent()) {
                stack.set(DataComponents.CUSTOM_NAME, ((Component)this.customName.get()).copy());
            } else {
                stack.remove(DataComponents.CUSTOM_NAME);
            }

            MultiphaseModifier.ApplyUpgradeTag(stack,this.upgradesTag);
        }
        static {
            EMPTY = new UpgradePhase(Optional.empty(), MultiphaseModifier.createEmptyTag());
            CODEC = RecordCodecBuilder.mapCodec((instance) ->
                    instance.group(
                            ComponentSerialization.FLAT_CODEC.
                                    optionalFieldOf("custom_name").
                                    forGetter(UpgradePhase::customName),
                            CompoundTag.CODEC.fieldOf("upgradesTag").forGetter(UpgradePhase::upgradesTag)
                    ).apply(instance, UpgradePhase::new));
            STREAM_CODEC = StreamCodec.composite(
                    ComponentSerialization.OPTIONAL_STREAM_CODEC,
                    UpgradePhase::customName,
                    ByteBufCodecs.COMPOUND_TAG,
                    UpgradePhase::upgradesTag,
                    UpgradePhase::new);
        }
    }
}
