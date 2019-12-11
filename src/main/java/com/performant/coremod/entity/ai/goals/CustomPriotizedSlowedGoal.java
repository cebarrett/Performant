package com.performant.coremod.entity.ai.goals;

import com.performant.coremod.entity.ai.CustomGoalSelector;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;

/**
 * Custom priotized goal, which updates at given rates.
 */
public class CustomPriotizedSlowedGoal extends PrioritizedGoal
{
    /**
     * The selector it belongs to
     */
    private final CustomGoalSelector selector;

    /**
     * Rate at which shouldexecute is checked
     */
    private final int shouldExecuteRate;
    private       int shouldExecuteCounter = 1;

    /**
     * Rate at which the ticks are done
     */
    private final int tickRate;
    private       int tickCounter = 1;

    /**
     * Custom priotized goal for different tickrates.
     *
     * @param priority          given priority
     * @param goal              goal to use
     * @param selector          goalselector ref
     * @param shouldExecuteRate should execute rate, in multiples of the SHOULD_EXECUTE_INTERVAL config.
     * @param tickRate          tick rate for ticks and shoulcontinueexecute
     */
    public CustomPriotizedSlowedGoal(final int priority, final Goal goal, final CustomGoalSelector selector, final int shouldExecuteRate, final int tickRate)
    {
        super(priority, goal);
        this.selector = selector;
        this.tickRate = Math.max(1, tickRate);
        this.shouldExecuteRate = Math.max(1, shouldExecuteRate);
    }

    @Override
    public boolean shouldExecute()
    {
        if (shouldExecuteCounter == shouldExecuteRate)
        {
            shouldExecuteCounter = 1;
            return super.shouldExecute();
        }
        shouldExecuteCounter++;
        return false;
    }

    /**
     * Whether we update shouldcontinueexecuting, use the same counter/rate as tickrate. So that before tick() is executed shouldContinueExecuting() is run.
     *
     * @return
     */
    @Override
    public boolean shouldContinueExecuting()
    {
        if (tickCounter == tickRate)
        {
            return super.shouldContinueExecuting();
        }
        return true;
    }

    @Override
    public void tick()
    {
        if (tickCounter == tickRate)
        {
            tickCounter = 1;
            super.tick();
        }
        else
        {
            tickCounter++;
        }
    }
}
