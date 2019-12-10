package com.performant.coremod.entity.ai.goals;

import com.performant.coremod.entity.ai.CustomGoalSelector;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;

/**
 * Custom Temp goal, which runs at a lower rate.
 */
public class CustomPriotizedTemptGoal extends PrioritizedGoal
{
    /**
     * The selector it belongs to
     */
    private final CustomGoalSelector selector;

    /**
     * the tick counter
     */
    private int counter = 0;

    public CustomPriotizedTemptGoal(final int p_i50318_1_, final Goal p_i50318_2_, final CustomGoalSelector selector)
    {
        super(p_i50318_1_, p_i50318_2_);
        this.selector = selector;
    }

    @Override
    public boolean shouldExecute()
    {
        counter++;
        if (counter == 5)
        {
            counter = 0;
            return super.shouldExecute();
        }
        return false;
    }
}
