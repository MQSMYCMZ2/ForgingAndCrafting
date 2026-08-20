package com.mqsmycmz.forging_and_crafting.item;

import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WaterWoodenBucketItem extends BlockItem {

    public WaterWoodenBucketItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = pContext.getPlayer();
        ItemStack stack = pContext.getItemInHand();

        if (state.is(ForgingAndCraftingBlocks.WOODEN_BUCKET.get())) {
            if (!level.isClientSide) {
                level.setBlock(pos, ForgingAndCraftingBlocks.WATER_WOODEN_BUCKET.get().defaultBlockState(), 11);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY,
                        SoundSource.BLOCKS, 1.0F, 1.0F);

                if (player != null && !player.getAbilities().instabuild) {
                    stack.shrink(1);
                    ItemStack emptyBucket = new ItemStack(ForgingAndCraftingItems.WOODEN_BUCKET_ITEM.get());
                    if (stack.isEmpty()) {
                        player.setItemInHand(pContext.getHand(), emptyBucket);
                    } else {
                        if (!player.addItem(emptyBucket)) {
                            player.drop(emptyBucket, false);
                        }
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        BlockHitResult hitResult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        if (pPlayer.isShiftKeyDown()) {
            return placeWaterWoodenBucketBlock(pLevel, pPlayer, pUsedHand, stack, hitResult);
        }

        return placeWater(pLevel, pPlayer, pUsedHand, stack, hitResult);
    }

    private InteractionResultHolder<ItemStack> placeWaterWoodenBucketBlock(Level level, Player player,
                                                                           InteractionHand hand,
                                                                           ItemStack stack, BlockHitResult hitResult) {
        BlockPlaceContext placeContext = new BlockPlaceContext(level, player, hand, stack, hitResult);
        BlockPos placePos = placeContext.getClickedPos();

        if (!level.isEmptyBlock(placePos) && !level.getBlockState(placePos).canBeReplaced(placeContext)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            BlockState placeState = this.getBlock().getStateForPlacement(placeContext);
            if (placeState == null) {
                placeState = this.getBlock().defaultBlockState();
            }

            if (!placeBlock(placeContext, placeState)) {
                return InteractionResultHolder.fail(stack);
            }

            level.playSound(null, placePos, placeState.getSoundType().getPlaceSound(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        if (player.getAbilities().instabuild) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.sidedSuccess(ItemStack.EMPTY, level.isClientSide);
    }

    private InteractionResultHolder<ItemStack> placeWater(Level level, Player player,
                                                          InteractionHand hand, ItemStack stack, BlockHitResult hitResult) {
        BlockPos clickedPos = hitResult.getBlockPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        // 判断视线命中的是否为静止水源
        boolean isWaterSource = clickedState.getFluidState().is(Fluids.WATER)
                && clickedState.getFluidState().isSource();

        BlockPos placePos;
        if (isWaterSource) {
            // 面对静止水源：倒回同一格（位置不变，视觉上无变化，但会消耗水桶）
            placePos = clickedPos;
        } else {
            // 面对固体方块：原版逻辑，放在点击面的外侧相邻位置
            Direction direction = hitResult.getDirection();
            placePos = clickedPos.relative(direction);
        }

        // 只有面对非水源时才检查目标位置是否可放置
        // 面对水源时强制允许（即使那里已经有水，也要消耗水桶）
        if (!isWaterSource) {
            BlockState stateAtPlacePos = level.getBlockState(placePos);
            boolean canPlace = level.isEmptyBlock(placePos)
                    || stateAtPlacePos.canBeReplaced(new BlockPlaceContext(level, player, hand, stack, hitResult))
                    || (stateAtPlacePos.getFluidState().is(Fluids.WATER) && !stateAtPlacePos.getFluidState().isSource());
            if (!canPlace) {
                return InteractionResultHolder.fail(stack);
            }
        }

        if (!level.isClientSide) {
            // 强制放置水源（面对水源时相当于"刷新"同一格）
            if (!level.setBlock(placePos, Blocks.WATER.defaultBlockState(), 11)) {
                return InteractionResultHolder.fail(stack);
            }
            level.playSound(null, placePos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if (player.getAbilities().instabuild) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        ItemStack emptyBucket = new ItemStack(ForgingAndCraftingItems.WOODEN_BUCKET_ITEM.get());
        return InteractionResultHolder.sidedSuccess(emptyBucket, level.isClientSide);
    }
}