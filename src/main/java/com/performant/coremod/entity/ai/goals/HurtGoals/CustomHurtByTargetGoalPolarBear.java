package com.performant.coremod.entity.ai.goals.HurtGoals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.PolarBearEntity;

public class CustomHurtByTargetGoalPolarBear extends CustomHurtByTargetGoal
{
    public CustomHurtByTargetGoalPolarBear(final PolarBearEntity.HurtByTargetGoal original)
    {
        super(original);
        // Fixes vanilla logic, where help classes are empty and the alterOthers() call has no effect.
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        super.startExecuting();
        if (goalOwner.isChild())
        {
            this.alertOthers();
            this.resetTask();
        }
    }

    @Override
    public void setAttackTarget(MobEntity mobIn, LivingEntity targetIn)
    {
        if (mobIn instanceof PolarBearEntity && !mobIn.isChild())
        {
            super.setAttackTarget(mobIn, targetIn);
        }
    }
}
