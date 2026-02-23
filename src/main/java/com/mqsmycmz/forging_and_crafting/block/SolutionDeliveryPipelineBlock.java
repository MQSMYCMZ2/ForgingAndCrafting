package com.mqsmycmz.forging_and_crafting.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SolutionDeliveryPipelineBlock extends Block {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    private static final VoxelShape NORTH_SHAPE = Stream.of(
            Block.box(6.45, 6.875, 0, 6.95, 9.625, 0.75),
            Block.box(6.45, 6.375, 0, 9.2, 6.875, 0.75),
            Block.box(9.2, 6.375, 0, 9.7, 9.125, 0.75),
            Block.box(7.45, 6.875, 0.75, 9.2, 7.375, 1.25),
            Block.box(6.95, 8.625, 0.75, 8.7, 9.125, 1.25),
            Block.box(8.7, 7.375, 0.75, 9.2, 9.125, 1.25),
            Block.box(6.95, 6.875, 0.75, 7.45, 8.625, 1.25),
            Block.box(6.95, 9.125, 0, 9.7, 9.625, 0.75),
            Block.box(9.1, 7.125, 1.25, 9.35, 8.875, 8),
            Block.box(6.85, 7.125, 1.25, 7.1, 8.875, 8),
            Block.box(6.85, 6.875, 1.25, 9.35, 7.125, 8),
            Block.box(6.85, 8.875, 1.25, 9.35, 9.125, 8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SOUTH_SHAPE = Stream.of(
            Block.box(6.45, 6.875, 15.25, 6.95, 9.625, 16),
            Block.box(6.45, 6.375, 15.25, 9.2, 6.875, 16),
            Block.box(9.2, 6.375, 15.25, 9.7, 9.125, 16),
            Block.box(7.45, 6.875, 14.75, 9.2, 7.375, 15.25),
            Block.box(6.95, 8.625, 14.75, 8.7, 9.125, 15.25),
            Block.box(8.7, 7.375, 14.75, 9.2, 9.125, 15.25),
            Block.box(6.95, 6.875, 14.75, 7.45, 8.625, 15.25),
            Block.box(6.95, 9.125, 15.25, 9.7, 9.625, 16),
            Block.box(9.1, 7.125, 8, 9.35, 8.875, 14.75),
            Block.box(6.85, 7.125, 8, 7.1, 8.875, 14.75),
            Block.box(6.85, 6.875, 8, 9.35, 7.125, 14.75),
            Block.box(6.85, 8.875, 8, 9.35, 9.125, 14.75)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape EAST_SHAPE = Stream.of(
            Block.box(15.325, 6.875, 6.375, 16.075, 9.625, 6.875),
            Block.box(15.325, 6.375, 6.375, 16.075, 6.875, 9.125),
            Block.box(15.325, 6.375, 9.125, 16.075, 9.125, 9.625),
            Block.box(14.825, 6.875, 7.375, 15.325, 7.375, 9.125),
            Block.box(14.825, 8.625, 6.875, 15.325, 9.125, 8.625),
            Block.box(14.825, 7.375, 8.625, 15.325, 9.125, 9.125),
            Block.box(14.825, 6.875, 6.875, 15.325, 8.625, 7.375),
            Block.box(15.325, 9.125, 6.875, 16.075, 9.625, 9.625),
            Block.box(8.075, 7.125, 9.025, 14.825, 8.875, 9.275),
            Block.box(8.075, 7.125, 6.775, 14.825, 8.875, 7.025),
            Block.box(8.075, 6.875, 6.775, 14.825, 7.125, 9.275),
            Block.box(8.075, 8.875, 6.775, 14.825, 9.125, 9.275)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape WEST_SHAPE = Stream.of(
            Block.box(0.075, 6.875, 6.375, 0.825, 9.625, 6.875),
            Block.box(0.075, 6.375, 6.375, 0.825, 6.875, 9.125),
            Block.box(0.075, 6.375, 9.125, 0.825, 9.125, 9.625),
            Block.box(0.825, 6.875, 7.375, 1.325, 7.375, 9.125),
            Block.box(0.825, 8.625, 6.875, 1.325, 9.125, 8.625),
            Block.box(0.825, 7.375, 8.625, 1.325, 9.125, 9.125),
            Block.box(0.825, 6.875, 6.875, 1.325, 8.625, 7.375),
            Block.box(0.075, 9.125, 6.875, 0.825, 9.625, 9.625),
            Block.box(1.325, 7.125, 9.025, 8.075, 8.875, 9.275),
            Block.box(1.325, 7.125, 6.775, 8.075, 8.875, 7.025),
            Block.box(1.325, 6.875, 6.775, 8.075, 7.125, 9.275),
            Block.box(1.325, 8.875, 6.775, 8.075, 9.125, 9.275)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape UP_SHAPE = Stream.of(
            Block.box(9.2, 15.25, 6.375, 9.7, 16, 9.125),
            Block.box(6.95, 15.25, 9.125, 9.7, 16, 9.625),
            Block.box(6.45, 15.25, 6.875, 6.95, 16, 9.625),
            Block.box(6.45, 15.25, 6.375, 9.2, 16, 6.875),
            Block.box(7.45, 14.75, 6.875, 9.2, 15.25, 7.375),
            Block.box(6.95, 14.75, 8.625, 8.7, 15.25, 9.125),
            Block.box(6.95, 14.75, 6.875, 7.45, 15.25, 8.625),
            Block.box(8.7, 14.75, 7.375, 9.2, 15.25, 9.125),
            Block.box(6.95, 8, 6.775, 7.2, 14.75, 9.275),
            Block.box(7.2, 8, 9.025, 8.95, 14.75, 9.275),
            Block.box(8.95, 8, 6.775, 9.2, 14.75, 9.275),
            Block.box(7.2, 8, 6.775, 8.95, 14.75, 7.025)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape DOWN_SHAPE = Stream.of(
            Block.box(6.45, 0, 6.375, 9.2, 0.75, 6.875),
            Block.box(9.2, 0, 6.375, 9.7, 0.75, 9.125),
            Block.box(6.95, 0, 9.125, 9.7, 0.75, 9.625),
            Block.box(8.7, 0.75, 7.375, 9.2, 1.25, 9.125),
            Block.box(6.95, 0.75, 6.875, 7.45, 1.25, 8.625),
            Block.box(6.95, 0.75, 8.625, 8.7, 1.25, 9.125),
            Block.box(7.45, 0.75, 6.875, 9.2, 1.25, 7.375),
            Block.box(6.45, 0, 6.875, 6.95, 0.75, 9.625),
            Block.box(7.2, 1.25, 9.025, 8.95, 8, 9.275),
            Block.box(7.2, 1.25, 6.775, 8.95, 8, 7.025),
            Block.box(8.95, 1.25, 6.775, 9.2, 8, 9.275),
            Block.box(6.95, 1.25, 6.775, 7.2, 8, 9.275)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape UNCONNECTED_SHAPE = Block.box(5.5, 5.5, 5.5, 10.5, 10.5, 10.5);

    private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new ConcurrentHashMap<>();

    public SolutionDeliveryPipelineBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(getStateDefinition().any()
                .setValue(CONNECTED, false)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    public static boolean hasAnyConnection(BlockState state) {
        return state.getValue(NORTH) ||
                state.getValue(EAST) ||
                state.getValue(SOUTH) ||
                state.getValue(WEST) ||
                state.getValue(UP) ||
                state.getValue(DOWN);
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getCollisionShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (!pState.getValue(CONNECTED)) {
            return UNCONNECTED_SHAPE;
        }
        return SHAPE_CACHE.computeIfAbsent(pState, this::calculateConnectedShape);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if (!pState.getValue(CONNECTED)) {
            return UNCONNECTED_SHAPE;
        }
        return SHAPE_CACHE.computeIfAbsent(pState, this::calculateConnectedShape);
    }

    private VoxelShape calculateConnectedShape(BlockState state) {
        // 【优化】使用更小的中心形状，减少重叠计算
        var shape = Block.box(6.5, 6.5, 6.5, 9.5, 9.5, 9.5);

        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (state.getValue(EAST))  shape = Shapes.or(shape, EAST_SHAPE);
        if (state.getValue(WEST))  shape = Shapes.or(shape, WEST_SHAPE);
        if (state.getValue(UP))    shape = Shapes.or(shape, UP_SHAPE);
        if (state.getValue(DOWN))  shape = Shapes.or(shape, DOWN_SHAPE);

        return shape;
    }

    public static BooleanProperty getConnection(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        var world = pContext.getLevel();
        var pos = pContext.getClickedPos();
        var state = this.defaultBlockState();

        for (var direction : Direction.values()) {
            var neighborPos = pos.relative(direction);
            var neighborState = world.getBlockState(neighborPos);
            var connect = canConnect(neighborState, neighborPos, world, direction);
            state = state.setValue(getConnection(direction), connect);
        }

        boolean hasConnection = hasAnyConnection(state);
        state = state.setValue(CONNECTED, hasConnection);

        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction pDirection, BlockState pNeighborState,
                                  LevelAccessor world, BlockPos pos, BlockPos pNeighborPos) {
        if (!(world instanceof Level level)) {
            return state;
        }

        for (var dir : Direction.values()) {
            var p = pos.relative(dir);
            var s = world.getBlockState(p);
            var connected = canConnect(s, p, level, dir);
            var connectionProperty = getConnection(dir);

            if (state.getValue(connectionProperty) != connected) {
                state = state.setValue(connectionProperty, connected);
            }
        }

        boolean hasConnection = hasAnyConnection(state);
        state = state.setValue(CONNECTED, hasConnection);

        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);

        var newState = calculateConnections(state, world, pos);
        boolean hasConnection = hasAnyConnection(newState);
        newState = newState.setValue(CONNECTED, hasConnection);

        if (newState != state) {
            world.setBlock(pos, newState, 2);
        }
    }

    private BlockState calculateConnections(BlockState state, Level world, BlockPos pos) {
        for (var dir : Direction.values()) {
            var p = pos.relative(dir);
            var s = world.getBlockState(p);
            var connected = canConnect(s, p, world, dir);
            state = state.setValue(getConnection(dir), connected);
        }
        return state;
    }

    private boolean canConnect(BlockState neighborState, BlockPos pos, Level world, Direction direction) {
        if (neighborState.is(Blocks.COMPOSTER))
            return true;

        if (neighborState.getBlock() instanceof SolutionDeliveryPipelineBlock || neighborState.getBlock() instanceof ElectricEnergyTransmissionPipelineBlock)
            return true;

        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            return true;
        }

        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }
}