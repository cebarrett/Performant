package com.performant.coremod.entity.ai.goals;

import com.performant.coremod.entity.ai.CustomGoalSelector;
import net.minecraft.entity.ai.EntityAIBase;
/**
 * Custom priotized goal, which updates at given rates.
 */
public class CustomPriotizedSlowedGoal
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

    public final EntityAIBase goal;
    public final int          priority;
    public       boolean      isrunning = false;

    /**
     * Custom priotized goal for different tickrates.
     *
     * @param priority          given priority
     * @param goal              goal to use
     * @param selector          goalselector ref
     * @param shouldExecuteRate should execute rate, in multiples of the SHOULD_EXECUTE_INTERVAL config.
     * @param tickRate          tick rate for ticks and shoulcontinueexecute
     */
    public CustomPriotizedSlowedGoal(final int priority, final EntityAIBase goal, final CustomGoalSelector selector, final int shouldExecuteRate, final int tickRate)
    {
        this.priority = priority;
        this.goal = goal;
        this.selector = selector;
        this.tickRate = Math.max(1, tickRate);
        this.shouldExecuteRate = Math.max(1, shouldExecuteRate);
    }

    public int getPriority()
    {
        return priority;
    }

    public boolean shouldExecute()
    {
        if (shouldExecuteCounter == shouldExecuteRate)
        {
            shouldExecuteCounter = 1;
            return goal.shouldExecute();
        }
        shouldExecuteCounter++;
        return false;
    }

    /**
     * Whether we update shouldcontinueexecuting, use the same counter/rate as tickrate. So that before tick() is executed shouldContinueExecuting() is run.
     *
     * @return
     */
    public boolean shouldContinueExecuting()
    {
        if (tickCounter == tickRate)
        {
            return goal.shouldContinueExecuting();
        }
        return true;
    }

    public void updateTask()
    {
        if (tickCounter == tickRate)
        {
            tickCounter = 1;
            goal.updateTask();
        }
        else
        {
            tickCounter++;
        }
    }

    public boolean isRunning()
    {
        return isrunning;
    }
}
