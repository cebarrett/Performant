package com.performant.coremod.entity.ai.goals;

import net.minecraft.entity.ai.EntityAIBase;

public class FakeEmptyGoal extends EntityAIBase
{
    public static long shoulExecuteTimes = 0;

    @Override
    public boolean shouldExecute()
    {
        shoulExecuteTimes++;
        return false;
    }
}
