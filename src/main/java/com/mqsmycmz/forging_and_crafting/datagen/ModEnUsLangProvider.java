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
        add(ForgingAndCraftingItems.CHISEL.get(), "Chisel");

        add(ForgingAndCraftingBlocks.ROCK_CRUSHER.get(), "Rock Crusher");
        add(ForgingAndCraftingBlocks.CLAY_BRICK.get(), "Clay Brick");
        add(ForgingAndCraftingBlocks.IRON_ORE_GRANULES.get(), "Iron Ore Granules");
        add(ForgingAndCraftingBlocks.COPPER_ORE_GRANULES.get(), "Copper Ore Granules");
        add(ForgingAndCraftingBlocks.GOLD_ORE_GRANULES.get(), "Gold Ore Granules");
        add(ForgingAndCraftingBlocks.CARRIER_DISH.get(), "Carrier Dish");

        add("itemGroup.forging_and_crafting_tab", "Forging And Crafting");

        add("entity.forging_and_crafting.rock_crusher", "Rock Crusher");

        add("jei.forging_and_crafting.rock_crusher", "Rock Crusher");
        add("jei.forging_and_crafting.category.ore_processing", "Ore Chiseling");
        add("jei.forging_and_crafting.ore_processing.hint", "Use Chisel on Carrier Dish");

        add("message.forging_and_crafting.chisel_sharpness", "§aChiseling successful! Current sharpness level:%d");

        add("tooltip.forging_and_crafting.chisel.sharpness", "§aSharpness:%d");
        add("tooltip.forging_and_crafting.chisel.granules_drop", "§eEach output: %d grains");
        add("tooltip.forging_and_crafting.chisel.grind_hint", "§8 Sneak right-click Chisel (consumes 1 durability, +1 sharpness)");
    }
}
