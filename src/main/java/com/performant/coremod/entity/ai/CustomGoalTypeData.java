package com.performant.coremod.entity.ai;

import com.performant.coremod.Performant;
import com.performant.coremod.entity.ai.goals.CustomPriotizedSlowedGoal;
import com.performant.coremod.entity.ai.goals.HurtGoals.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Class for custom data for goals, which replace/alter vanilla ones
 */
public class CustomGoalTypeData
{
    public class CustomGoalTypeParameter
    {
        public final Function<Goal, Goal> goalConverter;
        public final int                  shouldExecuteRate;
        public final int                  tickRate;
        /**
         * Whether this custom params should be applied.
         */
        public final boolean              isEnabled;

        private CustomGoalTypeParameter(final Function<Goal, Goal> goalConverter, final int shouldExecuteRate, final int tickRate, final boolean isEnabled)
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
    private final HashMap<Class<? extends Goal>, CustomGoalTypeParameter> GOAL_TYPES = new HashMap<>();

    /**
     * Adds all custom goal entries.
     */
    public CustomGoalTypeData()
    {
        GOAL_TYPES.put(TemptGoal.class, new CustomGoalTypeParameter(g -> g, 5, 1, Performant.getConfig().getCommon().optimizeTempt.get()));
        GOAL_TYPES.put(AvoidEntityGoal.class, new CustomGoalTypeParameter(g -> g, 4, 10, Performant.getConfig().getCommon().optimizeAvoid.get()));
        GOAL_TYPES.put(HurtByTargetGoal.class,
          new CustomGoalTypeParameter(g -> new CustomHurtByTargetGoal((HurtByTargetGoal) g), 1, 2, Performant.getConfig().getCommon().optimizeHurtByTarget.get()));
        GOAL_TYPES.put(PanicGoal.class, new CustomGoalTypeParameter(g -> g, 3, 4, Performant.getConfig().getCommon().optimizePanic.get()));
        GOAL_TYPES.put(BreedGoal.class, new CustomGoalTypeParameter(g -> g, 10, 1, Performant.getConfig().getCommon().optimizeBreed.get()));
        GOAL_TYPES.put(LlamaEntity.HurtByTargetGoal.class,
          new CustomGoalTypeParameter(g -> new CustomHurtByTargetGoalLLama((LlamaEntity.HurtByTargetGoal) g), 1, 2, Performant.getConfig().getCommon().optimizeHurtByTarget.get()));
        GOAL_TYPES.put(PandaEntity.RevengeGoal.class,
          new CustomGoalTypeParameter(g -> new CustomHurtByTargetGoalPanda((PandaEntity.RevengeGoal) g), 1, 2, Performant.getConfig().getCommon().optimizeHurtByTarget.get()));
        GOAL_TYPES.put(ZombiePigmanEntity.HurtByAggressorGoal.class,
          new CustomGoalTypeParameter(g -> new CustomHurtByTargetGoalPigman((ZombiePigmanEntity.HurtByAggressorGoal) g),
            1,
            2,
            Performant.getConfig().getCommon().optimizeHurtByTarget.get()));
        GOAL_TYPES.put(PolarBearEntity.HurtByTargetGoal.class,
          new CustomGoalTypeParameter(g -> new CustomHurtByTargetGoalPolarBear((PolarBearEntity.HurtByTargetGoal) g),
            1,
            2,
            Performant.getConfig().getCommon().optimizeHurtByTarget.get()));
        GOAL_TYPES.put(FoxEntity.FindShelterGoal.class, new CustomGoalTypeParameter(g -> g, 20, 1, Performant.getConfig().getCommon().slowFindShelter.get()));
    }

    /**
     * Generates the right priotized goal for the given goal and priority.
     *
     * @param priority priority to use
     * @param goal     goal to use
     * @param selector selector to use
     * @return new PrioritizedGoal to use
     */
    public PrioritizedGoal getPriotizedGoalFor(final int priority, final Goal goal, final CustomGoalSelector selector)
    {
        final CustomGoalTypeParameter params = GOAL_TYPES.get(goal.getClass());
        if (params != null && params.isEnabled)
        {
            return new CustomPriotizedSlowedGoal(priority, params.goalConverter.apply(goal), selector, params.shouldExecuteRate, params.tickRate);
        }
        else
        {
            return new PrioritizedGoal(priority, goal);
        }
    }
}
