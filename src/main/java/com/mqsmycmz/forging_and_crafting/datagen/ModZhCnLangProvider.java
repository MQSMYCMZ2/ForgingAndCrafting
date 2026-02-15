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
        add(ForgingAndCraftingItems.GEAR.get(), "齿轮");
        add(ForgingAndCraftingItems.CHISEL.get(), "凿子");
        add(ForgingAndCraftingItems.COPPER_ORE_POWDER_PARTICLES.get(), "铜矿粉末颗粒");
        add(ForgingAndCraftingItems.IRON_ORE_POWDER_PARTICLES.get(), "铁矿粉末颗粒");
        add(ForgingAndCraftingItems.GOLD_ORE_POWDER_PARTICLES.get(), "金矿粉末颗粒");

        add(ForgingAndCraftingBlocks.ROCK_CRUSHER.get(), "碎石机");
        add(ForgingAndCraftingBlocks.CLAY_BRICK.get(), "耐火砖块");
        add(ForgingAndCraftingBlocks.IRON_ORE_GRANULES.get(), "铁矿碎粒");
        add(ForgingAndCraftingBlocks.COPPER_ORE_GRANULES.get(), "铜矿碎粒");
        add(ForgingAndCraftingBlocks.GOLD_ORE_GRANULES.get(), "金矿碎粒");
        add(ForgingAndCraftingBlocks.CARRIER_DISH.get(), "承载皿");
        add(ForgingAndCraftingBlocks.MELTING_POT.get(), "熔炼锅");
        add(ForgingAndCraftingBlocks.PRIMARY_ELECTRIC_ENERGY_TRANSMISSION_PIPELINE.get(), "初级电能传输管道");
        add(ForgingAndCraftingBlocks.PRIMARY_SOLUTION_DELIVERY_PIPELINE.get(), "初级溶液输送管道");

        add("itemGroup.forging_and_crafting_tab", "锻造和创造");

        add("entity.forging_and_crafting.rock_crusher", "碎石机");

        add("jei.forging_and_crafting.rock_crusher", "碎石机");
        add("jei.forging_and_crafting.ore_processing.hint", "使用凿子在承物盘上处理");
        add("jei.forging_and_crafting.category.ore_processing", "矿石凿刻处理");

        add("message.forging_and_crafting.chisel_sharpness", "§a磨凿成功！当前尖锐程度：%d");

        add("tooltip.forging_and_crafting.chisel.sharpness", "§a尖锐程度：%d");
        add("tooltip.forging_and_crafting.chisel.granules_drop", "§e每次产出：%d 个碎粒");
        add("tooltip.forging_and_crafting.chisel.grind_hint", "§8潜行右键磨凿（消耗1耐久，+1尖锐程度）");
    }
}
