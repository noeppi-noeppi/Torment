package io.github.noeppi_noeppi.mods.torment.network;

import io.github.noeppi_noeppi.mods.torment.ability.Ability;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import io.github.noeppi_noeppi.mods.torment.util.EntityHitUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PossessMobHandler {

    public static void handle(PossessMobSerializer.PossessMobMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                TormentData data = TormentData.get(player);
                if (data.hasAbility(Ability.DEVIL_ALLIANCE)) {
                    LivingEntity hitEntity = EntityHitUtil.hitEntity(player, 20, 1);
                    if (hitEntity instanceof Mob entity) {
                        if (entity.getTarget() == player) {
                            entity.setTarget(null);
                        }
                        if (!data.isPossessed(entity)) {
                            data.addPendingDevilMob(entity);
                            entity.setPersistenceRequired();
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
        });
        ctx.get().setPacketHandled(true);
    }
}
