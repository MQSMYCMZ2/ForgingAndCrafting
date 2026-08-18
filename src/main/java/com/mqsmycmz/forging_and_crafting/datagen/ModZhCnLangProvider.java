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
        add(ForgingAndCraftingItems.SIMPLE_STONE_CHISEL.get(), "简易石凿");
        add(ForgingAndCraftingItems.COPPER_ORE_POWDER_PARTICLES.get(), "铜矿粉末颗粒");
        add(ForgingAndCraftingItems.IRON_ORE_POWDER_PARTICLES.get(), "铁矿粉末颗粒");
        add(ForgingAndCraftingItems.GOLD_ORE_POWDER_PARTICLES.get(), "金矿粉末颗粒");
        add(ForgingAndCraftingItems.GRAPHITE_POWDER.get(), "石墨粉");
        add(ForgingAndCraftingItems.WOODEN_BUCKET.get(), "木质水桶");

        add(ForgingAndCraftingBlocks.ROCK_CRUSHER.get(), "碎石机");
        add(ForgingAndCraftingBlocks.CLAY_BRICK.get(), "耐火砖块");
        add(ForgingAndCraftingBlocks.IRON_ORE_GRANULES.get(), "铁矿碎粒");
        add(ForgingAndCraftingBlocks.COPPER_ORE_GRANULES.get(), "铜矿碎粒");
        add(ForgingAndCraftingBlocks.GOLD_ORE_GRANULES.get(), "金矿碎粒");
        add(ForgingAndCraftingBlocks.GRINDING_TABLE.get(), "凿磨台");
        add(ForgingAndCraftingBlocks.MELTING_POT.get(), "熔炼锅");
        add(ForgingAndCraftingBlocks.GRAPHITE_ORE.get(), "石墨原矿");
        add(ForgingAndCraftingBlocks.DEEPSLATE_GRAPHITE_ORE.get(), "深板岩石墨原矿");
//        add(ForgingAndCraftingBlocks.ELECTRIC_ENERGY_TRANSMISSION_PIPELINE.get(), "电能传输管道");
//        add(ForgingAndCraftingBlocks.SOLUTION_DELIVERY_PIPELINE.get(), "溶液输送管道");
//        add(ForgingAndCraftingBlocks.INFORMATION_TRANSMISSION_CABLE.get(), "信息传输电缆");

        add("itemGroup.forging_and_crafting_tab", "锻造和创造");

        add("entity.forging_and_crafting.rock_crusher", "碎石机");

        add("jei.forging_and_crafting.rock_crusher", "碎石机");
        add("jei.forging_and_crafting.ore_processing.hint", "使用简易石凿在承物盘上处理");
        add("jei.forging_and_crafting.category.ore_processing", "矿石凿刻处理");
        add("jei.forging_and_crafting.grinding_table", "载物皿");

        add("message.forging_and_crafting.chisel_sharpness", "§a磨凿成功！当前尖锐程度：%d");

        add("tooltip.forging_and_crafting.chisel.sharpness", "§a尖锐程度：%d");
        add("tooltip.forging_and_crafting.chisel.granules_drop", "§e每次产出：%d 个碎粒");
        add("tooltip.forging_and_crafting.simple_stone_chisel.auto_sharpen_hint", "§b潜行右键简易石凿（消耗1耐久，+1尖锐程度，每15点尖锐程度增加掉落的碎粒数量）");
    }
}
