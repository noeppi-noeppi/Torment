package io.github.noeppi_noeppi.mods.torment.ritual;

import io.github.noeppi_noeppi.mods.torment.ability.Ability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class MineRitual implements Ritual {

    public static final MineRitual INSTANCE = new MineRitual();
    
    private MineRitual() {
        
    }
    
    @Nullable
    @Override
    public Ability getAbility() {
        return Ability.COMPRESSED_LUCK;
    }

    @Override
    public List<BlockPos> possibleStructures(Player player) {
        return List.of();
    }

    @Override
    public boolean testStructure(Level level, BlockPos basePos) {
        return RitualHelper.all(
                RitualHelper.testBlock(level, basePos, -2, 0, 0, Blocks.CALCITE),
                RitualHelper.testBlock(level, basePos, 2, 0, 0, Blocks.CALCITE),
                RitualHelper.testBlock(level, basePos, 0, 0, -2, Blocks.CALCITE),
                RitualHelper.testBlock(level, basePos, 0, 0, 2, Blocks.CALCITE),
                RitualHelper.testBlock(level, basePos, -2, 1, 0, Blocks.LAPIS_BLOCK),
                RitualHelper.testBlock(level, basePos, 2, 1, 0, Blocks.LAPIS_BLOCK),
                RitualHelper.testBlock(level, basePos, 0, 1, -2, Blocks.LAPIS_BLOCK),
                RitualHelper.testBlock(level, basePos, 0, 1, 2, Blocks.LAPIS_BLOCK)
        );
    }

    @Override
    public int duration() {
        return 120;
    }

    @Override
    public void tick(Player player, ServerLevel level, BlockPos basePos, int tick) {
        level.sendParticles(ParticleTypes.COMPOSTER, basePos.getX() - 1.5, basePos.getY() + 1.5, basePos.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0);
        level.sendParticles(ParticleTypes.COMPOSTER, basePos.getX() + 2.5, basePos.getY() + 1.5, basePos.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0);
        level.sendParticles(ParticleTypes.COMPOSTER, basePos.getX() + 0.5, basePos.getY() + 1.5, basePos.getZ() - 1.5, 10, 0.3, 0.3, 0.3, 0);
        level.sendParticles(ParticleTypes.COMPOSTER, basePos.getX() + 0.5, basePos.getY() + 1.5, basePos.getZ() + 2.5, 10, 0.3, 0.3, 0.3, 0);
        if (tick == duration() - 1) {
            ExplosionDamageCalculator damage = new ExplosionDamageCalculator() {
                @Nonnull
                @Override
                public Optional<Float> getBlockExplosionResistance(@Nonnull Explosion explosion, @Nonnull BlockGetter reader, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull FluidState fluid) {
                    // No resistance at all
                    return Optional.of(0f);
                }

                @Override
                public boolean shouldBlockExplode(@Nonnull Explosion explosion, @Nonnull BlockGetter reader, @Nonnull BlockPos pos, @Nonnull BlockState state, float power) {
                    // Only destroy ritual blocks
                    int xd = Math.abs(pos.getX() - basePos.getX());
                    int yd = pos.getY() - basePos.getY();
                    int zd = Math.abs(pos.getZ() - basePos.getZ());
                    if (yd != 0 && yd != 1) return false;
                    return (xd == 2 && zd == 0) || (xd == 0 && zd == 2);
                }
            };
            level.explode(player, null, damage, basePos.getX() + 0.5, basePos.getY() + 0.5, basePos.getZ() + 0.5, 5, true, Explosion.BlockInteraction.BREAK);
        }
    }
}
