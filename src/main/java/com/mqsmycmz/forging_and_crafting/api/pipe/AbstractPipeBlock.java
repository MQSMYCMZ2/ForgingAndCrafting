package com.mqsmycmz.forging_and_crafting.api.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPipeBlock extends Block implements IPipeBlock {
    public final VoxelShape UNCONNECTED_SHAPE;

    public final PipeShapeCache shapeCache;

    public AbstractPipeBlock(Properties properties, VoxelShape unconnectedShape) {
        super(properties);
        this.shapeCache = new PipeShapeCache(this::createConnectedShape);
        this.UNCONNECTED_SHAPE = unconnectedShape;

        this.registerDefaultState(getStateDefinition().any()
                .setValue(CONNECTED, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    protected abstract VoxelShape createConnectedShape(BlockState state);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getCollisionShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return !pState.getValue(CONNECTED) ? UNCONNECTED_SHAPE : shapeCache.get(pState);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return !pState.getValue(CONNECTED) ? UNCONNECTED_SHAPE : shapeCache.get(pState);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        var world = pContext.getLevel();
        var pos = pContext.getClickedPos();
        var state = this.defaultBlockState();

        for (var direction : Direction.values()) {
            var neighborPos = pos.relative(direction);
            var neighborState = world.getBlockState(neighborPos);
            var connected = canPipeConnect(neighborState, neighborPos, world, direction);
            state = state.setValue(IPipeBlock.getConnection(direction), connected);
        }

        return state.setValue(CONNECTED, IPipeBlock.hasAnyConnection(state));
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
            var connected = canPipeConnect(s, p, level, dir);
            var connectionProperty = IPipeBlock.getConnection(dir);

            if (state.getValue(connectionProperty) != connected) {
                state = state.setValue(connectionProperty, connected);
            }
        }

        return state.setValue(CONNECTED, IPipeBlock.hasAnyConnection(state));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);

        var newState = calculateConnections(state, world, pos);
        newState = newState.setValue(CONNECTED, IPipeBlock.hasAnyConnection(newState));

        if (newState != state) {
            world.setBlock(pos, newState, 2);
        }
    }

    private BlockState calculateConnections(BlockState state, Level world, BlockPos pos) {
        for (var dir : Direction.values()) {
            var p = pos.relative(dir);
            var s = world.getBlockState(p);
            var connected = canPipeConnect(s, p, world, dir);
            state = state.setValue(IPipeBlock.getConnection(dir), connected);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }
}
