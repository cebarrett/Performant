package com.performant.coremod.entity.ai;

import com.performant.coremod.Performant;
import com.performant.coremod.config.Configuration;
import com.performant.coremod.entity.ai.goals.CustomPriotizedSlowedGoal;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.profiler.Profiler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * A simplified goal selector, for more performance.
 */
public class CustomGoalSelector extends EntityAITasks
{
    /**
     * All goals added to this selector
     */
    public final List<CustomPriotizedSlowedGoal> goals = new ArrayList<>();

    /**
     * Profiler used for debug information /debug
     */
    private Profiler profiler;

    /**
     * Array of flags, true if currently disabled
     */
    private int disabledFlags = 0;

    private int runningFlags           = 0;
    private int runningNoOverRuleFlags = 0;

    public int trackingPlayers = 0;

    /**
     * Tick counter
     */
    int counter = 0;

    /**
     * Tick interval of how often non-running goals are checked
     */
    public static final int SHOULD_EXECUTE_INTERVAL = Configuration.ai.goalSelectorTickRate - 1;

    /**
     * Create a new goalselector from an existing one, simply re-uses the references.
     *
     * @param old the old selector to use
     */
    public CustomGoalSelector(final EntityAITasks old)
    {
        super(old.profiler);
        importFrom(old);
        super.taskEntries = old.taskEntries;
        super.profiler = old.profiler;

        counter = new Random().nextInt(SHOULD_EXECUTE_INTERVAL + 1);
    }

    /**
     * Imports values from another selector
     *
     * @param selector selector to import from
     */
    public void importFrom(final EntityAITasks selector)
    {
        if (selector == null)
        {
            return;
        }

        for (EntityAITaskEntry task : selector.taskEntries)
        {
            CustomPriotizedSlowedGoal goal = Performant.goalData.getPriotizedGoalFor(task.priority, task.action, this);
            goal.isrunning = task.using;
            if (task.using && !task.action.isInterruptible())
            {
                runningNoOverRuleFlags |= task.action.getMutexBits();
            }
            goals.add(goal);
        }
        goals.sort(Comparator.comparingInt(CustomPriotizedSlowedGoal::getPriority));

        // Set profiler reference
        profiler = selector.profiler;

        // Set which flags are disabled
        disabledFlags = selector.disabledControlFlags;
    }

    /**
     * Add a now AITask. Args : priority, task
     */
    @Override
    public void addTask(int priority, EntityAIBase task)
    {
        goals.add(Performant.goalData.getPriotizedGoalFor(priority, task, this));
        goals.sort(Comparator.comparingInt(CustomPriotizedSlowedGoal::getPriority));
    }

    /**
     * removes the indicated task from the entity's AI tasks.
     */
    @Override
    public void removeTask(EntityAIBase task)
    {
        this.goals.stream().filter((goal) -> {
            return goal.goal == task;
        }).filter(CustomPriotizedSlowedGoal::isRunning).forEach(this::resetGoal);
        this.goals.removeIf((goal) -> {
            return goal.goal == task;
        });
    }

    /**
     * Whether the goals flag are within the disabled flags.
     *
     * @param goal
     * @return
     */
    private boolean goalContainsDisabledFlag(final CustomPriotizedSlowedGoal goal)
    {
        return (disabledFlags & goal.goal.getMutexBits()) != 0;
    }

    /**
     * Returns whether the given goal is higher priority in all flags it uses than existing running goals.
     *
     * @param goal1 goal to check
     * @return true if it overrules the existing goal.
     */
    private boolean isPreemptedByAll(final CustomPriotizedSlowedGoal goal1)
    {
        return (runningFlags & goal1.goal.getMutexBits()) == 0 && (runningNoOverRuleFlags & goal1.goal.getMutexBits()) == 0;
    }

    /**
     * Resets the current goal.
     *
     * @param goal
     */
    private void resetGoal(final CustomPriotizedSlowedGoal goal)
    {
        if (goal.isrunning && !goal.goal.isInterruptible())
        {
            runningNoOverRuleFlags &= ~goal.goal.getMutexBits();
        }
        goal.goal.resetTask();
        goal.isrunning = false;
    }

    /**
     * Ticks this selector, first checks running goals to stop, then checks all goals to see which should start running. Finally ticks all running goals Performance wise this is
     * about 6 times faster, when checking at the same rate as the vanilla one, resulting in about 3-4 times less time spent updating and executing AI goals. When updating
     * non-running goals only every 4 ticks it goes up to about 10% of vanilla's time spent for the whole update goals and their execution.
     */
    @Override
    public void onUpdateTasks()
    {
        this.profiler.startSection("goalUpdate");
        counter++;
        runningFlags = 0;

        for (final CustomPriotizedSlowedGoal currentGoal : goals)
        {
            if (currentGoal.isRunning() && (goalContainsDisabledFlag(currentGoal) || (currentGoal.goal.isInterruptible() && (runningFlags & currentGoal.goal.getMutexBits()) != 0)
                                              || !currentGoal.shouldContinueExecuting()))
            {
                resetGoal(currentGoal);
            }

            // Vanilla behaviour changed to checking it each tick with 1.14
            if (counter == 1 && !currentGoal.isRunning() && !goalContainsDisabledFlag(currentGoal) && isPreemptedByAll(currentGoal) && currentGoal.shouldExecute())
            {
                if (!currentGoal.goal.isInterruptible())
                {
                    runningNoOverRuleFlags |= currentGoal.goal.getMutexBits();
                }
                currentGoal.goal.startExecuting();
                currentGoal.isrunning = true;
            }

            if (currentGoal.isRunning())
            {
                runningFlags |= currentGoal.goal.getMutexBits();
                currentGoal.updateTask();
            }
        }

        if (counter > SHOULD_EXECUTE_INTERVAL)
        {
            counter = 0;
        }
        this.profiler.endSection();
    }

    public boolean isControlFlagDisabled(int flag)
    {
        return (this.disabledControlFlags & flag) > 0;
    }

    public void disableControlFlag(int flag)
    {
        this.disabledControlFlags |= flag;
    }

    public void enableControlFlag(int flag)
    {
        this.disabledControlFlags &= ~flag;
    }

    public void setControlFlag(int flag, boolean enable)
    {
        if (enable)
        {
            this.enableControlFlag(flag);
        }
        else
        {
            this.disableControlFlag(flag);
        }
    }
}