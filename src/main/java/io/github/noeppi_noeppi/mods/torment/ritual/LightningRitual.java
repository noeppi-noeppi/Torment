package io.github.noeppi_noeppi.mods.torment.ritual;

import io.github.noeppi_noeppi.mods.torment.ability.Ability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

import javax.annotation.Nullable;
import java.util.List;

public class LightningRitual implements Ritual {

    public static final LightningRitual INSTANCE = new LightningRitual();
    
    private LightningRitual() {
        
    }
    
    @Nullable
    @Override
    public Ability getAbility() {
        return Ability.LIGHTNING_STRIKE;
    }

    @Override
    public List<BlockPos> possibleStructures(Player player) {
        return RitualHelper.locateBlock(player, Blocks.LIGHTNING_ROD);
    }

    @Override
    public boolean testStructure(Level level, BlockPos basePos) {
        return level.canSeeSky(basePos.above()) && RitualHelper.all(
                RitualHelper.testBlock(level, basePos, 0, 0, 0, Blocks.LIGHTNING_ROD,
                        BlockStateProperties.FACING, Direction.UP),
                RitualHelper.testBlock(level, basePos, 0, -1, 0, Blocks.NETHERRACK),
                RitualHelper.testBlock(level, basePos, 1, -1, 0, Blocks.CUT_COPPER_STAIRS,
                        BlockStateProperties.HALF, Half.BOTTOM,
                        BlockStateProperties.STAIRS_SHAPE, StairsShape.STRAIGHT,
                        BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
                RitualHelper.testBlock(level, basePos, -1, -1, 0, Blocks.CUT_COPPER_STAIRS,
                        BlockStateProperties.HALF, Half.BOTTOM,
                        BlockStateProperties.STAIRS_SHAPE, StairsShape.STRAIGHT,
                        BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
                RitualHelper.testBlock(level, basePos, 0, -1, 1, Blocks.CUT_COPPER_STAIRS,
                        BlockStateProperties.HALF, Half.BOTTOM,
                        BlockStateProperties.STAIRS_SHAPE, StairsShape.STRAIGHT,
                        BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
                RitualHelper.testBlock(level, basePos, 0, -1, -1, Blocks.CUT_COPPER_STAIRS,
                        BlockStateProperties.HALF, Half.BOTTOM,
                        BlockStateProperties.STAIRS_SHAPE, StairsShape.STRAIGHT,
                        BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
                RitualHelper.testBlock(level, basePos, 2, -1, -1, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, 2, -1, 0, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, 2, -1, 1, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, 1, -1, 1, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, 1, -1, 2, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, 0, -1, 2, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, -1, -1, 2, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, -1, -1, 1, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, -2, -1, 1, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, -2, -1, 0, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, -2, -1, -1, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, -1, -1, -1, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, -1, -1, -2, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, 0, -1, -2, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, 1, -1, -2, Blocks.REDSTONE_WIRE),
                RitualHelper.testBlock(level, basePos, 1, -1, -1, Blocks.REDSTONE_WIRE)
        );
    }

    @Override
    public int duration() {
        return 240;
    }

    @Override
    public void tick(Player player, ServerLevel level, BlockPos basePos, int tick) {
        if (tick % 4 == 0) {
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            lightning.setPos(basePos.getX() + 0.5, basePos.getY() + 0.5, basePos.getZ() + 0.5);
            level.addFreshEntity(lightning);
        }
    }
}
