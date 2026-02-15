package com.mqsmycmz.forging_and_crafting.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PrimaryElectricEnergyTransmissionPipeline extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    // 只定义基础水平形状（朝北）
    public static final VoxelShape SHAPE_BASE = Stream.of(
            Block.box(6.5, 6.5, 0, 9.5, 9.5, 16),
            Block.box(6.25, 6.5, 0, 6.5, 9.75, 0.5),
            Block.box(9.5, 6.25, 0, 9.75, 9.5, 0.5),
            Block.box(6.5, 9.5, 0, 9.75, 9.75, 0.5),
            Block.box(6.25, 6.25, 0, 9.5, 6.5, 0.5),
            Block.box(6.5, 9.5, 15.5, 9.75, 9.75, 16),
            Block.box(9.5, 6.5, 15.5, 9.75, 9.5, 16),
            Block.box(6.25, 6.5, 15.5, 6.5, 9.75, 16),
            Block.box(6.25, 6.25, 15.5, 9.75, 6.5, 16),
            Block.box(7.25, 6.25, 0.5, 8.75, 6.5, 15.5),
            Block.box(6.25, 7.25, 0.5, 6.5, 8.75, 15.5),
            Block.box(9.5, 7.25, 0.5, 9.75, 8.75, 15.5),
            Block.box(7.25, 9.5, 0.5, 8.75, 9.75, 15.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    // 缓存所有方向的形状
    public static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        // 水平方向：通过Y轴旋转生成
        SHAPES.put(Direction.NORTH, SHAPE_BASE);
        SHAPES.put(Direction.EAST, rotateShapeClockwise(SHAPE_BASE, 1));
        SHAPES.put(Direction.SOUTH, rotateShapeClockwise(SHAPE_BASE, 2));
        SHAPES.put(Direction.WEST, rotateShapeClockwise(SHAPE_BASE, 3));

        // 垂直方向：通过X轴旋转生成（关键！）
        SHAPES.put(Direction.UP, rotateX(SHAPE_BASE, -90));
        SHAPES.put(Direction.DOWN, rotateX(SHAPE_BASE, 90));
    }

    public PrimaryElectricEnergyTransmissionPipeline(Properties properties) {
        super(properties);
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

    /**
     * 绕X轴旋转（用于垂直方向）- 修复版
     */
    private static VoxelShape rotateX(VoxelShape shape, double degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double center = 8.0;

        List<AABB> boxes = new ArrayList<>();
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            // 转换到像素坐标，以中心为原点
            double x1 = minX * 16 - center;
            double x2 = maxX * 16 - center;
            double y1 = minY * 16 - center;
            double y2 = maxY * 16 - center;
            double z1 = minZ * 16 - center;
            double z2 = maxZ * 16 - center;

            // 绕X轴旋转：Y和Z交换
            double newY1 = y1 * cos - z1 * sin;
            double newY2 = y2 * cos - z2 * sin;
            double newZ1 = y1 * sin + z1 * cos;
            double newZ2 = y2 * sin + z2 * cos;

            // 加回中心并转换回世界坐标
            double finalMinX = (Math.min(x1, x2) + center) / 16.0;
            double finalMaxX = (Math.max(x1, x2) + center) / 16.0;
            double finalMinY = (Math.min(newY1, newY2) + center) / 16.0;
            double finalMaxY = (Math.max(newY1, newY2) + center) / 16.0;
            // 关键修复：使用center而不是添加偏移
            double finalMinZ = (Math.min(newZ1, newZ2) + center) / 16.0;
            double finalMaxZ = (Math.max(newZ1, newZ2) + center) / 16.0;

            boxes.add(new AABB(finalMinX, finalMinY, finalMinZ, finalMaxX, finalMaxY, finalMaxZ));
        });

        return combineBoxes(boxes);
    }

    /**
     * 辅助方法：合并所有AABB为VoxelShape
     */
    private static VoxelShape combineBoxes(List<AABB> boxes) {
        return boxes.stream()
                .map(Shapes::create)
                .reduce(Shapes.empty(), (a, b) -> Shapes.join(a, b, BooleanOp.OR));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction facing;

        // 逻辑：管道朝向与点击面相同（指向该方向）
        if (clickedFace.getAxis().isHorizontal()) {
            // 水平面：管道水平放置，朝向该方向
            facing = clickedFace;
        } else {
            // 顶面/底面：管道垂直，朝向上下
            facing = clickedFace;
        }

        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), SHAPE_BASE);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getShape(state, level, pos, CollisionContext.empty());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}