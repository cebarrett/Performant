package com.performant.coremod.entity.ai.goals.HurtGoals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;

public class CustomHurtByTargetGoalPigman extends CustomHurtByTargetGoal
{
    public CustomHurtByTargetGoalPigman(final ZombiePigmanEntity.HurtByAggressorGoal original)
    {
        super(original);
    }

    @Override
    public void setAttackTarget(MobEntity mobIn, LivingEntity targetIn)
    {
        if (mobIn instanceof ZombiePigmanEntity && this.goalOwner.canEntityBeSeen(targetIn) && ((ZombiePigmanEntity) mobIn).func_226547_i_(targetIn))
        {
            mobIn.setAttackTarget(targetIn);
        }
    }
}
