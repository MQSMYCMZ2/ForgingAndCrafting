package com.mqsmycmz.forging_and_crafting.block.entity.renderer.model;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.entity.RockCrusherBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RockCrusherModel extends GeoModel<RockCrusherBlockEntity> {
    @Override
    public ResourceLocation getModelResource(RockCrusherBlockEntity entity) {
        return new ResourceLocation(ForgingAndCrafting.MOD_ID, "geo/rock_crusher.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RockCrusherBlockEntity entity) {
        return new ResourceLocation(ForgingAndCrafting.MOD_ID, "textures/block/rock_crusher.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RockCrusherBlockEntity entity) {
        return new ResourceLocation(ForgingAndCrafting.MOD_ID, "animations/rock_crusher.animation.json");
    }
}
