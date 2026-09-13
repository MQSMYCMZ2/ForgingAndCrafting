package com.mqsmycmz.forging_and_crafting.block;

import com.mqsmycmz.forging_and_crafting.block.entity.ForgingAndCraftingBlockEntities;
//import com.mqsmycmz.forging_and_crafting.block.entity.ShapingTableBlockEntity;
import com.mqsmycmz.forging_and_crafting.block.entity.ShapingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ShapingTableBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE_BASE = Stream.of(
            Block.box(2.5, 0, 2.5, 13.5, 2.25, 13.5),
            Block.box(1.5, 0, 2.5, 2.5, 1.25, 14.5),
            Block.box(13.5, 0, 1.5, 14.5, 1.25, 13.5),
            Block.box(1.5, 0, 1.5, 13.5, 1.25, 2.5),
            Block.box(2.5, 0, 13.5, 14.5, 1.25, 14.5),
            Block.box(14.5, 0, 1.5, 15.5, 0.75, 15.5),
            Block.box(0.5, 0, 14.5, 14.5, 0.75, 15.5),
            Block.box(0.5, 0, 0.5, 1.5, 0.75, 14.5),
            Block.box(1.5, 0, 0.5, 15.5, 0.75, 1.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public ShapingTableBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BASE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

        @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ShapingTableBlockEntity(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ShapingTableBlockEntity) {
                ((ShapingTableBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof ShapingTableBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer) pPlayer), (ShapingTableBlockEntity) entity, pPos);
            } else {
                throw new IllegalStateException("Our container provider is missing -- from RockCrusherBlock");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ForgingAndCraftingBlockEntities.SHAPING_TABLE.get(),
                ((pLevel1, pPos, pState1, pBlockEntity) ->
                        pBlockEntity.tick(pLevel1, pPos, pState1)));
    }
}
