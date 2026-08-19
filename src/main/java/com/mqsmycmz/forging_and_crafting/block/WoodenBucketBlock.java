package com.mqsmycmz.forging_and_crafting.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class WoodenBucketBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final VoxelShape SHAPE_BASE = Stream.of(
            Block.box(3, 0.5, 2.5, 14, 13.5, 3),
            Block.box(2.5, 0.5, 3, 3, 13.5, 13),
            Block.box(3, 0.5, 13, 14, 13.5, 13.5),
            Block.box(3, 0, 3, 14, 0.5, 13),
            Block.box(14, 0.5, 3, 14.5, 13.5, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        SHAPES.put(Direction.NORTH, SHAPE_BASE);
        SHAPES.put(Direction.EAST, rotateShapeClockwise(SHAPE_BASE, 1));
        SHAPES.put(Direction.SOUTH, rotateShapeClockwise(SHAPE_BASE, 2));
        SHAPES.put(Direction.WEST, rotateShapeClockwise(SHAPE_BASE,3));
    }

    private static VoxelShape rotateShapeClockwise(VoxelShape shape, int times) {
        if (times == 0) return shape;

        VoxelShape result = shape;
        for (int i = 0; i < times; i++) {
            List<AABB> boxes = new ArrayList<>();
            result.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                double pxMinX = minX * 16.0;
                double pxMaxX = maxX * 16.0;
                double pxMinZ = minZ * 16.0;
                double pxMaxZ = maxZ * 16.0;

                double newPxMinX = 16.0 - pxMaxZ;
                double newPxMaxX = 16.0 - pxMinZ;
                double newPxMinZ = pxMinX;
                double newPxMaxZ = pxMaxX;

                boxes.add(new AABB(
                        newPxMinX / 16.0, minY, newPxMinZ / 16.0,
                        newPxMaxX / 16.0, maxY, newPxMaxZ / 16.0
                ));
            });
            result = boxes.stream()
                    .map(Shapes::create)
                    .reduce(Shapes.empty(), (a, b) -> Shapes.join(a, b, BooleanOp.OR));
        }
        return result;
    }

    public WoodenBucketBlock(Properties pProperties) {
        super(pProperties);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction facing = pContext.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES.getOrDefault(pState.getValue(FACING), SHAPE_BASE);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES.getOrDefault(pState.getValue(FACING), SHAPE_BASE);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return SHAPES.getOrDefault(pState.getValue(FACING), SHAPE_BASE);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return false;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }
}
