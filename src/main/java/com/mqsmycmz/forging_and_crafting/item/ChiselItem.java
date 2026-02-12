package com.mqsmycmz.forging_and_crafting.item;

import com.mqsmycmz.forging_and_crafting.block.entity.CarrierDishBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChiselItem extends Item {
    public static final String NBT_SHARPNESS = "Sharpness";

    public static final int BASE_SHARPNESS = 80;
    public static final int BASE_DURABILITY = 500;
    public static final int BASE_GRANULES_DROP = 3;
    // 每次凿矿增加的尖锐程度
    public static final int SHARPNESS_PER_CHISEL = 1;
    // 每次凿矿消耗的耐久
    public static final int DURABILITY_COST_PER_CHISEL = 1;

    public ChiselItem(Properties pProperties) {
        super(pProperties.stacksTo(1).durability(BASE_DURABILITY));
    }

    public static int getSharpness(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_SHARPNESS)) {
            return tag.getInt(NBT_SHARPNESS);
        }

        setSharpness(stack, BASE_SHARPNESS);
        return BASE_SHARPNESS;
    }

    public static void setSharpness(ItemStack stack, int sharpness) {
        stack.getOrCreateTag().putInt(NBT_SHARPNESS, sharpness);
    }

    /**
     * 增加尖锐程度（完成凿矿后调用）
     * @return 新的尖锐程度值
     */
    public static int increaseSharpness(ItemStack stack) {
        int currentSharpness = getSharpness(stack);
        int newSharpness = currentSharpness + SHARPNESS_PER_CHISEL;
        setSharpness(stack, newSharpness);
        return newSharpness;
    }

    public static int calculateGranulesDrop(int sharpness) {
        int bonus = (sharpness - BASE_SHARPNESS) / 2;
        return BASE_GRANULES_DROP + bonus;
    }

    public static int calculateMaxDurability(int sharpness) {
        return Math.max(1, -sharpness + BASE_DURABILITY);
    }

    public static ItemStack createChiselWithSharpness(int sharpness) {
        ItemStack stack = new ItemStack(ForgingAndCraftingItems.CHISEL.get());
        setSharpness(stack, sharpness);
        return stack;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        InteractionHand hand = pContext.getHand();
        ItemStack stack = pContext.getItemInHand();

        BlockEntity entity = level.getBlockEntity(pos);

        if (!(entity instanceof CarrierDishBlockEntity dishBlockEntity)) {
            return InteractionResult.PASS;
        }

        // 检查是否有可凿的矿石
        if (!dishBlockEntity.hasChiselerOre()) {
            return InteractionResult.PASS;
        }

        // 检查是否已经凿完了
        if (dishBlockEntity.getRemainingHeight() <= 0) {
            return InteractionResult.PASS;
        }

        // 客户端立即返回
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // 服务端处理：根据当前状态开始或停止
        if (dishBlockEntity.isChiseling()) {
            // 正在凿，停止
            dishBlockEntity.stopChiseling();
        } else {
            // 没有在凿，开始
            int sharpness = getSharpness(stack);
            dishBlockEntity.startChiseling(player, hand, sharpness, stack);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        int sharpness = getSharpness(stack);
        int dropCount = calculateGranulesDrop(sharpness);
        int maxDurability = calculateMaxDurability(sharpness);
        int currentDamage = stack.getDamageValue();
        int remainingDurability = maxDurability - currentDamage;

        tooltip.add(Component.translatable("tooltip.forging_and_crafting.chisel.sharpness", sharpness)
                .withStyle(net.minecraft.ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.forging_and_crafting.chisel.granules_drop", dropCount)
                .withStyle(net.minecraft.ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("tooltip.forging_and_crafting.chisel.durability",
                        remainingDurability, maxDurability)
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.forging_and_crafting.chisel.auto_sharpen_hint")
                .withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
    }
}