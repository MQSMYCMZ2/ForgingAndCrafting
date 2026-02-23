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

public class InformationTransmissionCableBlock extends Block {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    
    private static final VoxelShape NORTH_SHAPE = Stream.of(
            Block.box(6.25, 9.25, 0, 9.25, 9.75, 0.5),
            Block.box(6.25, 6.25, 0, 6.75, 9.25, 0.5),
            Block.box(6.75, 6.25, 0, 9.75, 6.75, 0.5),
            Block.box(9.25, 6.75, 0, 9.75, 9.75, 0.5),
            Block.box(6.75, 6.75, 0.5, 8.25, 7.75, 1),
            Block.box(6.75, 7.75, 0.5, 7.75, 9.25, 1),
            Block.box(7.75, 8.25, 0.5, 9.25, 9.25, 1),
            Block.box(8.25, 6.75, 0.5, 9.25, 8.25, 1),
            Block.box(6.25, 6.25, 0.5, 6.5, 6.75, 8),
            Block.box(6.25, 8.975, 0.5, 6.5, 9.475, 8),
            Block.box(6.25, 9.475, 0.5, 9.5, 9.725, 8),
            Block.box(9.5, 9.225, 0.5, 9.75, 9.725, 8),
            Block.box(6.5, 6.25, 0.5, 9.75, 6.5, 8),
            Block.box(9.5, 6.5, 0.5, 9.75, 7, 8),
            Block.box(9.5, 7.25, 0.5, 9.75, 7.5, 8),
            Block.box(9.5, 7.75, 0.5, 9.75, 8, 8),
            Block.box(9.5, 8.25, 0.5, 9.75, 8.5, 8),
            Block.box(9.5, 8.75, 0.5, 9.75, 9, 8),
            Block.box(6.25, 7, 0.5, 6.5, 7.25, 8),
            Block.box(6.25, 7.5, 0.5, 6.5, 7.75, 8),
            Block.box(6.25, 8, 0.5, 6.5, 8.25, 8),
            Block.box(6.25, 8.5, 0.5, 6.5, 8.75, 8),
            Block.box(8, 7, 1, 8.5, 7.5, 8),
            Block.box(7, 7.5, 1, 7.5, 8, 8),
            Block.box(8, 8.75, 1, 8.5, 9.25, 8),
            Block.box(7.5, 8.25, 1, 8, 8.75, 8),
            Block.box(8.5, 8, 1, 9, 8.5, 8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    private static final VoxelShape SOUTH_SHAPE = Stream.of(
            Block.box(6.25, 9.25, 15.5, 9.25, 9.75, 16),
            Block.box(7.75, 8.25, 15, 9.25, 9.25, 15.5),
            Block.box(6.75, 6.75, 15, 8.25, 7.75, 15.5),
            Block.box(6.75, 7.75, 15, 7.75, 9.25, 15.5),
            Block.box(8.25, 6.75, 15, 9.25, 8.25, 15.5),
            Block.box(6.25, 6.25, 15.5, 6.75, 9.25, 16),
            Block.box(6.75, 6.25, 15.5, 9.75, 6.75, 16),
            Block.box(9.25, 6.75, 15.5, 9.75, 9.75, 16),
            Block.box(6.25, 6.25, 8, 6.5, 6.75, 15.5),
            Block.box(6.25, 8.975, 8, 6.5, 9.475, 15.5),
            Block.box(6.25, 9.475, 8, 9.5, 9.725, 15.5),
            Block.box(9.5, 9.225, 8, 9.75, 9.725, 15.5),
            Block.box(6.5, 6.25, 8, 9.75, 6.5, 15.5),
            Block.box(9.5, 6.5, 8, 9.75, 7, 15.5),
            Block.box(9.5, 7.25, 8, 9.75, 7.5, 15.5),
            Block.box(9.5, 7.75, 8, 9.75, 8, 15.5),
            Block.box(9.5, 8.25, 8, 9.75, 8.5, 15.5),
            Block.box(9.5, 8.75, 8, 9.75, 9, 15.5),
            Block.box(6.25, 7, 8, 6.5, 7.25, 15.5),
            Block.box(6.25, 7.5, 8, 6.5, 7.75, 15.5),
            Block.box(6.25, 8, 8, 6.5, 8.25, 15.5),
            Block.box(6.25, 8.5, 8, 6.5, 8.75, 15.5),
            Block.box(8, 7, 8, 8.5, 7.5, 15),
            Block.box(7, 7.5, 8, 7.5, 8, 15),
            Block.box(8, 7.75, 8, 8.5, 8.25, 15),
            Block.box(7.5, 8.25, 8, 8, 8.75, 15),
            Block.box(8.5, 8, 8, 9, 8.5, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    private static final VoxelShape EAST_SHAPE = Stream.of(
            Block.box(15.5, 9.25, 6.25, 16, 9.75, 9.25),
            Block.box(15.5, 6.25, 6.25, 16, 9.25, 6.75),
            Block.box(15.5, 6.25, 6.75, 16, 6.75, 9.75),
            Block.box(15.5, 6.75, 9.25, 16, 9.75, 9.75),
            Block.box(15, 6.75, 6.75, 15.5, 7.75, 8.25),
            Block.box(15, 7.75, 6.75, 15.5, 9.25, 7.75),
            Block.box(15, 8.25, 7.75, 15.5, 9.25, 9.25),
            Block.box(15, 6.75, 8.25, 15.5, 8.25, 9.25),
            Block.box(8, 6.25, 6.25, 15.5, 6.75, 6.5),
            Block.box(8, 8.975, 6.25, 15.5, 9.475, 6.5),
            Block.box(8, 9.475, 6.25, 15.5, 9.725, 9.5),
            Block.box(8, 9.225, 9.5, 15.5, 9.725, 9.75),
            Block.box(8, 6.25, 6.5, 15.5, 6.5, 9.75),
            Block.box(8, 6.5, 9.5, 15.5, 7, 9.75),
            Block.box(8, 7.25, 9.5, 15.5, 7.5, 9.75),
            Block.box(8, 7.75, 9.5, 15.5, 8, 9.75),
            Block.box(8, 8.25, 9.5, 15.5, 8.5, 9.75),
            Block.box(8, 8.75, 9.5, 15.5, 9, 9.75),
            Block.box(8, 7, 6.25, 15.5, 7.25, 6.5),
            Block.box(8, 7.5, 6.25, 15.5, 7.75, 6.5),
            Block.box(8, 8, 6.25, 15.5, 8.25, 6.5),
            Block.box(8, 8.5, 6.25, 15.5, 8.75, 6.5),
            Block.box(8, 7, 8, 15, 7.5, 8.5),
            Block.box(8, 7.5, 7, 15, 8, 7.5),
            Block.box(8, 7.75, 8, 15, 8.25, 8.5),
            Block.box(8, 8.25, 7.5, 15, 8.75, 8),
            Block.box(8, 8, 8.5, 15, 8.5, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    private static final VoxelShape WEST_SHAPE = Stream.of(
            Block.box(0, 9.25, 6.75, 0.5, 9.75, 9.75),
            Block.box(0, 6.25, 9.25, 0.5, 9.25, 9.75),
            Block.box(0, 6.25, 6.25, 0.5, 6.75, 9.25),
            Block.box(0, 6.75, 6.25, 0.5, 9.75, 6.75),
            Block.box(0.5, 6.75, 7.75, 1, 7.75, 9.25),
            Block.box(0.5, 7.75, 8.25, 1, 9.25, 9.25),
            Block.box(0.5, 8.25, 6.75, 1, 9.25, 8.25),
            Block.box(0.5, 6.75, 6.75, 1, 8.25, 7.75),
            Block.box(0.5, 6.25, 9.5, 8, 6.75, 9.75),
            Block.box(0.5, 8.975, 9.5, 8, 9.475, 9.75),
            Block.box(0.5, 9.475, 6.5, 8, 9.725, 9.75),
            Block.box(0.5, 9.225, 6.25, 8, 9.725, 6.5),
            Block.box(0.5, 6.25, 6.25, 8, 6.5, 9.5),
            Block.box(0.5, 6.5, 6.25, 8, 7, 6.5),
            Block.box(0.5, 7.25, 6.25, 8, 7.5, 6.5),
            Block.box(0.5, 7.75, 6.25, 8, 8, 6.5),
            Block.box(0.5, 8.25, 6.25, 8, 8.5, 6.5),
            Block.box(0.5, 8.75, 6.25, 8, 9, 6.5),
            Block.box(0.5, 7, 9.5, 8, 7.25, 9.75),
            Block.box(0.5, 7.5, 9.5, 8, 7.75, 9.75),
            Block.box(0.5, 8, 9.5, 8, 8.25, 9.75),
            Block.box(0.5, 8.5, 9.5, 8, 8.75, 9.75),
            Block.box(1, 7, 7.5, 8, 7.5, 8),
            Block.box(1, 7.5, 8.5, 8, 8, 9),
            Block.box(1, 7.75, 7.5, 8, 8.25, 8),
            Block.box(1, 8.25, 8, 8, 8.75, 8.5),
            Block.box(1, 8, 7, 8, 8.5, 7.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    private static final VoxelShape UP_SHAPE = Stream.of(
            Block.box(6.25, 15.4875, 9.2625, 9.25, 15.9875, 9.7625),
            Block.box(6.25, 15.4875, 6.2625, 6.75, 15.9875, 9.2625),
            Block.box(6.75, 15.4875, 6.2625, 9.75, 15.9875, 6.7625),
            Block.box(9.25, 15.4875, 6.7625, 9.75, 15.9875, 9.7625),
            Block.box(6.75, 14.9875, 6.7625, 8.25, 15.4875, 7.7625),
            Block.box(6.75, 14.9875, 7.7625, 7.75, 15.4875, 9.2625),
            Block.box(7.75, 14.9875, 8.2625, 9.25, 15.4875, 9.2625),
            Block.box(8.25, 14.9875, 6.7625, 9.25, 15.4875, 8.2625),
            Block.box(6.25, 7.9875, 6.2625, 6.5, 15.4875, 6.7625),
            Block.box(6.25, 7.9875, 8.9875, 6.5, 15.4875, 9.4875),
            Block.box(6.25, 7.9875, 9.4875, 9.5, 15.4875, 9.7375),
            Block.box(9.5, 7.9875, 9.2375, 9.75, 15.4875, 9.7375),
            Block.box(6.5, 7.9875, 6.2625, 9.75, 15.4875, 6.5125),
            Block.box(9.5, 7.9875, 6.5125, 9.75, 15.4875, 7.0125),
            Block.box(9.5, 7.9875, 7.2625, 9.75, 15.4875, 7.5125),
            Block.box(9.5, 7.9875, 7.7625, 9.75, 15.4875, 8.0125),
            Block.box(9.5, 7.9875, 8.2625, 9.75, 15.4875, 8.5125),
            Block.box(9.5, 7.9875, 8.7625, 9.75, 15.4875, 9.0125),
            Block.box(6.25, 7.9875, 7.0125, 6.5, 15.4875, 7.2625),
            Block.box(6.25, 7.9875, 7.5125, 6.5, 15.4875, 7.7625),
            Block.box(6.25, 7.9875, 8.0125, 6.5, 15.4875, 8.2625),
            Block.box(6.25, 7.9875, 8.5125, 6.5, 15.4875, 8.7625),
            Block.box(8, 7.9875, 7.0125, 8.5, 14.9875, 7.5125),
            Block.box(7, 7.9875, 7.5125, 7.5, 14.9875, 8.0125),
            Block.box(8, 7.9875, 7.7625, 8.5, 14.9875, 8.2625),
            Block.box(7.5, 7.9875, 8.2625, 8, 14.9875, 8.7625),
            Block.box(8.5, 7.9875, 8.0125, 9, 14.9875, 8.5125)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    private static final VoxelShape DOWN_SHAPE = Stream.of(
            Block.box(6.25, -0.0125, 9.2625, 9.25, 0.4875, 9.7625),
            Block.box(7.75, 0.4875, 8.2625, 9.25, 0.9875, 9.2625),
            Block.box(6.75, 0.4875, 6.7625, 8.25, 0.9875, 7.7625),
            Block.box(6.75, 0.4875, 7.7625, 7.75, 0.9875, 9.2625),
            Block.box(8.25, 0.4875, 6.7625, 9.25, 0.9875, 8.2625),
            Block.box(6.25, -0.0125, 6.2625, 6.75, 0.4875, 9.2625),
            Block.box(6.75, -0.0125, 6.2625, 9.75, 0.4875, 6.7625),
            Block.box(9.25, -0.0125, 6.7625, 9.75, 0.4875, 9.7625),
            Block.box(6.25, 0.4875, 6.2625, 6.5, 7.9875, 6.7625),
            Block.box(6.25, 0.4875, 8.9875, 6.5, 7.9875, 9.4875),
            Block.box(6.25, 0.4875, 9.4875, 9.5, 7.9875, 9.7375),
            Block.box(9.5, 0.4875, 9.2375, 9.75, 7.9875, 9.7375),
            Block.box(6.5, 0.4875, 6.2625, 9.75, 7.9875, 6.5125),
            Block.box(9.5, 0.4875, 6.5125, 9.75, 7.9875, 7.0125),
            Block.box(9.5, 0.4875, 7.2625, 9.75, 7.9875, 7.5125),
            Block.box(9.5, 0.4875, 7.7625, 9.75, 7.9875, 8.0125),
            Block.box(9.5, 0.4875, 8.2625, 9.75, 7.9875, 8.5125),
            Block.box(9.5, 0.4875, 8.7625, 9.75, 7.9875, 9.0125),
            Block.box(6.25, 0.4875, 7.0125, 6.5, 7.9875, 7.2625),
            Block.box(6.25, 0.4875, 7.5125, 6.5, 7.9875, 7.7625),
            Block.box(6.25, 0.4875, 8.0125, 6.5, 7.9875, 8.2625),
            Block.box(6.25, 0.4875, 8.5125, 6.5, 7.9875, 8.7625),
            Block.box(8, 1.4875, 7.0125, 8.5, 7.9875, 7.5125),
            Block.box(7, 1.4875, 7.5125, 7.5, 7.9875, 8.0125),
            Block.box(8, 1.4875, 7.7625, 8.5, 7.9875, 8.2625),
            Block.box(7.5, 1.4875, 8.2625, 8, 7.9875, 8.7625),
            Block.box(8.5, 1.4875, 8.0125, 9, 7.9875, 8.5125)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape UNCONNECTED_SHAPE = Block.box(5.5, 5.5, 5.5, 10.5, 10.5, 10.5);

    private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new ConcurrentHashMap<>();
    
    public InformationTransmissionCableBlock(Properties pProperties) {
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

        if (neighborState.getBlock() instanceof SolutionDeliveryPipelineBlock ||
                neighborState.getBlock() instanceof ElectricEnergyTransmissionPipelineBlock ||
                neighborState.getBlock() instanceof InformationTransmissionCableBlock)
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
