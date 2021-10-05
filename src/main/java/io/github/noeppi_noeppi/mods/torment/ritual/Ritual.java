package io.github.noeppi_noeppi.mods.torment.ritual;

import io.github.noeppi_noeppi.mods.torment.ability.Ability;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public interface Ritual {
    
    @Nullable
    Ability getAbility();
    
    List<BlockPos> possibleStructures(Player player);
    boolean testStructure(Level level, BlockPos basePos);
    
    int duration();
    void tick(Player player, ServerLevel level, BlockPos basePos, int tick);
}
