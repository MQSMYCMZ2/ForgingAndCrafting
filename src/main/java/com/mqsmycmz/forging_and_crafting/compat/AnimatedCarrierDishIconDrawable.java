// AnimatedCarrierDishDrawable.java
package com.mqsmycmz.forging_and_crafting.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AnimatedCarrierDishIconDrawable implements IDrawable {

    private final int width;
    private final int height;
    private final float scale;

    // 可自定义的渲染参数
    private final float rotationX;  // X轴旋转角度
    private final float rotationY;  // Y轴旋转角度
    private final float rotationZ;  // Z轴旋转角度
    private final float offsetX;    // X偏移（相对于中心）
    private final float offsetY;    // Y偏移（相对于中心）
    private final float offsetZ;    // Z偏移（深度）

    public AnimatedCarrierDishIconDrawable(int width, int height, float scale) {
        this(width, height, scale, -20f, 45f, 0f, 0f, 0f, 0f);
    }

    /**
     * 完整构造函数，可自定义所有渲染参数
     *
     * @param width     绘制区域宽度
     * @param height    绘制区域高度
     * @param scale     模型缩放
     * @param rotationX X轴旋转（度）
     * @param rotationY Y轴旋转（度）
     * @param rotationZ Z轴旋转（度）
     * @param offsetX   X偏移（像素）
     * @param offsetY   Y偏移（像素）
     * @param offsetZ   Z偏移（深度，通常保持0）
     */
    public AnimatedCarrierDishIconDrawable(int width, int height, float scale,
                                           float rotationX, float rotationY, float rotationZ,
                                           float offsetX, float offsetY, float offsetZ) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
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

        // 计算中心点 + 自定义偏移
        float centerX = xOffset + width / 2.0f + offsetX;
        float centerY = yOffset + height / 2.0f + offsetY;

        // 设置渲染位置
        poseStack.translate(centerX, centerY, 100.0 + offsetZ);

        // Y轴用-scale翻转，确保模型正向朝上
        poseStack.scale(scale, -scale, scale);

        // 应用自定义旋转
        if (rotationX != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees(rotationX));
        }
        if (rotationY != 0) {
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
        }
        if (rotationZ != 0) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotationZ));
        }

        // 渲染GeckoLib模型
        renderGeoItem(stack, poseStack);

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void renderGeoItem(ItemStack stack, PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        IClientItemExtensions extensions = IClientItemExtensions.of(stack.getItem());
        if (extensions != null) {
            BlockEntityWithoutLevelRenderer renderer = extensions.getCustomRenderer();
            if (renderer instanceof GeoItemRenderer<?> geoRenderer) {
                geoRenderer.renderByItem(stack, ItemDisplayContext.GUI, poseStack,
                        bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                bufferSource.endBatch();
            }
        }
    }

    public static class Builder {
        private int width = 16;
        private int height = 16;
        private float scale = 10.0f;
        private float rotationX = -20f;
        private float rotationY = 45f;
        private float rotationZ = 0f;
        private float offsetX = 0f;
        private float offsetY = 0f;
        private float offsetZ = 0f;

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder rotation(float x, float y, float z) {
            this.rotationX = x;
            this.rotationY = y;
            this.rotationZ = z;
            return this;
        }

        public Builder offset(float x, float y, float z) {
            this.offsetX = x;
            this.offsetY = y;
            this.offsetZ = z;
            return this;
        }

        public AnimatedCarrierDishIconDrawable build() {
            return new AnimatedCarrierDishIconDrawable(width, height, scale,
                    rotationX, rotationY, rotationZ, offsetX, offsetY, offsetZ);
        }
    }
}