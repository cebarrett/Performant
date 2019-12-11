package com.performant.coremod.entity.ai.goals.HurtGoals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.PandaEntity;

public class CustomHurtByTargetGoalPanda extends CustomHurtByTargetGoal
{
    private final PandaEntity panda;

    public CustomHurtByTargetGoalPanda(final PandaEntity.RevengeGoal original)
    {
        super(original);
        panda = (PandaEntity) original.goalOwner;
    }

    public boolean shouldContinueExecuting()
    {
        if (!this.panda.field_213598_bH && !this.panda.field_213599_bI)
        {
            return super.shouldContinueExecuting();
        }
        else
        {
            this.panda.setAttackTarget((LivingEntity) null);
            return false;
        }
    }

    public void setAttackTarget(MobEntity mobIn, LivingEntity targetIn)
    {
        if (mobIn instanceof PandaEntity && ((PandaEntity) mobIn).isAggressive())
        {
            mobIn.setAttackTarget(targetIn);
        }
    }
}
