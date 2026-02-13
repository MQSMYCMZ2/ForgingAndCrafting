package com.mqsmycmz.forging_and_crafting.block;

import com.mqsmycmz.forging_and_crafting.block.entity.CarrierDishBlockEntity;
import com.mqsmycmz.forging_and_crafting.block.entity.ForgingAndCraftingBlockEntities;
import com.mqsmycmz.forging_and_crafting.data.OreProcessingDataLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CarrierDishBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // 注意：VALID_ORES 硬编码列表已移除，现在使用 OreProcessingDataLoader.isValidOre()

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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CarrierDishBlockEntity dishEntity)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        // 如果是凿子，交给凿子处理
        if (heldItem.getItem() instanceof com.mqsmycmz.forging_and_crafting.item.ChiselItem) {
            return InteractionResult.PASS; // 让凿子的useOn处理
        }

        // 放置矿石 - 使用数据加载器检查是否是有效矿石
        if (OreProcessingDataLoader.getInstance().isValidOre(heldItem.getItem())) {
            // 如果正在凿，不能更换
            if (dishEntity.isChiseling()) {
                return InteractionResult.PASS;
            }

            if (dishEntity.hasItem()) {
                dropDisplayedItem(level, pos, dishEntity.getDisplayedItem());
            }

            ItemStack toPlace = heldItem.copy();
            toPlace.setCount(1);
            dishEntity.setDisplayedItem(toPlace);

            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }

            return InteractionResult.CONSUME;
        }

        // 空手取出
        if (heldItem.isEmpty() && dishEntity.hasItem()) {
            // 如果正在凿，不能取出
            if (dishEntity.isChiseling()) {
                return InteractionResult.PASS;
            }

            ItemStack displayed = dishEntity.getDisplayedItem();
            dishEntity.clearItem();

            if (!player.getInventory().add(displayed)) {
                dropDisplayedItem(level, pos, displayed);
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    private void dropDisplayedItem(Level level, BlockPos pos, ItemStack stack) {
        if (level.isClientSide || stack.isEmpty()) {
            return;
        }

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack.copy());
        itemEntity.setDefaultPickUpDelay();
        itemEntity.setDeltaMovement(
                level.random.nextGaussian() * 0.05,
                0.2,
                level.random.nextGaussian() * 0.05
        );
        level.addFreshEntity(itemEntity);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CarrierDishBlockEntity(pos, state);
    }

    // 添加Ticker支持
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ForgingAndCraftingBlockEntities.CARRIER_DISH.get(),
                CarrierDishBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CarrierDishBlockEntity dishEntity) {
                if (dishEntity.hasItem()) {
                    // 检查是否是可以凿的矿石
                    if (dishEntity.hasChiselerOre() && dishEntity.getRemainingHeight() < CarrierDishBlockEntity.MAX_HEIGHT) {
                        // 已经凿过，掉落粗矿物品
                        int dropCount = dishEntity.getRemainingRawOreCount();
                        Item rawOreItem = dishEntity.getRawOreItem();

                        if (rawOreItem != null && dropCount > 0) {
                            ItemStack dropStack = new ItemStack(rawOreItem, dropCount);
                            dropDisplayedItem(level, pos, dropStack);
                        }
                        // 如果dropCount为0，不掉落任何东西（矿石被完全凿碎了）
                    } else {
                        // 未凿过或是非矿石，掉落原物品
                        dropDisplayedItem(level, pos, dishEntity.getDisplayedItem());
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void dropBlock(ServerLevel level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CarrierDishBlockEntity dishEntity && dishEntity.hasItem()) {
            dropDisplayedItem(level, pos, dishEntity.getDisplayedItem());
            dishEntity.clearItem();
        }

        level.destroyBlock(pos, false);
        Block.dropResources(state, level, pos, null, null, ItemStack.EMPTY);
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
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return Block.canSupportCenter(pLevel, pPos.below(), Direction.UP);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);

        if (pFromPos.equals(pPos.below())) {
            if (!pLevel.isClientSide && !pState.canSurvive(pLevel, pPos)) {
                dropBlock((ServerLevel) pLevel, pPos, pState);
            }
        }
    }
}