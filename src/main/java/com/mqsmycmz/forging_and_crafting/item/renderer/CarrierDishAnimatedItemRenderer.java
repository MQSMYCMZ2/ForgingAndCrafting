package com.mqsmycmz.forging_and_crafting.item.renderer;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.item.CarrierDishAnimatedItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CarrierDishAnimatedItemRenderer extends GeoItemRenderer<CarrierDishAnimatedItem> {

    public CarrierDishAnimatedItemRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(ForgingAndCrafting.MOD_ID, "carrier_dish_with_raw_iron_block")));
    }
}