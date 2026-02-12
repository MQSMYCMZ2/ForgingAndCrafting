package com.mqsmycmz.forging_and_crafting.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mqsmycmz.forging_and_crafting.block.entity.CarrierDishBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CarrierDishBlockEntityRenderer implements BlockEntityRenderer<CarrierDishBlockEntity> {

    // 凿子物品（用于渲染）
    private static final ItemStack CHISEL_STACK = new ItemStack(
            com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems.CHISEL.get()
    );

    public CarrierDishBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CarrierDishBlockEntity pBlockEntity,
                       float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer,
                       int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.isRemoved()) {
            return;
        }

        ItemStack displayedItem = pBlockEntity.getDisplayedItem();

        if (displayedItem == null || displayedItem.isEmpty()) {
            return;
        }

        // 渲染矿石方块（带高度缩放）
        renderOreBlock(pBlockEntity, displayedItem, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);

        // 如果正在凿，渲染凿子动画
        if (pBlockEntity.isChiseling()) {
            renderChiselAnimation(pBlockEntity, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pPartialTick);
        }
    }

    private void renderOreBlock(CarrierDishBlockEntity pBlockEntity, ItemStack displayedItem,
                                PoseStack pPoseStack, MultiBufferSource pBuffer,
                                int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(displayedItem, pBlockEntity.getLevel(), null, 0);

        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 0.5f, 0.5f);

        // 根据剩余高度缩放（8个阶段，每个阶段降低1/8）
        int remainingHeight = pBlockEntity.getRemainingHeight();
        float maxHeight = 8.0f;
        float heightScale = remainingHeight / maxHeight;

        // 基础缩放
        float baseScale = 14f / 16f;
        pPoseStack.scale(baseScale, baseScale * heightScale, baseScale);

        // 旋转动画（空闲时缓慢旋转）
        long time = System.currentTimeMillis();
        float angle = (time % 3600) / 10f;
        pPoseStack.mulPose(Axis.YP.rotationDegrees(angle));

        itemRenderer.render(displayedItem, ItemDisplayContext.FIXED, false, pPoseStack,
                pBuffer, pPackedLight, pPackedOverlay, bakedModel);

        pPoseStack.popPose();
    }

    private void renderChiselAnimation(CarrierDishBlockEntity pBlockEntity,
                                       PoseStack pPoseStack, MultiBufferSource pBuffer,
                                       int pPackedLight, int pPackedOverlay, float pPartialTick) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel chiselModel = itemRenderer.getModel(CHISEL_STACK, pBlockEntity.getLevel(), null, 0);

        pPoseStack.pushPose();

        // 获取玩家方向
        Direction playerDir = pBlockEntity.getPlayerDirection();

        // 根据方向设置凿子的基础位置和旋转
        float yRotation = getRotationFromDirection(playerDir);

        // 移动到方块中心
        pPoseStack.translate(0.5f, 0.8f, 0.5f);

        // 根据玩家方向旋转
        pPoseStack.mulPose(Axis.YP.rotationDegrees(yRotation));

        // 计算凿子前后移动的动画
        int animTick = pBlockEntity.getAnimationTick();
        float animProgress = (animTick + pPartialTick) % 20f / 20f;

        // 前后移动：从0.6（远离）到0.3（靠近）之间移动
        float moveDistance = 0.6f - (float) Math.sin(animProgress * Math.PI * 2) * 0.15f;

        // 凿子倾斜角度
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(-30f));

        // 移动到凿子位置（沿着当前朝向的方向）
        pPoseStack.translate(moveDistance, 0, 0);

        // 凿子大小
        pPoseStack.scale(0.5f, 0.5f, 0.5f);

        // 渲染凿子
        itemRenderer.render(CHISEL_STACK, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, pPoseStack,
                pBuffer, pPackedLight, pPackedOverlay, chiselModel);

        pPoseStack.popPose();
    }

    // 根据方向获取Y轴旋转角度
    private float getRotationFromDirection(Direction direction) {
        return switch (direction) {
            case SOUTH -> 0f;
            case WEST -> 270f;
            case NORTH -> 180f;
            case EAST -> 90f;
            default -> 0f;
        };
    }
}