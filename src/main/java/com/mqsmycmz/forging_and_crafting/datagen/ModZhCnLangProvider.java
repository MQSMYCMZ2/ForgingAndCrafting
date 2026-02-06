package com.mqsmycmz.forging_and_crafting.datagen;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModZhCnLangProvider extends LanguageProvider {
    public ModZhCnLangProvider(PackOutput output) {
        super(output, ForgingAndCrafting.MOD_ID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(ForgingAndCraftingItems.DUST_PARTICLES.get(), "灰尘颗粒");

        add(ForgingAndCraftingBlocks.ROCK_CRUSHER.get(), "碎石机");
        add(ForgingAndCraftingBlocks.CLAY_BRICK.get(), "耐火砖块");

        add("itemGroup.forging_and_crafting_tab", "锻造和创造");

        add("entity.forging_and_crafting.rock_crusher", "碎石机");

        add("jei.forging_and_crafting.rock_crusher", "碎石机");
    }
}
