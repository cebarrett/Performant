package com.performant.coremod.entity.ai.goals.HurtGoals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class CustomHurtByTargetGoal extends HurtByTargetGoal
{
    protected static final EntityPredicate field_220795_a = (new EntityPredicate()).setLineOfSiteRequired().setUseInvisibilityCheck();
    protected              boolean         entityCallsForHelp;
    protected              int             revengeTimerOld;
    protected final        Class<?>[]      excludedReinforcementTypes;
    protected              Class<?>[]      excludedClassFromHelp;

    public CustomHurtByTargetGoal(final HurtByTargetGoal original)
    {
        super((CreatureEntity) original.goalOwner, original.excludedReinforcementTypes);
        this.entityCallsForHelp = original.entityCallsForHelp;
        super.entityCallsForHelp = original.entityCallsForHelp;
        this.revengeTimerOld = original.revengeTimerOld;
        super.revengeTimerOld = original.revengeTimerOld;
        this.excludedReinforcementTypes = original.excludedReinforcementTypes;
        super.excludedReinforcementTypes = original.excludedReinforcementTypes;
        this.excludedClassFromHelp = original.field_220797_i;
        super.field_220797_i = original.field_220797_i;
    }

    @Override
    public HurtByTargetGoal setCallsForHelp(Class<?>... callToHelpClasses)
    {
        this.entityCallsForHelp = true;
        this.excludedClassFromHelp = callToHelpClasses;
        return this;
    }

    @Override
    protected void alertOthers()
    {
        // Added: early out if there is no revenge target to call helpers to or no possible helper class
        if (goalOwner.getRevengeTarget() == null)
        {
            return;
        }

        final double targetDistance = this.getTargetDistance();
        final List<MobEntity> mobEntities = this.goalOwner.world.getEntitiesWithinAABB(this.goalOwner.getClass(),
          (new AxisAlignedBB(this.goalOwner.posX,
            this.goalOwner.posY,
            this.goalOwner.posZ,
            this.goalOwner.posX + 1.0D,
            this.goalOwner.posY + 1.0D,
            this.goalOwner.posZ + 1.0D)).grow(targetDistance, 10.0D, targetDistance));


        for (final MobEntity mob : mobEntities)
        {
            if (mob == null || mob == goalOwner || mob.getAttackTarget() != null ||
                  goalOwner instanceof TameableEntity && ((TameableEntity) goalOwner).getOwner() != ((TameableEntity) mob).getOwner() ||
                  mob.isOnSameTeam(goalOwner.getRevengeTarget()))
            {
                continue;
            }

            final int length = excludedClassFromHelp.length;
            boolean shouldHelp = true;
            for (int i = 0; i < length; ++i)
            {
                final Class<?> classEntry = excludedClassFromHelp[i];
                if (mob.getClass() == classEntry)
                {
                    shouldHelp = false;
                    break;
                }
            }

            if (shouldHelp)
            {
                setAttackTarget(mob, goalOwner.getRevengeTarget());
            }
        }
    }
}
