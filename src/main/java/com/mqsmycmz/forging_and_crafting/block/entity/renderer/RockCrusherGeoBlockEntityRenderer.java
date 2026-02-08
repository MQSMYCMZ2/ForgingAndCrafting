package com.mqsmycmz.forging_and_crafting.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mqsmycmz.forging_and_crafting.block.entity.RockCrusherBlockEntity;
import com.mqsmycmz.forging_and_crafting.block.entity.renderer.model.RockCrusherModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RockCrusherGeoBlockEntityRenderer extends GeoBlockRenderer<RockCrusherBlockEntity> {
    private final ItemRenderer itemRenderer;
    private Level level;
    private Direction facing;

    public RockCrusherGeoBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new RockCrusherModel());
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void preRender(PoseStack poseStack, RockCrusherBlockEntity animatable,
                          BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable,
                model, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);

        this.level = animatable.getLevel();
        if (this.level != null) {
            this.facing = animatable.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        }
    }

    @Override
    public void postRender(PoseStack poseStack, RockCrusherBlockEntity animatable,
                           BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer,
                           boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                           float red, float green, float blue, float alpha) {
        super.postRender(poseStack, animatable,
                model, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);

        if (this.level == null) return;

        ItemStack inputStack = animatable.itemStackHandler.getStackInSlot(0);
            Direction left = facing.getClockWise();
            Direction right = facing.getCounterClockWise();

            poseStack.pushPose();
            poseStack.translate(0, 0.5f, 0);

            poseStack.pushPose();
            offsetToFace(poseStack, left, true);
            renderItem(inputStack, poseStack, bufferSource, level, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.pushPose();
            offsetToFace(poseStack, right, false);
            renderItem(inputStack, poseStack, bufferSource, level, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.popPose();

        ItemStack recipeResultStack = animatable.getResultItemForDisplay();
        if (!recipeResultStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0, 0.5f, 0);
            offsetToTop(poseStack, facing);

            renderRecipeResultItem(recipeResultStack, poseStack, bufferSource, level, packedLight, packedOverlay);

            poseStack.popPose();
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