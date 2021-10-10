package io.github.noeppi_noeppi.mods.torment.effect.instances;

import com.google.common.collect.ImmutableMap;
import io.github.noeppi_noeppi.mods.torment.effect.EffectConfig;
import io.github.noeppi_noeppi.mods.torment.effect.TormentEffect;
import io.github.noeppi_noeppi.mods.torment.util.WeightTable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class RandomBlocksEffect implements TormentEffect {

    public static final RandomBlocksEffect INSTANCE = new RandomBlocksEffect();

    private final WeightTable<BlockState> states = WeightTable.<BlockState>builder()
            .add(Blocks.SAND.defaultBlockState(), 5)
            .add(Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 1)
            .add(Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 1)
            .add(Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST), 1)
            .add(Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), 1)
            .add(Blocks.JACK_O_LANTERN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 1)
            .add(Blocks.JACK_O_LANTERN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 1)
            .add(Blocks.JACK_O_LANTERN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST), 1)
            .add(Blocks.JACK_O_LANTERN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), 1)
            .add(Blocks.REDSTONE_TORCH.defaultBlockState(), 3)
            .add(Blocks.COBWEB.defaultBlockState(), 6)
            .add(Blocks.BIRCH_PLANKS.defaultBlockState(), 1)
            .build();
    
    private Map<BlockPos, BlockState> blocks = Collections.emptyMap();
    
    private RandomBlocksEffect() {

    }

    @Override
    public int minCoolDown() {
        return 6000;
    }

    @Override
    public float minLevel() {
        return 10;
    }

    @Override
    public int weight() {
        return 2;
    }

    @Nullable
    @Override
    public EffectConfig start(LocalPlayer player, Random random) {
        ImmutableMap.Builder<BlockPos, BlockState> builder = ImmutableMap.builder();
        int blockAmount = 4 + random.nextInt(7);
        for (int i = 0; i < blockAmount; i++) {
            BlockState state = states.random(random);
            if (state != null) {
                int x = player.blockPosition().getX() - 20 + random.nextInt(40);
                int z = player.blockPosition().getZ() - 20 + random.nextInt(40);
                int y = player.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) + 20;
                BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos(x, y, z);
                while (player.level.getBlockState(mpos).isAir()) {
                    mpos.move(Direction.DOWN);
                    y -= 1;
                }
                y += 1;
                builder.put(new BlockPos(x, y, z), state);
            }
        }
        blocks = builder.build();
        return new EffectConfig(2000 + random.nextInt(3000), 2 + random.nextInt(3));
    }

    @Override
    public void update(LocalPlayer player, Random random) {

    }

    @Override
    public void stop(LocalPlayer player, Random random) {
        blocks = Collections.emptyMap();
    }

    public Map<BlockPos, BlockState> getBlocks() {
        return blocks;
    }
}
