package com.performant.coremod.entity.ai;

import com.performant.coremod.config.Configuration;
import com.performant.coremod.entity.ai.goals.CustomPriotizedSlowedGoal;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Class for custom data for goals, which replace/alter vanilla ones
 */
public class CustomGoalTypeData
{
    public class CustomGoalTypeParameter
    {
        public final Function<EntityAIBase, EntityAIBase> goalConverter;
        public final int                                  shouldExecuteRate;
        public final int                                  tickRate;
        /**
         * Whether this custom params should be applied.
         */
        public final boolean                              isEnabled;

        private CustomGoalTypeParameter(final Function<EntityAIBase, EntityAIBase> goalConverter, final int shouldExecuteRate, final int tickRate, final boolean isEnabled)
        {
            this.goalConverter = goalConverter;
            this.shouldExecuteRate = shouldExecuteRate;
            this.tickRate = tickRate;
            this.isEnabled = isEnabled;
        }
    }

    /**
     * Contains all parameters needed for goals
     */
    private final HashMap<Class<? extends EntityAIBase>, CustomGoalTypeParameter> GOAL_TYPES = new HashMap<>();

    /**
     * Adds all custom goal entries.
     */
    public CustomGoalTypeData()
    {
        GOAL_TYPES.put(EntityAISwimming.class, new CustomGoalTypeParameter(g -> g, 5, 1, Configuration.ai.slowerSwimmingAI));
        GOAL_TYPES.put(EntityAITempt.class, new CustomGoalTypeParameter(g -> g, 4, 10, Configuration.ai.slowerTemptCheck));
        GOAL_TYPES.put(EntityAIWander.class, new CustomGoalTypeParameter(g -> g, 4, 1, Configuration.ai.slowerWander));
    }

    /**
     * Generates the right priotized goal for the given goal and priority.
     *
     * @param priority priority to use
     * @param goal     goal to use
     * @param selector selector to use
     * @return new PrioritizedGoal to use
     */
    public CustomPriotizedSlowedGoal getPriotizedGoalFor(final int priority, final EntityAIBase goal, final CustomGoalSelector selector)
    {
        final CustomGoalTypeParameter params = GOAL_TYPES.get(goal.getClass());
        if (params != null && params.isEnabled)
        {
            return new CustomPriotizedSlowedGoal(priority, params.goalConverter.apply(goal), selector, params.shouldExecuteRate, params.tickRate);
        }
        else
        {
            return new CustomPriotizedSlowedGoal(priority, goal, selector, 1, 1);
        }
    }
}
