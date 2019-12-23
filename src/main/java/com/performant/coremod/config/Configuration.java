package com.performant.coremod.config;

import net.minecraftforge.common.config.Config;

import static com.performant.coremod.Constants.MOD_ID;

@Config(modid = MOD_ID)
public class Configuration
{
    @Config.Comment("All configurations related to AI")
    public static AI ai = new AI();

    @Config.Comment("All configurations related to non AI entity changes")
    public static Entity ex = new Entity();

    public static class AI
    {
        @Config.Comment("Interval in which non-running AI tasks are rechecked. Vanilla default is 3, this mods suggested default is 4")
        @Config.RangeInt(min = 1, max = 500)
        public int goalSelectorTickRate = 4;

        @Config.Comment("Whether to use a slower SwimmingAI, default: true")
        public boolean slowerSwimmingAI = true;

        @Config.Comment("Whether to use a slower TemptAI check, default: true")
        public boolean slowerTemptCheck = true;

        @Config.Comment("Whether to use a slower WanderAI check, default: true")
        public boolean slowerWander = true;
    }

    public static class Entity
    {
        @Config.Comment("Use fast collision and nearbyentity checks, default: true")
        public boolean fastCollisionAndEntityChecks = true;
    }
}
