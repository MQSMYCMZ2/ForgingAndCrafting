package com.mqsmycmz.forging_and_crafting.block;

import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import com.mqsmycmz.forging_and_crafting.item.GraphitePowderItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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

public class WaterWoodenBucketBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final IntegerProperty CLICKS = IntegerProperty.create("clicks", 0, 9);

    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);

    public static final VoxelShape SHAPE_BASE = Shapes.join(Stream.of(
            Block.box(3, 0.5, 2, 14, 13.5, 2.5),
            Block.box(2.5, 0.5, 2.5, 3, 13.5, 13.5),
            Block.box(3, 0.5, 13.5, 14, 13.5, 14),
            Block.box(3, 0, 2.5, 14, 0.5, 13.5),
            Block.box(14, 0.5, 2.5, 14.5, 13.5, 13.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), Block.box(3, 0.5, 2.5, 14, 12.5, 13.5), BooleanOp.OR);

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

    public WaterWoodenBucketBlock(Properties pProperties) {
        super(pProperties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(STAGE, 0)
                .setValue(CLICKS, 0));
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
        pBuilder.add(FACING, STAGE, CLICKS);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BASE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BASE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return SHAPE_BASE;
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

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack handItem = pPlayer.getItemInHand(pHand);

        if (handItem.getItem() instanceof GraphitePowderItem) {
            if (!pLevel.isClientSide) {
                int stage = pState.getValue(STAGE);
                if (stage < 5) {

                    if (!pPlayer.getAbilities().instabuild) {
                        handItem.shrink(1);
                        if (handItem.isEmpty()) {
                            pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
                        }
                    }

                    ItemStack pureGraphite = new ItemStack(ForgingAndCraftingItems.PURE_GRAPHITE_POWDER.get());
                    if (!pPlayer.addItem(pureGraphite)) {
                        pPlayer.drop(pureGraphite, false);
                    }

                    int clicks = pState.getValue(CLICKS);

                    clicks++;
                    if (clicks >= 10) {
                        clicks = 0;
                        if (stage < 5) {
                            stage++;
                        }
                    }
                    BlockState newState = pState.setValue(STAGE, stage).setValue(CLICKS, clicks);
                    pLevel.setBlock(pPos, newState, Block.UPDATE_ALL);
                } else {
                    pPlayer.displayClientMessage(Component.translatable("message.forging_and_crafting.water_wooden_bucket_stage_5"),
                            true);
                }
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        List<ItemStack> drops = new ArrayList<>();
        int stage = pState.getValue(STAGE);
        if (stage > 0) {
            drops.add(new ItemStack(ForgingAndCraftingItems.WOODEN_BUCKET_ITEM.get()));
        } else {
            drops.add(new ItemStack(this));
        }
        return drops;
    }
}