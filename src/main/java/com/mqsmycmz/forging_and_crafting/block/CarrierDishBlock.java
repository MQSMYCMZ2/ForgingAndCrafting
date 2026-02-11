package com.mqsmycmz.forging_and_crafting.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CarrierDishBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final VoxelShape SHAPE_BASE = Stream.of(
            Block.box(2.25, 0.5, 2.25, 14.25, 1.25, 14.25),
            Block.box(2.25, 1.25, 2.25, 13.25, 2.25, 3.25),
            Block.box(2.25, 1.25, 3.25, 3.25, 2.25, 14.25),
            Block.box(13.25, 1.25, 2.25, 14.25, 2.25, 13.25),
            Block.box(3.25, 1.25, 13.25, 14.25, 2.25, 14.25),
            Block.box(4.25, 0.25, 4.25, 12.25, 0.5, 12.25),
            Block.box(6.25, 0, 6.25, 10.25, 0.25, 10.25),
            Block.box(1.25, 1, 10.5, 2.5, 1.5, 11),
            Block.box(1.25, 1, 5.5, 2.5, 1.5, 6),
            Block.box(14, 1, 10.5, 15.25, 1.5, 11),
            Block.box(14, 1, 5.5, 15.25, 1.5, 6),
            Block.box(10.5, 1, 1, 11, 1.5, 2.25),
            Block.box(5.5, 1, 14.25, 6, 1.5, 15.5),
            Block.box(10.5, 1, 14.25, 11, 1.5, 15.5),
            Block.box(5.5, 1, 1, 6, 1.5, 2.25),
            Block.box(0.75, 1, 5, 1.25, 1.5, 11.5),
            Block.box(5, 1, 0.5, 11.5, 1.5, 1),
            Block.box(5, 1, 15.5, 11.5, 1.5, 16),
            Block.box(15.25, 1, 5, 15.75, 1.5, 11.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        SHAPES.put(Direction.NORTH, SHAPE_BASE);
        SHAPES.put(Direction.EAST, rotateShapeClockwise(SHAPE_BASE, 1));
        SHAPES.put(Direction.SOUTH, rotateShapeClockwise(SHAPE_BASE, 2));
        SHAPES.put(Direction.WEST, rotateShapeClockwise(SHAPE_BASE, 3));
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

    public CarrierDishBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
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
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return Block.canSupportCenter(pLevel, pPos.below(), Direction.UP);
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Object pRandom) {
        if (!pState.canSurvive(pLevel, pPos)) {
            dropBlock(pLevel, pPos, pState);
        }
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);

        // 如果下方的方块发生变化，立即检查
        if (pFromPos.equals(pPos.below())) {
            if (!pLevel.isClientSide && !pState.canSurvive(pLevel, pPos)) {
                dropBlock((ServerLevel) pLevel, pPos, pState);
            }
        }
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
        }
    }

    private void dropBlock(ServerLevel pLevel, BlockPos pPos, BlockState pState) {
        pLevel.destroyBlock(pPos, false);

        ItemStack itemStack = new ItemStack(this.asItem());
        ItemEntity itemEntity = new ItemEntity(
                pLevel,
                pPos.getX() + 0.5,
                pPos.getY() + 0.5,
                pPos.getZ() + 0.5,
                itemStack
        );
        itemEntity.setDefaultPickUpDelay();
        pLevel.addFreshEntity(itemEntity);
    }
}
