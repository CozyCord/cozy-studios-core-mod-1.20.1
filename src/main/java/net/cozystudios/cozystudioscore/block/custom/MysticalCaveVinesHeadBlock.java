package net.cozystudios.cozystudioscore.block.custom;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.item.ModItems;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CaveVines;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class MysticalCaveVinesHeadBlock extends AbstractPlantStemBlock {

    private static final float BERRIES_CHANCE = 0.05F;

    public MysticalCaveVinesHeadBlock(Settings settings) {
        super(settings, Direction.DOWN, VoxelShapes.fullCube(), false, 0.10);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(AGE, 0)
                .with(CaveVines.BERRIES, false));
    }

    @Override
    protected int getGrowthLength(Random random) {
        return 1;
    }

    @Override
    protected boolean chooseStemState(BlockState state) {
        return state.isAir();
    }

    @Override
    protected Block getPlant() {
        return ModBlocks.MYSTICAL_CAVE_VINES_BODY;
    }

    @Override
    protected BlockState age(BlockState state, Random random) {
        return super.age(state, random).with(CaveVines.BERRIES, random.nextFloat() < BERRIES_CHANCE);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.MYSTICAL_BERRIES);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return pickBerries(player, state, world, pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(CaveVines.BERRIES);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return !state.get(CaveVines.BERRIES);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(CaveVines.BERRIES, true), Block.NOTIFY_LISTENERS);
    }

    public static ActionResult pickBerries(PlayerEntity picker, BlockState state, World world, BlockPos pos) {
        if (!state.get(CaveVines.BERRIES)) {
            return ActionResult.PASS;
        }
        Block.dropStack(world, pos, new ItemStack(ModItems.MYSTICAL_BERRIES, 1));
        float pitch = MathHelper.nextBetween(world.random, 0.8F, 1.2F);
        world.playSound(null, pos, SoundEvents.BLOCK_CAVE_VINES_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, pitch);
        BlockState next = state.with(CaveVines.BERRIES, false);
        world.setBlockState(pos, next, Block.NOTIFY_LISTENERS);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(picker, next));
        return ActionResult.success(world.isClient());
    }
}
