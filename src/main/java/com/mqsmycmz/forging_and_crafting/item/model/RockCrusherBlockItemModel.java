package com.mqsmycmz.forging_and_crafting.item.model;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.item.RockCrusherBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RockCrusherBlockItemModel extends GeoModel<RockCrusherBlockItem> {
    @Override
    public ResourceLocation getModelResource(RockCrusherBlockItem blockItem) {
        return new ResourceLocation(ForgingAndCrafting.MOD_ID, "geo/rock_crusher.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RockCrusherBlockItem blockItem) {
        return new ResourceLocation(ForgingAndCrafting.MOD_ID, "textures/block/rock_crusher.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RockCrusherBlockItem blockItem) {
        return new ResourceLocation(ForgingAndCrafting.MOD_ID, "animations/rock_crusher.animation.json");
    }
}
