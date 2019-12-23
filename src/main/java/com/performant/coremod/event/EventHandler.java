package com.performant.coremod.event;

import com.performant.coremod.entity.ai.CustomGoalSelector;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class EventHandler
{
    /**
     * On Entity join world event.
     *
     * @param event the event.
     */
    @SubscribeEvent
    public void onEntityAdded(@NotNull final EntityJoinWorldEvent event)
    {
        if (!event.getWorld().isRemote)
        {
            if (event.getEntity() instanceof EntityLiving)
            {
                EntityLiving mob = (EntityLiving) event.getEntity();
                mob.targetTasks = new CustomGoalSelector(mob.targetTasks);
                mob.tasks = new CustomGoalSelector(mob.tasks);
            }
        }
    }
}
