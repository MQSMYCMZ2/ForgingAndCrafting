package com.mqsmycmz.forging_and_crafting.item;

import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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

public class WoodenBucketItem extends BlockItem {
    public WoodenBucketItem(Block block, Properties pProperties) {
        super(block, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = pContext.getPlayer();
        ItemStack stack = pContext.getItemInHand();

        if (state.is(ForgingAndCraftingBlocks.WATER_WOODEN_BUCKET.get())) {
            if (!level.isClientSide) {
                level.setBlock(pos, ForgingAndCraftingBlocks.WOODEN_BUCKET.get().defaultBlockState(), 11);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY,
                        SoundSource.BLOCKS, 1.0F, 1.0F);

                if (player != null && !player.getAbilities().instabuild) {
                    stack.shrink(1);
                    ItemStack emptyBucket = new ItemStack(ForgingAndCraftingItems.WATER_WOODEN_BUCKET_ITEM.get());
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);

        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        if (player.isShiftKeyDown()) {
            return placeWoodenBucketBlock(level, player, pUsedHand, stack, hitResult);
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);

        if (state.getFluidState().is(Fluids.WATER) && state.getFluidState().isSource()) {
            ItemStack filled = new ItemStack(ForgingAndCraftingItems.WATER_WOODEN_BUCKET_ITEM.get());

            if (!level.isClientSide) {
                // 移除水源
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
                level.playSound(null, pos, SoundEvents.BUCKET_FILL,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResultHolder.sidedSuccess(filled, level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

    private InteractionResultHolder<ItemStack> placeWoodenBucketBlock(Level level, Player player,
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
}