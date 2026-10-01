package com.eibrahs.tinkerstranscend.tinker.modifiers;

import com.eibrahs.tinkerstranscend.TinkersTranscendComponents;
import dev.dubhe.anvilcraft.client.init.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;

public class MultiphaseModifierUI extends Screen {
    private final Player player;
    private final List<PhaseItemRenderer> phasesRenderer = new ArrayList<>();
    private boolean shouldClose = false;

    protected MultiphaseModifierUI(Player player){
        super(Component.literal("Multiphase"));
        this.player = player;
        int ww = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int wh = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        phasesRenderer.add(new PhaseItemRenderer(ww/3*2,wh/3*2,EquipmentSlot.MAINHAND));
        phasesRenderer.add(new PhaseItemRenderer(ww/3,wh/3*2,EquipmentSlot.OFFHAND));
        phasesRenderer.add(new PhaseItemRenderer(ww/5*4,wh/3,EquipmentSlot.FEET));
        phasesRenderer.add(new PhaseItemRenderer(ww/5*3,wh/3,EquipmentSlot.LEGS));
        phasesRenderer.add(new PhaseItemRenderer(ww/5*2,wh/3,EquipmentSlot.CHEST));
        phasesRenderer.add(new PhaseItemRenderer(ww/5,wh/3,EquipmentSlot.HEAD));

    }
    @Override
    protected void init() {
        super.init();
    }

    public record Vec2(int x,int y){}
    public record Border(int l,int t,int r,int b){};
    protected class PhaseItemRenderer{
        private int x;
        private int y;
        private final EquipmentSlot equipmentSlot;
        private int rx = 2;
        private int ry = 6;
        private int maxRx = 24;
        private int maxRy = 48;
        private final int slotSize = 16;
        private int PHASE_COUNT=2;
        public int selectSlot1 = 0;
        public int selectSlot2 = 0;
        private final List<ItemStack> phaseSlots = new ArrayList<>();

        public PhaseItemRenderer( int x, int y,EquipmentSlot equipmentSlot){
            this.x = x;
            this.y = y;
            this.equipmentSlot = equipmentSlot;
            capturePhase();
       }
       private void capturePhase(){
           ItemStack item = Minecraft.getInstance().player.getItemBySlot(equipmentSlot).copy();
           if (!MultiphaseModifier.isInitPhase(item)){
               return;
           }
           PHASE_COUNT = item.get(TinkersTranscendComponents.TTMULTIPHASE).size();
           selectSlot1 = item.get(TinkersTranscendComponents.TTMULTIPHASE).activePhase();
           selectSlot2 = item.get(TinkersTranscendComponents.TTMULTIPHASE).activeUpgradePhase();

           phaseSlots.clear();
           for (int i = 0; i < PHASE_COUNT*2; i++) {
               ItemStack disPlayItem = item.copy();
               ToolStack.copyFrom(item).updateStack(disPlayItem);
               TTMultiphase disPlay = disPlayItem.get(TinkersTranscendComponents.TTMULTIPHASE).forDisplay(disPlayItem);
               disPlayItem.set(TinkersTranscendComponents.TTMULTIPHASE,disPlay);
               if (i<PHASE_COUNT) {
                   disPlay.selectEnchantment(disPlayItem, i);
               }else{
                   disPlay.selectUpgrade(disPlayItem,i-PHASE_COUNT);
               }
               phaseSlots.add(disPlayItem);
           }
       }

        public void render(GuiGraphics guiGraphics,int mouseX,int mouseY){
            if (rx<maxRx) rx++;
            if (ry<maxRy) ry++;
            ItemStack item = Minecraft.getInstance().player.getItemBySlot(equipmentSlot).copy();
            if (!MultiphaseModifier.isInitPhase(item)){
                renderItem(guiGraphics,item,-1);
                return;
            }
            for (int i = 0; i < PHASE_COUNT*2; i++) {
                renderItem(guiGraphics,phaseSlots.get(i),i);
            }

            guiGraphics.drawCenteredString(Minecraft.getInstance().font,
                    TTMultiphase.getEnchantmentPhaseName(selectSlot1).getString(),
                    x-8, y, 0x888888
            );
            guiGraphics.drawCenteredString(Minecraft.getInstance().font,
                    TTMultiphase.getUpgradePhaseName(selectSlot2).getString(),
                    x+8, y, 0x888888
            );
            int mouseSlot = mouseOnPhaseSlot(mouseX,mouseY);
            if (mouseSlot!=-1){
                guiGraphics.renderTooltip(
                        Minecraft.getInstance().font,
                        phaseSlots.get(mouseSlot),
                        mouseX, mouseY
                );
            }
        }
        public Vec2 getPhaseSlotCenter(int index){
            if (index<0){return new Vec2(x,y);}
            double tgap = Math.PI/6;
            double t = - index * tgap + (PHASE_COUNT-1)*tgap/2+Math.PI;
            if (index >= PHASE_COUNT){
                t = (index - PHASE_COUNT) * tgap - (PHASE_COUNT-1)*tgap/2;
            }
            return new Vec2(
                    x+(int)Math.round(rx*Math.cos(t)),
                    y+(int)Math.round(ry*Math.sin(t)));
        }
        public Border getPhaseSlotBorder(int index){
            Vec2 pos = getPhaseSlotCenter(index);
            return new Border(pos.x-slotSize/2,pos.y-slotSize/2,pos.x+slotSize/2,pos.y+slotSize/2);
        }
        private void renderItem(GuiGraphics guiGraphics, ItemStack stack,int phaseSlot) {
            Border border = getPhaseSlotBorder(phaseSlot);
            guiGraphics.fill(border.l-2, border.t-2, border.r+2, border.b+2, 0xFF333333);
            guiGraphics.renderItem(stack, border.l+(slotSize-16)/2,border.t+(slotSize-16)/2);

            if (phaseSlot == selectSlot1) {
                guiGraphics.fill(border.l - 1, border.t - 1, border.r + 1, border.b + 1, 0x803333FF);
            }else if (phaseSlot == selectSlot2 + PHASE_COUNT) {
                guiGraphics.fill(border.l - 1, border.t - 1, border.r + 1, border.b + 1, 0x8033FF33);
            }
        }
        private int mouseOnPhaseSlot(double mouseX,double mouseY){
            for(int i=0;i<PHASE_COUNT*2;i++){
                Border border = getPhaseSlotBorder(i);
                if (mouseX>border.l&&mouseX<border.r&&mouseY>border.t&&mouseY<border.b){
                    return i;
                }
            }
            return -1;
        }
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(Minecraft.getInstance().font,
                "Select Item:",
                20, 20, 0xFFFFFF, true);
        for (PhaseItemRenderer phaseRenderer : phasesRenderer) {
            phaseRenderer.render(guiGraphics,mouseX,mouseY);
        }
        guiGraphics.drawString(Minecraft.getInstance().font,
                "Click to select, or release key to cancel",
                20, height - 30, 0x888888, false);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 ) {
            for(int i=0;i<phasesRenderer.size();++i){
                PhaseItemRenderer renderer = phasesRenderer.get(i);
                int phaseIndex = renderer.mouseOnPhaseSlot(mouseX,mouseY);
                if (phaseIndex==-1) continue;
                ItemStack item = Minecraft.getInstance().player.getItemBySlot(MultiphaseModifierHandler.getEquipmentSlotByInt(i));
                TTMultiphase comp = item.get(TinkersTranscendComponents.TTMULTIPHASE);
                if (phaseIndex<renderer.PHASE_COUNT){
                    comp.selectEnchantment(item,phaseIndex);
                    PacketDistributor.sendToServer(new MultiphaseModifierHandler.SelectPhasePacket(item.getComponents().hashCode(),i,phaseIndex,false));
                    //TinkersTranscendium.LOGGER.info("select send enchantments,slot:{}",i);
                }else{
                    comp.selectUpgrade(item,phaseIndex-renderer.PHASE_COUNT);

                    PacketDistributor.sendToServer(new MultiphaseModifierHandler.SelectPhasePacket(item.getComponents().hashCode(),i,phaseIndex-renderer.PHASE_COUNT,true));
                    //TinkersTranscendium.LOGGER.info("select send upgrade,slot:{}",i);
                }
                renderer.capturePhase();
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == ModKeyMappings.SWITCH_PHASE.get().getKey().getValue()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == ModKeyMappings.SWITCH_PHASE.get().getKey().getValue()) {
            closeUI();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
    private void closeUI() {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
