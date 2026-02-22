package com.mqsmycmz.forging_and_crafting.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
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

import java.util.stream.Stream;

public class PrimaryElectricEnergyTransmissionPipeline extends Block {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public static final VoxelShape UNCONNECTED_SHAPE = Block.box(5.5, 5.5, 5.5, 10.5, 10.5, 10.5);

    public PrimaryElectricEnergyTransmissionPipeline(Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any()
                .setValue(CONNECTED, false)  // 默认未连接
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    // 【新增】检查是否有任意方向连接
    public static boolean hasAnyConnection(BlockState state) {
        return state.getValue(NORTH) || state.getValue(SOUTH) ||
                state.getValue(EAST) || state.getValue(WEST) ||
                state.getValue(UP) || state.getValue(DOWN);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    private VoxelShape getConnectShape(BlockState pState) {
        var shape = Block.box(7, 7, 7, 9, 9, 9);

        if (pState.getValue(NORTH)) shape = Shapes.or(shape, Stream.of(
                Block.box(6.5, 6.5, 0, 9.5, 9.5, 8),
                Block.box(6.25, 6.5, 0, 6.5, 9.75, 0.5),
                Block.box(9.5, 6.25, 0, 9.75, 9.5, 0.5),
                Block.box(6.5, 9.5, 0, 9.75, 9.75, 0.5),
                Block.box(6.25, 6.25, 0, 9.5, 6.5, 0.5),
                Block.box(6.25, 7.25, 0.5, 6.5, 8.75, 8),
                Block.box(9.5, 7.25, 0.5, 9.75, 8.75, 8),
                Block.box(7.25, 9.5, 0.5, 8.75, 9.75, 8),
                Block.box(7.25, 6.25, 0.5, 8.75, 6.5, 8)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
        if (pState.getValue(SOUTH)) shape = Shapes.or(shape, Stream.of(
                Block.box(6.5, 6.5, 8, 9.5, 9.5, 16),
                Block.box(6.25, 6.5, 15.5, 6.5, 9.75, 16),
                Block.box(9.5, 6.25, 15.5, 9.75, 9.5, 16),
                Block.box(6.5, 9.5, 15.5, 9.75, 9.75, 16),
                Block.box(6.25, 6.25, 15.5, 9.5, 6.5, 16),
                Block.box(6.25, 7.25, 8, 6.5, 8.75, 15.5),
                Block.box(9.5, 7.25, 8, 9.75, 8.75, 15.5),
                Block.box(7.25, 9.5, 8, 8.75, 9.75, 15.5),
                Block.box(7.25, 6.25, 8, 8.75, 6.5, 15.5)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
        if (pState.getValue(EAST)) shape = Shapes.or(shape, Stream.of(
                Block.box(8, 6.5, 6.5, 16, 9.5, 9.5),
                Block.box(15.5, 6.5, 9.5, 16, 9.75, 9.75),
                Block.box(15.5, 6.25, 6.25, 16, 9.5, 6.5),
                Block.box(15.5, 9.5, 6.25, 16, 9.75, 9.5),
                Block.box(15.5, 6.25, 6.5, 16, 6.5, 9.75),
                Block.box(8, 7.25, 9.5, 15.5, 8.75, 9.75),
                Block.box(8, 7.25, 6.25, 15.5, 8.75, 6.5),
                Block.box(8, 9.5, 7.25, 15.5, 9.75, 8.75),
                Block.box(8, 6.25, 7.25, 15.5, 6.5, 8.75)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
        if (pState.getValue(WEST)) shape = Shapes.or(shape, Stream.of(
                Block.box(0, 6.5, 6.5, 8, 9.5, 9.5),
                Block.box(0, 6.5, 9.5, 0.5, 9.75, 9.75),
                Block.box(0, 6.25, 6.25, 0.5, 9.5, 6.5),
                Block.box(0, 9.5, 6.25, 0.5, 9.75, 9.5),
                Block.box(0, 6.25, 6.5, 0.5, 6.5, 9.75),
                Block.box(0.5, 7.25, 9.5, 8, 8.75, 9.75),
                Block.box(0.5, 7.25, 6.25, 8, 8.75, 6.5),
                Block.box(0.5, 9.5, 7.25, 8, 9.75, 8.75),
                Block.box(0.5, 6.25, 7.25, 8, 6.5, 8.75)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
        if (pState.getValue(UP)) shape = Shapes.or(shape, Stream.of(
                Block.box(6.5, 8, 6.5, 9.5, 16, 9.5),
                Block.box(9.5, 15.5, 6.5, 9.75, 16, 9.75),
                Block.box(6.25, 15.5, 6.25, 6.5, 16, 9.5),
                Block.box(6.25, 15.5, 9.5, 9.5, 16, 9.75),
                Block.box(6.5, 15.5, 6.25, 9.75, 16, 6.5),
                Block.box(6.25, 8, 7.25, 6.5, 15.5, 8.75),
                Block.box(9.5, 8, 7.25, 9.75, 15.5, 8.75),
                Block.box(7.25, 8, 6.25, 8.75, 15.5, 6.5),
                Block.box(7.25, 8, 9.5, 8.75, 15.5, 9.75)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
        if (pState.getValue(DOWN)) shape = Shapes.or(shape, Stream.of(
                Block.box(6.5, 0, 6.5, 9.5, 8, 9.5),
                Block.box(9.5, 0, 6.5, 9.75, 0.5, 9.75),
                Block.box(6.25, 0, 6.25, 6.5, 0.5, 9.5),
                Block.box(6.25, 0, 9.5, 9.5, 0.5, 9.75),
                Block.box(6.5, 0, 6.25, 9.75, 0.5, 6.5),
                Block.box(6.25, 0.5, 7.25, 6.5, 8, 8.75),
                Block.box(9.5, 0.5, 7.25, 9.75, 8, 8.75),
                Block.box(7.25, 0.5, 6.25, 8.75, 8, 6.5),
                Block.box(7.25, 0.5, 9.5, 8.75, 8, 9.75)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());

        return shape;
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
        return getConnectShape(pState);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if (!pState.getValue(CONNECTED)) {
            return UNCONNECTED_SHAPE;
        }
        return getConnectShape(pState);
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var world = context.getLevel();
        var pos = context.getClickedPos();
        var state = this.defaultBlockState();

        // 检查所有方向的连接
        for (var direction : Direction.values()) {
            var neighborPos = pos.relative(direction);
            var neighborState = world.getBlockState(neighborPos);
            var connected = canConnect(neighborState, neighborPos, world, direction);
            state = state.setValue(getConnection(direction), connected);
        }

        // 【修改】设置 CONNECTED 属性
        boolean hasConnection = hasAnyConnection(state);
        state = state.setValue(CONNECTED, hasConnection);

        System.out.println("【放置】Pipe at " + pos + " connected=" + hasConnection +
                " N=" + state.getValue(NORTH) + " S=" + state.getValue(SOUTH));

        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!(world instanceof Level level)) {
            return state;
        }

        // 重新计算所有方向的连接
        for (var dir : Direction.values()) {
            var p = pos.relative(dir);
            var s = world.getBlockState(p);
            var connected = canConnect(s, p, level, dir);
            var connectionProperty = getConnection(dir);

            if (state.getValue(connectionProperty) != connected) {
                state = state.setValue(connectionProperty, connected);
            }
        }

        // 【修改】更新 CONNECTED 属性
        boolean hasConnection = hasAnyConnection(state);
        state = state.setValue(CONNECTED, hasConnection);

        System.out.println("【更新】Pipe at " + pos + " connected=" + hasConnection);

        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);

        var newState = calculateConnections(state, world, pos);

        // 【修改】更新 CONNECTED 属性
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
        // 特殊方块直接连接
        if (neighborState.is(Blocks.END_PORTAL_FRAME) || neighborState.is(Blocks.COMPOSTER))
            return true;

        // 管道之间互相连接
        if (neighborState.getBlock() instanceof PrimaryElectricEnergyTransmissionPipeline)
            return true;

        // 检查是否有容器
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            return blockEntity instanceof Container;
        }

        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // 【修改】使用 CONNECTED 替代 JOINT
        builder.add(CONNECTED, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }
}