package com.performant.coremod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfiguration extends AbstractConfiguration
{
    public final ForgeConfigSpec.IntValue     goalSelectorTickRate;
    public final ForgeConfigSpec.BooleanValue optimizeTempt;
    public final ForgeConfigSpec.BooleanValue optimizeAvoid;
    public final ForgeConfigSpec.BooleanValue optimizeHurtByTarget;
    public final ForgeConfigSpec.BooleanValue optimizePanic;
    public final ForgeConfigSpec.BooleanValue optimizeBreed;
    public final ForgeConfigSpec.BooleanValue slowFleeSun;

    protected CommonConfiguration(final ForgeConfigSpec.Builder builder)
    {
        createCategory(builder, "Entity");
        builder.comment(
          "Sets the tick, ticks happen 20 times each sec, interval in which non-running AI tasks are rechecked. Vanilla default is 1, this mods suggested default is 4")
          .push("Entity");
        goalSelectorTickRate = defineInteger(builder, "goalselectorrate", 4, 1, 500);

        builder.comment(
          "Whether to use a slower updated AI tempt goal, tempt is used e.g. for luring sheep with wheat. default = true")
          .push("Entity");
        optimizeTempt = defineBoolean(builder, "optimizeTempt", true);

        builder.comment(
          "Whether to use a slower updated AI avoid entity goal, avoid is used e.g. for villagers avoiding zombies so it constantly searches for mobs in the area. default = true")
          .push("Entity");
        optimizeAvoid = defineBoolean(builder, "optimizeAvoid", true);

        builder.comment(
          "Whether to use a modified/fixed HurtByTarget goal which is used to call other entities for help. default = true")
          .push("Entity");
        optimizeHurtByTarget = defineBoolean(builder, "optimizeHurtByTarget", true);

        builder.comment(
          "Whether to use a slower updated AI Panic entity goal, panic is used for chickens/other animals running away e.g. on fire. default = true")
          .push("Entity");
        optimizePanic = defineBoolean(builder, "optimizePanic", true);

        builder.comment(
          "Whether to use a slower updated AI Breed entity goal, breed is used for searching nearby similar animals and doesnt have to be checked that often. default = true")
          .push("Entity");
        optimizeBreed = defineBoolean(builder, "optimizeBreed", true);

        builder.comment(
          "Whether to use a slower updated AI Fox fleesun goal, which causes too much load. default = true")
          .push("Entity");
        slowFleeSun = defineBoolean(builder, "slowFleeSun", true);
        finishCategory(builder);
    }
}
