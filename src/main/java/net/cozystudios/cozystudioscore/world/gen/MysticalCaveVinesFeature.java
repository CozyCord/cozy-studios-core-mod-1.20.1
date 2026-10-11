package net.cozystudios.cozystudioscore.world.gen;

import com.mojang.serialization.Codec;
import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CaveVines;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class MysticalCaveVinesFeature extends Feature<DefaultFeatureConfig> {

    private static final float BERRIES_CHANCE = 0.06F;
    private static final int MAX_LENGTH = 20;

    public MysticalCaveVinesFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        WorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        if (!world.isAir(origin)) return false;

        BlockState above = world.getBlockState(origin.up());
        if (!above.isSideSolidFullSquare(world, origin.up(), net.minecraft.util.math.Direction.DOWN)) {
            return false;
        }

        int length = 1 + random.nextInt(MAX_LENGTH);
        BlockPos.Mutable mutable = origin.mutableCopy();
        for (int i = 0; i < length; i++) {
            if (!world.isAir(mutable)) break;
            boolean isEnd = (i == length - 1) || !world.isAir(mutable.down());
            boolean berries = random.nextFloat() < BERRIES_CHANCE;
            Block block = isEnd ? ModBlocks.MYSTICAL_CAVE_VINES_HEAD : ModBlocks.MYSTICAL_CAVE_VINES_BODY;
            BlockState state = block.getDefaultState().with(CaveVines.BERRIES, berries);
            if (isEnd) {
                state = state.with(Properties.AGE_25, 25);
            }
            world.setBlockState(mutable, state, Block.NOTIFY_LISTENERS);
            mutable.move(net.minecraft.util.math.Direction.DOWN);
        }
        return true;
    }
}
