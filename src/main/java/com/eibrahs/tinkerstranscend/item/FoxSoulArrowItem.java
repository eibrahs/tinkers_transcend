package com.eibrahs.tinkerstranscend.item;

import com.eibrahs.tinkerstranscend.entity.TinkersTranscendEntities;
import com.eibrahs.tinkerstranscend.entity.projectile.FoxSoulArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FoxSoulArrowItem extends ArrowItem {
    public FoxSoulArrowItem(Properties properties) {
        super(properties);
    }
    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter,  ItemStack weapon) {
        return new FoxSoulArrowEntity(TinkersTranscendEntities.FOX_SOUL_ARROW.get(), shooter, level, stack.copyWithCount(1), weapon);
    }
}
