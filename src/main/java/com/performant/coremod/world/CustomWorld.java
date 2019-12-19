package com.performant.coremod.world;

import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.ServerWorldEventHandler;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Experimental world replacement
 */
public class CustomWorld extends WorldServer
{
    public CustomWorld(WorldServer old)
    {
        super(old.getMinecraftServer(), old.getSaveHandler(), old.getWorldInfo(), 0, old.profiler);
        this.init();
        WorldSettings worldsettings = new WorldSettings(old.getWorldInfo());
        this.initialize(worldsettings);

        this.addEventListener(new ServerWorldEventHandler(this.getMinecraftServer(), this));

        if (!this.getMinecraftServer().isSinglePlayer())
        {
            this.getWorldInfo().setGameType(this.getMinecraftServer().getGameType());
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(this));
    }

    private Cache<AxisAlignedBB, List<AxisAlignedBB>> bb = CacheBuilder.newBuilder().expireAfterWrite(500, TimeUnit.MILLISECONDS).build();

    @Override
    public List<AxisAlignedBB> getCollisionBoxes(@Nullable Entity entityIn, AxisAlignedBB aabb)
    {
        List<AxisAlignedBB> rs = bb.getIfPresent(aabb);
        if (rs == null)
        {
            rs = super.getCollisionBoxes(entityIn, aabb);
            bb.put(aabb, rs);
        }
        return rs;
    }

    private Cache<Integer, List<Entity>> entities = CacheBuilder.newBuilder().expireAfterWrite(500, TimeUnit.MILLISECONDS).build();

    @Override
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate)
    {
        List<Entity> list = entities.getIfPresent(boundingBox.hashCode());
        if (list == null)
        {
            list = super.getEntitiesInAABBexcluding(entityIn, boundingBox, EntitySelectors.NOT_SPECTATING);
            entities.put(boundingBox.hashCode(), list);
        }
        else if (predicate != null)
        {
            final List<Entity> tlist = new ArrayList<>();
            for (Entity entity : list)
            {
                if (predicate.apply(entity))
                {
                    tlist.add(entity);
                }
            }

            list = tlist;
        }
        return list;
    }
}
