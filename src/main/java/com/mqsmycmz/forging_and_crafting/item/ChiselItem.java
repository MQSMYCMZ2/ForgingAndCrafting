package com.mqsmycmz.forging_and_crafting.item;

import com.mqsmycmz.forging_and_crafting.block.entity.CarrierDishBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ChiselItem extends Item {
    public ChiselItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        InteractionHand hand = pContext.getHand();

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
            dishBlockEntity.startChiseling(player, hand);
        }

        return InteractionResult.CONSUME;
    }
}