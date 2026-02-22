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

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class PrimaryElectricEnergyTransmissionPipeline extends Block {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    // 未连接时的大方块碰撞箱
    private static final VoxelShape UNCONNECTED_SHAPE = Block.box(5.5, 5.5, 5.5, 10.5, 10.5, 10.5);

    // 【常量定义】可连接的方块列表 - 在这里添加自定义方块
    public static final List<Supplier<Block>> CONNECTABLE_BLOCKS = Arrays.asList(
            () -> ForgingAndCraftingBlocks.ROCK_CRUSHER.get()
    );

    public PrimaryElectricEnergyTransmissionPipeline(Properties properties) {
        super(properties);
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
        return state.getValue(NORTH) || state.getValue(SOUTH) ||
                state.getValue(EAST) || state.getValue(WEST) ||
                state.getValue(UP) || state.getValue(DOWN);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.getValue(CONNECTED)) {
            return UNCONNECTED_SHAPE;
        }
        return getConnectedShape(state);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (!state.getValue(CONNECTED)) {
            return UNCONNECTED_SHAPE;
        }
        return getConnectedShape(state);
    }

    private VoxelShape getConnectedShape(BlockState state) {
        var shape = Block.box(6.5, 6.5, 6.5, 9.5, 9.5, 9.5);

        if (state.getValue(NORTH)) shape = Shapes.or(shape, Stream.of(
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

        if (state.getValue(SOUTH)) shape = Shapes.or(shape, Stream.of(
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

        if (state.getValue(EAST)) shape = Shapes.or(shape, Stream.of(
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

        if (state.getValue(WEST)) shape = Shapes.or(shape, Stream.of(
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

        if (state.getValue(UP)) shape = Shapes.or(shape, Stream.of(
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

        if (state.getValue(DOWN)) shape = Shapes.or(shape, Stream.of(
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

        for (var direction : Direction.values()) {
            var neighborPos = pos.relative(direction);
            var neighborState = world.getBlockState(neighborPos);
            var connected = canConnect(neighborState, neighborPos, world, direction);
            state = state.setValue(getConnection(direction), connected);
        }

        boolean hasConnection = hasAnyConnection(state);
        state = state.setValue(CONNECTED, hasConnection);

        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
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

    // 【关键修改】使用常量定义可连接方块
    private boolean canConnect(BlockState neighborState, BlockPos pos, Level world, Direction direction) {
        // 特殊方块直接连接
        if (neighborState.is(Blocks.COMPOSTER))
            return true;

        // 管道之间互相连接
        if (neighborState.getBlock() instanceof PrimaryElectricEnergyTransmissionPipeline)
            return true;

        // 【关键】检查是否在可连接方块列表中
        for (Supplier<Block> blockSupplier : CONNECTABLE_BLOCKS) {
            if (neighborState.is(blockSupplier.get())) {
                return true;
            }
        }

        //检查是否有任意方块实体（作为后备选项）
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