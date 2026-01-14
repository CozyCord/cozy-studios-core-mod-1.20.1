package net.cozystudios.cozystudioscore.block.custom;

import net.cozystudios.cozystudioscore.block.entity.GoldenTranquilLanternBlockEntity;
import net.cozystudios.cozystudioscore.world.TranquilLanternSpawnBlocker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GoldenTranquilLanternBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = net.minecraft.block.HorizontalFacingBlock.FACING;

    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(4, 0, 4, 12, 2, 12),   // baseplate
            Block.createCuboidShape(4, 8, 4, 12, 10, 12),  // middle ring
            Block.createCuboidShape(5, 10, 5, 11, 12, 11), // inner cube
            Block.createCuboidShape(7, 12, 7, 9, 14, 9),   // tiny top cube
            Block.createCuboidShape(5, 13, 8, 11, 18, 8),  // handle/stem
            Block.createCuboidShape(5, 2, 5, 11, 8, 11)    // main body
    );

    public GoldenTranquilLanternBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GoldenTranquilLanternBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, net.cozystudios.cozystudioscore.block.entity.ModBlockEntities.GOLDEN_TRANQUIL_LANTERN, GoldenTranquilLanternBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient && world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
            TranquilLanternSpawnBlocker.addLantern(serverWorld, pos);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (!world.isClient && world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
            TranquilLanternSpawnBlocker.removeLantern(serverWorld, pos);
        }
    }
}
