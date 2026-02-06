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

        ItemStack inputStack = pBlockEntity.itemStackHandler.getStackInSlot(0);
        if (inputStack.isEmpty()) return;

        Direction facing = pBlockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        Direction left = facing.getClockWise();
        Direction right = facing.getCounterClockWise();

        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 0.5f, 0.5f);

        pPoseStack.pushPose();
        offsetToFace(pPoseStack, left);
        renderItem(inputStack, pPoseStack, pBuffer, level, pPackedLight, pPackedOverlay);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        offsetToFace(pPoseStack, right);
        renderItem(inputStack, pPoseStack, pBuffer, level, pPackedLight, pPackedOverlay);
        pPoseStack.popPose();

        pPoseStack.popPose();
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

    /* 把坐标系原点移到某个面的中心，并旋转到面朝玩家 */
    private static void offsetToFace(PoseStack poseStack, Direction face) {
        float offset = 0.51f;   // 稍微离墙一点，防止 z-fight
        switch (face) {
            case EAST  -> { poseStack.translate( offset, 0, 0);
                            poseStack.mulPose(Axis.YP.rotationDegrees(-90)); }
            case WEST  -> { poseStack.translate(-offset, 0, 0);
                            poseStack.mulPose(Axis.YP.rotationDegrees( 90)); }
            case SOUTH -> { poseStack.translate(0, 0,  offset);
                            poseStack.mulPose(Axis.YP.rotationDegrees(180)); }
            case NORTH -> { poseStack.translate(0, 0, -offset); /* 0° */ }
            default    -> {}
        }
    }
}
