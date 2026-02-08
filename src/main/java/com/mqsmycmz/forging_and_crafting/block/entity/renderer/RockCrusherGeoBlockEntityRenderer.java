package com.mqsmycmz.forging_and_crafting.block.entity.renderer;

import com.mqsmycmz.forging_and_crafting.block.entity.RockCrusherBlockEntity;
import com.mqsmycmz.forging_and_crafting.block.entity.renderer.model.RockCrusherModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RockCrusherGeoBlockEntityRenderer extends GeoBlockRenderer<RockCrusherBlockEntity> {
    public RockCrusherGeoBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new RockCrusherModel());
    }
}