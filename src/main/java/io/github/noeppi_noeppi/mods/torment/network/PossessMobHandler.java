package io.github.noeppi_noeppi.mods.torment.network;

import io.github.noeppi_noeppi.mods.torment.ability.Ability;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PossessMobHandler {

    public static void handle(PossessMobSerializer.PossessMobMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                TormentData data = TormentData.get(player);
                if (data.hasAbility(Ability.DEVIL_ALLIANCE)) {
                    Vec3 position = player.position().add(0, player.getEyeHeight(), 0);
                    Vec3 target = position.add(player.getLookAngle().multiply(10, 10, 10));
                    AABB bbox = player.getBoundingBox().expandTowards(target).inflate(1);
                    EntityHitResult hit = ProjectileUtil.getEntityHitResult(player.level, player, position, target, bbox, e -> e instanceof Mob);
                    if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                        if (hit.getEntity() instanceof Mob entity) {
                            if (entity.getTarget() == player) {
                                entity.setTarget(null);
                            }
                            if (!data.isPossessed(entity)) {
                                data.addPendingDevilMob(entity);
                                AABB entityAABB = entity.getBoundingBox();
                                player.getLevel().sendParticles(
                                        ParticleTypes.POOF, entity.getX(), entity.getY() + (entityAABB.getYsize() / 4d), entity.getZ(),
                                        30, entityAABB.getXsize() / 2d, entityAABB.getYsize() / 2d, entityAABB.getZsize() / 2d,
                                        0.01
                                );
                            }
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
