package com.performant.coremod.event;

import com.performant.coremod.entity.ai.CustomGoalSelector;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class EventHandler
{
    /**
     * On Entity join world event.
     *
     * @param event the event.
     */
    @SubscribeEvent
    public static void onEntityAdded(@NotNull final EntityJoinWorldEvent event)
    {
        if (!event.getWorld().isRemote)
        {
            if (event.getEntity() instanceof MobEntity)
            {
                MobEntity mob = (MobEntity) event.getEntity();
                mob.targetSelector = new CustomGoalSelector(mob.targetSelector);
                mob.goalSelector = new CustomGoalSelector(mob.goalSelector);
            }
        }
    }
}
