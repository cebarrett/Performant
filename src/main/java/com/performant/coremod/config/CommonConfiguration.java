package com.performant.coremod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfiguration extends AbstractConfiguration
{
    public final ForgeConfigSpec.IntValue     goalSelectorTickRate;
    public final ForgeConfigSpec.BooleanValue optimizeTempt;

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
        optimizeTempt = defineBoolean(builder, "goalselectorrate", true);
        finishCategory(builder);
    }
}
