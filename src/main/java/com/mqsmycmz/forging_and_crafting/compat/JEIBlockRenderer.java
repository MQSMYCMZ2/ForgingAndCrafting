package com.mqsmycmz.forging_and_crafting.compat;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class JEIBlockRenderer {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    public static void renderCarrierDish(GuiGraphics guiGraphics, int x, int y, float scale) {
        BlockState state = ForgingAndCraftingBlocks.CARRIER_DISH.get().defaultBlockState();
        renderBlock(guiGraphics, state, x, y, scale);
    }

    public static void renderBlock(GuiGraphics guiGraphics, BlockState state, int x, int y, float scale) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        poseStack.translate(x, y, 100.0);
        poseStack.scale(scale, -scale, scale);

        poseStack.mulPose(Axis.XP.rotationDegrees(40));
        poseStack.mulPose(Axis.YP.rotationDegrees(32));

        poseStack.translate(0, 0.15, 0);
        poseStack.scale(1.5f, 1.5f, 1.5f);

        renderBlockModel(poseStack, state);

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private static void renderBlockModel(PoseStack poseStack, BlockState state) {
        BlockRenderDispatcher blockRenderDispatcher = MINECRAFT.getBlockRenderer();
        BakedModel model = blockRenderDispatcher.getBlockModel(state);

        Lighting.setupForEntityInInventory();

        MultiBufferSource.BufferSource bufferSource = MINECRAFT.renderBuffers().bufferSource();

        blockRenderDispatcher.getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.solid()),
                state,
                model,
                1.0f, 1.0f, 1.0f,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.solid()
        );

        bufferSource.endBatch();

        Lighting.setupFor3DItems();
    }
}