package com.performant.coremod.entity.ai.goals.HurtGoals;

import net.minecraft.entity.passive.horse.LlamaEntity;

public class CustomHurtByTargetGoalLLama extends CustomHurtByTargetGoal
{
    public CustomHurtByTargetGoalLLama(final LlamaEntity.HurtByTargetGoal original)
    {
        super(original);
    }

    public boolean shouldContinueExecuting()
    {
        if (this.goalOwner instanceof LlamaEntity)
        {
            LlamaEntity llamaentity = (LlamaEntity) this.goalOwner;
            if (llamaentity.didSpit)
            {
                llamaentity.didSpit = false;
                return false;
            }
        }
        return super.shouldContinueExecuting();
    }
}
