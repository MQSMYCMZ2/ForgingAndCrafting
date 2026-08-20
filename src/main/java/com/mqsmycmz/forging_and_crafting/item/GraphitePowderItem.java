package com.mqsmycmz.forging_and_crafting.item;

import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class GraphitePowderItem extends Item {
    public GraphitePowderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);

        // 射线检测玩家看向的方块/流体
        HitResult hitResult = pPlayer.pick(5.0D, 0.0F, true);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos pos = blockHit.getBlockPos();
            BlockState state = pLevel.getBlockState(pos);

            // 检测：水方块 / 流动水 / 含水方块
            boolean isWater = state.getFluidState().is(FluidTags.WATER);
            boolean isWaterWoodenBucket = state.is(ForgingAndCraftingBlocks.WATER_WOODEN_BUCKET.get());

            if ((isWater || isWaterWoodenBucket) && !pLevel.isClientSide) {
                if (!pPlayer.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                ItemStack pureGraphite = new ItemStack(ForgingAndCraftingItems.PURE_GRAPHITE_POWDER.get());
                if (!pPlayer.addItem(pureGraphite)) {
                    pPlayer.drop(pureGraphite, false);
                }

                return InteractionResultHolder.success(stack);
            }
        }

        return super.use(pLevel, pPlayer, pHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        // useOn 保留做后备，但主要逻辑已在 use 中
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = pContext.getPlayer();
        ItemStack stack = pContext.getItemInHand();

        boolean isWater = state.getFluidState().is(FluidTags.WATER);
        boolean isWaterlogged = state.hasProperty(BlockStateProperties.WATERLOGGED)
                && state.getValue(BlockStateProperties.WATERLOGGED);

        if ((isWater || isWaterlogged) && player != null) {
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.setItemInHand(pContext.getHand(), ItemStack.EMPTY);
                    }
                }

                ItemStack pureGraphite = new ItemStack(ForgingAndCraftingItems.PURE_GRAPHITE_POWDER.get());
                if (!player.addItem(pureGraphite)) {
                    player.drop(pureGraphite, false);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useOn(pContext);
    }
}