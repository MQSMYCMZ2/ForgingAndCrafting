package com.mqsmycmz.forging_and_crafting.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mqsmycmz.forging_and_crafting.block.entity.RockCrusherBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class RockCrusherBlockEntityRenderer implements BlockEntityRenderer<RockCrusherBlockEntity> {
    private final ItemRenderer itemRenderer;

    public RockCrusherBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(RockCrusherBlockEntity pBlockEntity,
                       float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Level level = pBlockEntity.getLevel();
        if (level == null) return;

        Direction facing = pBlockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        // 渲染输入物品（左右两侧）
        ItemStack inputStack = pBlockEntity.itemStackHandler.getStackInSlot(0);
        if (!inputStack.isEmpty()) {
            Direction left = facing.getClockWise();
            Direction right = facing.getCounterClockWise();

            pPoseStack.pushPose();
            pPoseStack.translate(0.5f, 0.5f, 0.5f);

            pPoseStack.pushPose();
            offsetToFace(pPoseStack, left, true);
            renderItem(inputStack, pPoseStack, pBuffer, level, pPackedLight, pPackedOverlay);
            pPoseStack.popPose();

            pPoseStack.pushPose();
            offsetToFace(pPoseStack, right, false);
            renderItem(inputStack, pPoseStack, pBuffer, level, pPackedLight, pPackedOverlay);
            pPoseStack.popPose();

            pPoseStack.popPose();
        }

        ItemStack recipeResultStack = pBlockEntity.getResultItemForDisplay();
        if (!recipeResultStack.isEmpty()) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5f, 0.5f, 0.5f);
            offsetToTop(pPoseStack, facing);

            renderRecipeResultItem(recipeResultStack, pPoseStack, pBuffer, level, pPackedLight, pPackedOverlay);

            pPoseStack.popPose();
        }
    }

    private void renderItem(ItemStack stack, PoseStack poseStack,
                            MultiBufferSource bufferSource, Level level, int light, int overlay) {
        float scale = 14 / 16f;
        poseStack.scale(scale, scale, scale);

        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                light, overlay, poseStack, bufferSource, level, 0);
    }

    private void renderRecipeResultItem(ItemStack stack, PoseStack poseStack,
                                        MultiBufferSource bufferSource, Level level,
                                        int light, int overlay) {
        float scale = 14 / 16f;
        poseStack.scale(scale, scale, scale);

        float time = level.getGameTime() + Minecraft.getInstance().getFrameTime();
        poseStack.mulPose(Axis.ZP.rotationDegrees(time * 4));

        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                light, overlay, poseStack, bufferSource, level, 0);
    }

    private static void offsetToFace(PoseStack poseStack, Direction face, boolean isLeft) {
        float baseOffset = 0.35f;
        switch (face) {
            case EAST  -> {
                poseStack.translate( baseOffset, 0, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
            }
            case WEST  -> {
                poseStack.translate(-baseOffset, 0, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees( 270));
            }
            case SOUTH -> {
                poseStack.translate(0, 0,  baseOffset);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case NORTH -> {
                poseStack.translate(0, 0, -baseOffset);
            }
            default    -> {

            }
        }
    }

    private static void offsetToTop(PoseStack ms, Direction facing) {
        ms.translate(0, 0.51, 0);
        ms.mulPose(Axis.XP.rotationDegrees(-90));
        switch (facing) {
            case EAST -> ms.mulPose(Axis.ZP.rotationDegrees(-90));
            case WEST -> ms.mulPose(Axis.ZP.rotationDegrees(90));
            case SOUTH -> ms.mulPose(Axis.ZP.rotationDegrees(180));
            case NORTH -> ms.mulPose(Axis.ZP.rotationDegrees(0));
            default -> {}
        }
    }
}