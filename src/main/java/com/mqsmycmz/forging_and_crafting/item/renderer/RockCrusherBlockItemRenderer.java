package com.mqsmycmz.forging_and_crafting.item.renderer;

import com.mqsmycmz.forging_and_crafting.item.RockCrusherBlockItem;
import com.mqsmycmz.forging_and_crafting.item.model.RockCrusherBlockItemModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RockCrusherBlockItemRenderer extends GeoItemRenderer<RockCrusherBlockItem> {
    public RockCrusherBlockItemRenderer() {
        super(new RockCrusherBlockItemModel());
    }
}
