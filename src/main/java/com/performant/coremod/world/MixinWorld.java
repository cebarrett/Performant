package com.performant.coremod.world;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Mixin(World.class)
public class MixinWorld
{
    public Cache<AxisAlignedBB, List<AxisAlignedBB>> bbCache = new Cache2kBuilder<AxisAlignedBB, List<AxisAlignedBB>>() {}.expireAfterWrite(500, TimeUnit.MILLISECONDS).build();
    public Cache<AxisAlignedBB, List<Entity>>        eeCache = new Cache2kBuilder<AxisAlignedBB, List<Entity>>() {}.expireAfterWrite(500, TimeUnit.MILLISECONDS).build();

    /**
     * Collision Boxes early out
     */
    @Inject(method = "getCollisionBoxes(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;", at = @At(value = "HEAD"), cancellable = true)
    public List<AxisAlignedBB> performant_getCollisionBoxes(Entity entity, AxisAlignedBB bb, CallbackInfoReturnable ci)
    {
        List<AxisAlignedBB> rs = bbCache.get(bb);
        if (rs != null)
        {
            ci.setReturnValue(rs);
        }
        return null;
    }

    /**
     * Collision boxes gather result
     */
    @Inject(method = "getCollisionBoxes(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;", at = @At(value = "RETURN"))
    public List<AxisAlignedBB> performant_getCollisionBoxesReturn(Entity entity, AxisAlignedBB bb, CallbackInfoReturnable ci)
    {
        List<AxisAlignedBB> rs = (List<AxisAlignedBB>) ci.getReturnValue();
        bbCache.put(bb, rs);
        return rs;
    }

    /**
     * GetEntities Early out
     */
    @Inject(method = "getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;", at = @At(value = "HEAD"), cancellable = true)
    public List<Entity> performant_getEntitiesInAABBexcluding(Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate, CallbackInfoReturnable ci)
    {
        List<Entity> entities = eeCache.get(boundingBox);

        if (entities != null)
        {
            if (!entities.isEmpty() && predicate.apply(entities.get(0)))
            {
                ci.setReturnValue(entities);
            }
        }
        return null;
    }

    /**
     * Get Entities gather result
     */
    @Inject(method = "getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;", at = @At(value = "RETURN"))
    public List<Entity> performant_getEntitiesInAABBexcludingReturn(Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate, CallbackInfoReturnable ci)
    {
        eeCache.put(boundingBox, (List) ci.getReturnValue());
        return (List) ci.getReturnValue();
    }

    /**
     * GetEntities Early out
     */
    @Inject(method = "getEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;", at = @At(value = "HEAD"), cancellable = true)
    public <T extends Entity> List<T> performant_getEntitiesWithinAABB(
      Class<? extends T> clazz,
      AxisAlignedBB aabb,
      @Nullable Predicate<? super T> filter,
      CallbackInfoReturnable ci)
    {
        List<Entity> entities = eeCache.get(aabb);
        if (entities != null)
        {
            if (!entities.isEmpty() && clazz.isInstance(entities.get(0)) && filter.apply((T) entities.get(0)))
            {
                ci.setReturnValue(entities);
            }
        }
        return null;
    }

    /**
     * Get Entities gather result
     */
    @Inject(method = "getEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;", at = @At(value = "RETURN"))
    public <T extends Entity> List<T> performant_getEntitiesWithinAABBResult(
      Class<? extends T> clazz,
      AxisAlignedBB aabb,
      @Nullable Predicate<? super T> filter,
      CallbackInfoReturnable ci)
    {
        eeCache.put(aabb, (List) ci.getReturnValue());
        return (List<T>) ci.getReturnValue();
    }
}