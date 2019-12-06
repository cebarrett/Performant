package com.performant.coremod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfiguration extends AbstractConfiguration
{
    public final ForgeConfigSpec.IntValue goalSelectorTickRate;

    protected CommonConfiguration(final ForgeConfigSpec.Builder builder)
    {
        createCategory(builder, "Entity");
        goalSelectorTickRate = defineInteger(builder, "goalselectorrate", 4);
        finishCategory(builder);
    }
}
