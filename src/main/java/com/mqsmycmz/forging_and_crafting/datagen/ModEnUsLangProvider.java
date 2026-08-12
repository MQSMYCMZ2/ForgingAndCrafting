package com.mqsmycmz.forging_and_crafting.datagen;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModEnUsLangProvider extends LanguageProvider {
    public ModEnUsLangProvider(PackOutput output) {
        super(output, ForgingAndCrafting.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ForgingAndCraftingItems.DUST_PARTICLES.get(), "Dust Particles");
        add(ForgingAndCraftingItems.GEAR.get(), "Gear");
        add(ForgingAndCraftingItems.SIMPLE_STONE_CHISEL.get(), "Simple Stone Chisel");
        add(ForgingAndCraftingItems.COPPER_ORE_POWDER_PARTICLES.get(), "Copper Ore Powder Particles");
        add(ForgingAndCraftingItems.IRON_ORE_POWDER_PARTICLES.get(), "Iron Ore Powder Particles");
        add(ForgingAndCraftingItems.GOLD_ORE_POWDER_PARTICLES.get(), "Gold Ore Powder Particles");

        add(ForgingAndCraftingBlocks.ROCK_CRUSHER.get(), "Rock Crusher");
        add(ForgingAndCraftingBlocks.CLAY_BRICK.get(), "Clay Brick");
        add(ForgingAndCraftingBlocks.IRON_ORE_GRANULES.get(), "Iron Ore Granules");
        add(ForgingAndCraftingBlocks.COPPER_ORE_GRANULES.get(), "Copper Ore Granules");
        add(ForgingAndCraftingBlocks.GOLD_ORE_GRANULES.get(), "Gold Ore Granules");
        add(ForgingAndCraftingBlocks.GRINDING_TABLE.get(), "Grinding Table");
        add(ForgingAndCraftingBlocks.MELTING_POT.get(), "Melting Pot");
        add(ForgingAndCraftingBlocks.GRAPHITE_ORE.get(), "Graphite Ore");
        add(ForgingAndCraftingBlocks.DEEPSLATE_GRAPHITE_ORE.get(), "Deepslate Graphite Ore");
//        add(ForgingAndCraftingBlocks.ELECTRIC_ENERGY_TRANSMISSION_PIPELINE.get(), "Electric Energy Transmission Pipeline");
//        add(ForgingAndCraftingBlocks.SOLUTION_DELIVERY_PIPELINE.get(), "Solution Delivery Pipeline");
//        add(ForgingAndCraftingBlocks.INFORMATION_TRANSMISSION_CABLE.get(), "Information Transmission Cable");

        add("itemGroup.forging_and_crafting_tab", "Forging And Crafting");

        add("entity.forging_and_crafting.rock_crusher", "Rock Crusher");

        add("jei.forging_and_crafting.rock_crusher", "Rock Crusher");
        add("jei.forging_and_crafting.category.ore_processing", "Ore Chiseling");
        add("jei.forging_and_crafting.ore_processing.hint", "Use Simple Stone Chisel on Carrier Dish");

        add("message.forging_and_crafting.chisel_sharpness", "§aChiseling successful! Current sharpness level:%d");

        add("tooltip.forging_and_crafting.chisel.sharpness", "§aSharpness:%d");
        add("tooltip.forging_and_crafting.chisel.granules_drop", "§eEach output: %d grains");
        add("tooltip.forging_and_crafting.simple_stone_chisel.auto_sharpen_hint", "§bSneak right-click Simple Stone Chisel (consumes 1 durability, +1 sharpness, the number of fragmented grains dropped increases with every 15 points of sharpness)");
    }
}
