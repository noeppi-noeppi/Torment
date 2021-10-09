package io.github.noeppi_noeppi.mods.torment.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class EntityHitUtil {
    
    @Nullable
    public static LivingEntity hitEntity(LivingEntity from, double distance, double inflate) {
        Vec3 position = from.position().add(0, from.getEyeHeight(), 0);
        Vec3 target = position.add(from.getLookAngle().normalize().multiply(distance + 2 * inflate, distance + 2 * inflate, distance + 2 * inflate));
        AABB aabb = new AABB(position, target);
        List<LivingEntity> livings = from.level.getEntitiesOfClass(LivingEntity.class, aabb);
        double currentDistanceSq = Double.POSITIVE_INFINITY;
        LivingEntity currentEntity = null;
        for (LivingEntity entity : livings) {
            if (!entity.isSpectator()) {
                AABB bbox = entity.getBoundingBox().inflate(inflate);
                Vec3 hit = bbox.clip(position, target).orElse(null);
                if (hit != null) {
                    double distanceSq = position.distanceToSqr(hit);
                    if (distanceSq < currentDistanceSq) {
                        currentDistanceSq = distanceSq;
                        currentEntity = entity;
                    }
                }
            }
        }
        return currentEntity;
    }
}
