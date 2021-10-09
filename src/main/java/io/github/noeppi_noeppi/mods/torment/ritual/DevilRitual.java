package io.github.noeppi_noeppi.mods.torment.ritual;

import io.github.noeppi_noeppi.mods.torment.ability.Ability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class DevilRitual implements Ritual {

    public static final DevilRitual INSTANCE = new DevilRitual();
    
    private DevilRitual() {
        
    }
    
    @Nullable
    @Override
    public Ability getAbility() {
        return Ability.DEVIL_ALLIANCE;
    }

    @Override
    public List<BlockPos> possibleStructures(Player player) {
        return RitualHelper.locateBlock(player, Blocks.LIGHTNING_ROD);
    }

    @Override
    @SuppressWarnings("RedundantIfStatement")
    public boolean testStructure(Level level, BlockPos basePos) {
        if (!RitualHelper.all(
                RitualHelper.testBlock(level, basePos, 0, 0, 0, Blocks.SOUL_FIRE),
                RitualHelper.testBlock(level, basePos, 0, -1, 0, Blocks.SOUL_SOIL),
                RitualHelper.testBlock(level, basePos, -2, -1, -2, Blocks.OBSIDIAN),
                RitualHelper.testBlock(level, basePos, -2, 0, -2, Blocks.OBSIDIAN),
                RitualHelper.testBlock(level, basePos, -2, -1, 2, Blocks.OBSIDIAN),
                RitualHelper.testBlock(level, basePos, -2, 0, 2, Blocks.OBSIDIAN),
                RitualHelper.testBlock(level, basePos, 2, -1, -2, Blocks.OBSIDIAN),
                RitualHelper.testBlock(level, basePos, 2, 0, -2, Blocks.OBSIDIAN),
                RitualHelper.testBlock(level, basePos, 2, -1, 2, Blocks.OBSIDIAN),
                RitualHelper.testBlock(level, basePos, 2, 0, 2, Blocks.OBSIDIAN)
        )) return false;
        if (findCrystal(level, basePos, -2, -2).isEmpty()) return false;
        if (findCrystal(level, basePos, -2, 2).isEmpty()) return false;
        if (findCrystal(level, basePos, 2, -2).isEmpty()) return false;
        if (findCrystal(level, basePos, 2, 2).isEmpty()) return false;
        return true;
    }

    @Override
    public int duration() {
        return 240;
    }

    @Override
    public void tick(Player player, ServerLevel level, BlockPos basePos, int tick) {
        if (tick == 0) {
            findCrystal(level, basePos, -2, -2).ifPresent(e -> e.setBeamTarget(basePos.above(10)));
            findCrystal(level, basePos, -2, 2).ifPresent(e -> e.setBeamTarget(basePos.above(10)));
            findCrystal(level, basePos, 2, -2).ifPresent(e -> e.setBeamTarget(basePos.above(10)));
            findCrystal(level, basePos, 2, 2).ifPresent(e -> e.setBeamTarget(basePos.above(10)));
        } else if (tick == duration() - 1) {
            findCrystal(level, basePos, -2, -2).ifPresent(EndCrystal::kill);
            findCrystal(level, basePos, -2, 2).ifPresent(EndCrystal::kill);
            findCrystal(level, basePos, 2, -2).ifPresent(EndCrystal::kill);
            findCrystal(level, basePos, 2, 2).ifPresent(EndCrystal::kill);
            if (level.getBlockState(basePos).getBlock() == Blocks.SOUL_FIRE) level.setBlock(basePos, Blocks.AIR.defaultBlockState(), 3);
        } else if (tick == duration() - 20) {
            level.sendParticles(ParticleTypes.DRAGON_BREATH, basePos.getX() + 0.5, basePos.getY() + 1, basePos.getZ() + 0.5, 500, 0.3, 0.3, 0.3, 0.08);
        }
    }
    
    private Optional<EndCrystal> findCrystal(Level level, BlockPos basePos, int x, int z) {
        AABB aabb = new AABB(basePos.getX() + x, basePos.getY() + 2, basePos.getZ() + z,
                basePos.getX() + x + 1, basePos.getY() + 3, basePos.getZ() + z + 1);
        List<EndCrystal> entities = level.getEntitiesOfClass(EndCrystal.class, aabb);
        if (!entities.isEmpty()) {
            return Optional.of(entities.get(0));
        } else {
            return Optional.empty();
        }
    }
}
