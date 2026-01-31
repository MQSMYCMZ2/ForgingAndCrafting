package com.mqsmycmz.forging_and_crafting.datagen;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ForgingAndCrafting.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ForgingAndCraftingItems.DUST_PARTICLES.get());
    }
}
