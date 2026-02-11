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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CarrierDishBlockEntityRenderer implements BlockEntityRenderer<CarrierDishBlockEntity> {
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

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(displayedItem, pBlockEntity.getLevel(), null, 0);

        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 0.5f, 0.5f);

        float scale = 14f / 16f;
        pPoseStack.scale(scale, scale, scale);

        long time = System.currentTimeMillis();
        float angle = (time % 3600) / 10f;
        pPoseStack.mulPose(Axis.YP.rotationDegrees(angle));

        itemRenderer.render(displayedItem, ItemDisplayContext.FIXED, false, pPoseStack,
                pBuffer, pPackedLight, pPackedOverlay, bakedModel);

        pPoseStack.popPose();
    }
}
