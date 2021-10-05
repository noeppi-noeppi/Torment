package io.github.noeppi_noeppi.mods.torment.ritual;

import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RitualHelper {

    @SuppressWarnings("UnusedReturnValue")
    public static boolean startRitual(Player player, Ritual ritual, @Nullable BlockPos basePos) {
        Level level = player.level;
        if (!level.isClientSide) {
            TormentData data = TormentData.get(player);
            List<BlockPos> basePositions = basePos == null ? ritual.possibleStructures(player) : List.of(basePos.immutable());
            for (BlockPos basePosition : basePositions) {
                if (ritual.testStructure(level, basePosition.immutable())) {
                    return data.tryStartRitual(ritual, basePosition.immutable());
                }
            }
        }
        return false;
    }

    public static List<BlockPos> locateBlock(Player player, Block block) {
        List<BlockPos> positions = new ArrayList<>();
        Level level = player.level;
        BlockPos basePos = player.blockPosition();
        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos pos = basePos.offset(x, y, z);
                    if (level.getBlockState(pos).getBlock() == block) {
                        positions.add(pos.immutable());
                    }
                }
            }
        }
        return positions;
    }

    public static Supplier<Boolean> testBlock(Level level, BlockPos basePos, int x, int y, int z, Block block) {
        return () -> {
            BlockState state = level.getBlockState(basePos.offset(x, y, z));
            return state.getBlock() == block;
        };
    }

    public static <T extends Comparable<T>> Supplier<Boolean> testBlock(Level level, BlockPos basePos, int x, int y, int z, Block block, Property<T> property, T value) {
        return () -> {
            BlockState state = level.getBlockState(basePos.offset(x, y, z));
            return state.getBlock() == block && value.equals(state.getValue(property));
        };
    }

    public static <T extends Comparable<T>, U extends Comparable<U>> Supplier<Boolean> testBlock(Level level, BlockPos basePos, int x, int y, int z, Block block, Property<T> property1, T value1, Property<U> property2, U value2) {
        return () -> {
            BlockState state = level.getBlockState(basePos.offset(x, y, z));
            return state.getBlock() == block && value1.equals(state.getValue(property1)) && value2.equals(state.getValue(property2));
        };
    }

    public static <T extends Comparable<T>, U extends Comparable<U>, V extends Comparable<V>> Supplier<Boolean> testBlock(Level level, BlockPos basePos, int x, int y, int z, Block block, Property<T> property1, T value1, Property<U> property2, U value2, Property<V> property3, V value3) {
        return () -> {
            BlockState state = level.getBlockState(basePos.offset(x, y, z));
            return state.getBlock() == block && value1.equals(state.getValue(property1)) && value2.equals(state.getValue(property2)) && value3.equals(state.getValue(property3));
        };
    }

    @SafeVarargs
    public static boolean all(Supplier<Boolean>... tests) {
        for (Supplier<Boolean> test : tests) {
            if (!test.get()) return false;
        }
        return true;
    }
}
