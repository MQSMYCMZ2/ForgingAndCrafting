package com.mqsmycmz.forging_and_crafting.datagen;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ForgingAndCrafting.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ForgingAndCraftingItems.DUST_PARTICLES);
        simpleItem(ForgingAndCraftingItems.GEAR);
        simpleItem(ForgingAndCraftingItems.CHISEL);
        simpleItem(ForgingAndCraftingItems.COPPER_ORE_POWDER_PARTICLES);
        simpleItem(ForgingAndCraftingItems.IRON_ORE_POWDER_PARTICLES);
        simpleItem(ForgingAndCraftingItems.GOLD_ORE_POWDER_PARTICLES);
    }

    @SuppressWarnings("removal")
    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(ForgingAndCrafting.MOD_ID, "item/" + item.getId().getPath()));
    }
}
