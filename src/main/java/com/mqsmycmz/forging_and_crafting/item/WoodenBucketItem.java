package com.mqsmycmz.forging_and_crafting.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WoodenBucketItem extends Item {
    public WoodenBucketItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);

        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
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
}