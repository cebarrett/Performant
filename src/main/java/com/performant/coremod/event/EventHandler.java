package com.performant.coremod.event;

import com.performant.coremod.config.Configuration;
import com.performant.coremod.entity.ai.CustomGoalSelector;
import com.performant.coremod.world.CustomWorld;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class EventHandler
{
    /**
     * ON world load.
     *
     * @param event
     */
    @SubscribeEvent
    public void worldLoad(final WorldEvent.Load event)
    {
        if (Configuration.ex.worldReplace && event.getWorld() instanceof WorldServer && !(event.getWorld() instanceof CustomWorld)
              && ((WorldServer) event.getWorld()).provider.getDimension() == 0)
        {
            new CustomWorld((WorldServer) event.getWorld());
        }
    }

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
