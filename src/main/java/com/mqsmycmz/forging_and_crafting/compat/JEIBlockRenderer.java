package com.mqsmycmz.forging_and_crafting.compat;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class JEIBlockRenderer {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    /**
     * 渲染静态方块（保留原方法用于其他用途）
     */
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

    /**
     * 【新方法】渲染GeckoLib动画物品到JEI界面
     * 替换原来的 renderCarrierDish 静态方块渲染
     */
    public static void renderAnimatedCarrierDish(GuiGraphics guiGraphics, int x, int y, float scale) {
        if (ForgingAndCraftingItems.CARRIER_DISH_ANIMATED == null ||
                ForgingAndCraftingItems.CARRIER_DISH_ANIMATED.get() == null) {
            return;
        }

        ItemStack stack = new ItemStack(ForgingAndCraftingItems.CARRIER_DISH_ANIMATED.get());

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 设置渲染位置
        poseStack.translate(x + scale, y + scale, 150.0);
        poseStack.scale(scale, -scale, scale);

        // 调整角度以匹配JEI展示效果
        poseStack.mulPose(Axis.XP.rotationDegrees(32));
        poseStack.mulPose(Axis.YP.rotationDegrees(23));

        // 获取GeckoLib渲染器并渲染
        IClientItemExtensions extensions = IClientItemExtensions.of(stack.getItem());
        if (extensions != null) {
            BlockEntityWithoutLevelRenderer renderer = extensions.getCustomRenderer();
            if (renderer instanceof GeoItemRenderer<?> geoRenderer) {
                // 使用GeckoLib渲染器渲染，带动画
                // GeckoLib会自动处理动画时间，不需要手动tick
                renderGeoItemInJEI(geoRenderer, stack, poseStack, guiGraphics);
            } else {
                // 回退到默认物品渲染
                renderDefaultItem(stack, poseStack, guiGraphics, scale);
            }
        } else {
            renderDefaultItem(stack, poseStack, guiGraphics, scale);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    /**
     * 使用GeckoLib渲染器渲染动画物品
     * GeckoLib 4.8+ 会自动处理动画更新，不需要手动调用tick或setAnimation
     */
    private static void renderGeoItemInJEI(GeoItemRenderer<?> geoRenderer, ItemStack stack,
                                           PoseStack poseStack, GuiGraphics guiGraphics) {

        MultiBufferSource.BufferSource bufferSource = MINECRAFT.renderBuffers().bufferSource();

        // 重要：在渲染前准备PoseStack
        // GeoItemRenderer期望特定的矩阵状态
        poseStack.pushPose();

        // 调整比例以适应JEI的显示
        poseStack.scale(1.5f, 1.5f, 1.5f);

        // 使用ItemDisplayContext.GUI确保正确的GUI渲染上下文
        // GeckoLib会自动从Minecraft.level.getGameTime()获取动画时间
        geoRenderer.renderByItem(stack, ItemDisplayContext.GUI, poseStack, bufferSource,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        bufferSource.endBatch();
    }

    /**
     * 默认物品渲染（回退方案）
     */
    private static void renderDefaultItem(ItemStack stack, PoseStack poseStack,
                                          GuiGraphics guiGraphics, float scale) {
        ItemRenderer itemRenderer = MINECRAFT.getItemRenderer();
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);

        poseStack.pushPose();
        poseStack.scale(1.0f / scale, 1.0f / scale, 1.0f / scale);

        Lighting.setupForEntityInInventory();
        itemRenderer.render(stack, ItemDisplayContext.GUI, false, poseStack,
                guiGraphics.bufferSource(), LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY, model);
        Lighting.setupFor3DItems();

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