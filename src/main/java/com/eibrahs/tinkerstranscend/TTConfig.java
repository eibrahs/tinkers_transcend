package com.eibrahs.tinkerstranscend;


import net.neoforged.neoforge.common.ModConfigSpec;

public class TTConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MODIFIER_MATURE_TICKTIME = BUILDER
            .comment("老成 - 每过多少Tick增加1点强化值")
            .defineInRange("modifier.mature_ticktime", 100, 1, Integer.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue MODIFIER_MATURE_MAXEACHLEVEL = BUILDER
            .comment("老成 - 每1级强化等级最多提供多少点强化值")
            .defineInRange("modifier.mature_maxEachLevel",50,0,Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue MODIFIER_MATURE_ATTACK_DAMAGE_VAL = BUILDER
            .comment("老成 - 每点强化值为近战武器提供多少攻击伤害")
            .defineInRange("modifier.mature_attack_damage_val",0.01,0,Double.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue MODIFIER_MATURE_ATTACK_DAMAGE_RATE = BUILDER
            .comment("老成 - 攻击伤害加成是数值(false)还是倍率(true)")
            .define("modifier.mature_attack_damage_rate",true);
    public static final ModConfigSpec.DoubleValue MODIFIER_MATURE_ATTACK_SPEED_VAL = BUILDER
            .comment("老成 - 每点强化值为近战武器提供多少攻击速度")
            .defineInRange("modifier.mature_attack_damage_val",0.01,0,Double.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue MODIFIER_MATURE_ATTACK_SPEED_RATE = BUILDER
            .comment("老成 - 攻击速度加成是数值(false)还是倍率(true)")
            .define("modifier.mature_attack_damage_rate",true);
    public static final ModConfigSpec.DoubleValue MODIFIER_MATURE_ARMOR_VAL = BUILDER
            .comment("老成 - 每点强化值为盔甲提供多少护甲值")
            .defineInRange("modifier.mature_aromor_val",0.01,0,Double.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue MODIFIER_MATURE_ARMOR_RATE = BUILDER
            .comment("老成 - 护甲值加成是数值(false)还是倍率(true)")
            .define("modifier.mature_aromor_rate",true);
    public static final ModConfigSpec.DoubleValue MODIFIER_MATURE_ARMOR_TOUGHNESS_VAL = BUILDER
            .comment("老成 - 每点强化值为盔甲提供多少护甲韧性")
            .defineInRange("modifier.mature_armor_toughness_val",0.02,0,Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue MODIFIER_MATURE_UNBREAKING_VAL = BUILDER
            .comment("老成 - 每点强化值提供多少耐久消耗减免")
            .defineInRange("modifier.mature_unbreaking_val",0.01,0,Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue MODIFIER_MATURE_UNBREAKING_MAX = BUILDER
            .comment("老成 - 最大耐久消耗减免")
            .defineInRange("modifier.mature_unbreaking_max",0.8,0,Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue MODIFIER_MATURE_MINING_SPEED_VAL = BUILDER
            .comment("老成 - 每点强化值为工具提供多少挖掘速度")
            .defineInRange("modifier.mature_mining_speed_val",0.01,0,Double.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue MODIFIER_MATURE_MINING_SPEED_RATE = BUILDER
            .comment("老成 - 挖掘速度加成是数值(false)还是倍率(true)")
            .define("modifier.mature_mining_speed_rate",true);
    static final ModConfigSpec SPEC = BUILDER.build();

}
