package com.performant.coremod.entity.ai;

import com.google.common.collect.Sets;
import com.performant.coremod.Performant;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.profiler.Profiler;

import java.util.Iterator;
import java.util.Set;

/**
 * TestClass with vanilla logic
 */
public class VanillaGoalSelectorMeasurement extends EntityAITasks
{
    public VanillaGoalSelectorMeasurement(EntityAITasks old)
    {
        super(old.profiler);
        this.profiler = old.profiler;
        super.taskEntries = old.taskEntries;
        this.taskEntries = old.taskEntries;
        for (EntityAITasks.EntityAITaskEntry en : old.taskEntries)
        {
            en.using = false;
        }


        super.disabledControlFlags = old.disabledControlFlags;
        this.disabledControlFlags = old.disabledControlFlags;
    }

    /**
     * A list of EntityAITaskEntrys in EntityAITasks.
     */
    public        Set<EntityAITaskEntry>               taskEntries          = Sets.<EntityAITasks.EntityAITaskEntry>newLinkedHashSet();
    /**
     * A list of EntityAITaskEntrys that are currently being executed.
     */
    private final Set<EntityAITasks.EntityAITaskEntry> executingTaskEntries = Sets.<EntityAITasks.EntityAITaskEntry>newLinkedHashSet();
    /**
     * Instance of Profiler.
     */
    public        Profiler                             profiler;
    private       int                                  tickCount;
    private       int                                  tickRate             = 3;
    public        int                                  disabledControlFlags;

    /**
     * Add a now AITask. Args : priority, task
     */
    public void addTask(int priority, EntityAIBase task)
    {
        this.taskEntries.add(new EntityAITasks.EntityAITaskEntry(priority, task));
    }

    /**
     * removes the indicated task from the entity's AI tasks.
     */
    public void removeTask(EntityAIBase task)
    {
        Iterator<EntityAITaskEntry> iterator = this.taskEntries.iterator();

        while (iterator.hasNext())
        {
            EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
            EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;

            if (entityaibase == task)
            {
                if (entityaitasks$entityaitaskentry.using)
                {
                    entityaitasks$entityaitaskentry.using = false;
                    entityaitasks$entityaitaskentry.action.resetTask();
                    this.executingTaskEntries.remove(entityaitasks$entityaitaskentry);
                }

                iterator.remove();
                return;
            }
        }
    }

    static long totaltime = 0;
    static long runs      = 0;
    static long swimming  = 0;

    public void onUpdateTasks()
    {
        long d = System.nanoTime();
        this.profiler.startSection("goalSetup");

        if (this.tickCount++ % this.tickRate == 0)
        {
            for (EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry : this.taskEntries)
            {
                if (entityaitasks$entityaitaskentry.using)
                {
                    if (!this.canUse(entityaitasks$entityaitaskentry) || !this.canContinue(entityaitasks$entityaitaskentry))
                    {
                        entityaitasks$entityaitaskentry.using = false;
                        entityaitasks$entityaitaskentry.action.resetTask();
                        this.executingTaskEntries.remove(entityaitasks$entityaitaskentry);
                    }
                }
                else if (this.canUse(entityaitasks$entityaitaskentry))
                {
                    if (entityaitasks$entityaitaskentry.action.shouldExecute())
                    {
                        entityaitasks$entityaitaskentry.using = true;
                        entityaitasks$entityaitaskentry.action.startExecuting();
                        this.executingTaskEntries.add(entityaitasks$entityaitaskentry);
                    }
                }
            }
        }
        else
        {
            Iterator<EntityAITasks.EntityAITaskEntry> iterator = this.executingTaskEntries.iterator();

            while (iterator.hasNext())
            {
                EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry1 = iterator.next();

                if (!this.canContinue(entityaitasks$entityaitaskentry1))
                {
                    entityaitasks$entityaitaskentry1.using = false;
                    entityaitasks$entityaitaskentry1.action.resetTask();
                    iterator.remove();
                }
            }
        }

        this.profiler.endSection();

        if (!this.executingTaskEntries.isEmpty())
        {
            this.profiler.startSection("goalTick");

            for (EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry2 : this.executingTaskEntries)
            {
                entityaitasks$entityaitaskentry2.action.updateTask();
            }

            this.profiler.endSection();
        }
        d = System.nanoTime() - d;
        totaltime += d;
        runs++;
        if (runs >= 100000)
        {
            Performant.LOGGER.warn("Avg vanilla custom tick: " + totaltime / runs + " swimming attempts:" + swimming);
            totaltime = 0;
            runs = 0;
            swimming = 0;
        }

    }

    /**
     * Determine if a specific AI Task should continue being executed.
     */
    private boolean canContinue(EntityAITasks.EntityAITaskEntry taskEntry)
    {
        return taskEntry.action.shouldContinueExecuting();
    }

    /**
     * Determine if a specific AI Task can be executed, which means that all running higher (= lower int value) priority tasks are compatible with it or all lower priority tasks
     * can be interrupted.
     */
    static long canUseTime  = 0;
    static long canUseCount = 0;

    private boolean canUse(EntityAITaskEntry taskEntry, int a)
    {
        long d = System.nanoTime();
        boolean res = canUse(taskEntry, 1);
        d = System.nanoTime() - d;
        canUseTime += d;
        canUseCount++;
        if (canUseCount >= 100000)
        {
            Performant.LOGGER.warn("VANILLA:CANUSEAVG:" + canUseTime);
            canUseCount = 0;
            canUseTime = 0;
        }
        return res;
    }

    private boolean canUse(EntityAITasks.EntityAITaskEntry taskEntry)
    {
        if (this.executingTaskEntries.isEmpty())
        {
            return true;
        }
        else if (this.isControlFlagDisabled(taskEntry.action.getMutexBits()))
        {
            return false;
        }
        else
        {
            for (EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry : this.executingTaskEntries)
            {
                if (entityaitasks$entityaitaskentry != taskEntry)
                {
                    if (taskEntry.priority >= entityaitasks$entityaitaskentry.priority)
                    {
                        if (!this.areTasksCompatible(taskEntry, entityaitasks$entityaitaskentry))
                        {
                            return false;
                        }
                    }
                    else if (!entityaitasks$entityaitaskentry.action.isInterruptible())
                    {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    /**
     * Returns whether two EntityAITaskEntries can be executed concurrently
     */
    private boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry taskEntry1, EntityAITasks.EntityAITaskEntry taskEntry2)
    {
        return (taskEntry1.action.getMutexBits() & taskEntry2.action.getMutexBits()) == 0;
    }

    public boolean isControlFlagDisabled(int p_188528_1_)
    {
        return (this.disabledControlFlags & p_188528_1_) > 0;
    }

    public void disableControlFlag(int p_188526_1_)
    {
        this.disabledControlFlags |= p_188526_1_;
    }

    public void enableControlFlag(int p_188525_1_)
    {
        this.disabledControlFlags &= ~p_188525_1_;
    }

    public void setControlFlag(int p_188527_1_, boolean p_188527_2_)
    {
        if (p_188527_2_)
        {
            this.enableControlFlag(p_188527_1_);
        }
        else
        {
            this.disableControlFlag(p_188527_1_);
        }
    }
}