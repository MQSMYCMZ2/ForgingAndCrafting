package com.mqsmycmz.forging_and_crafting.worldgen;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class ForgingAndCraftingConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GRAPHITE =
            registerKey("ore_graphite");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceable = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreConfiguration.TargetBlockState> graphiteOres = List.of(
                OreConfiguration.target(stoneReplaceable, ForgingAndCraftingBlocks.GRAPHITE_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceable, ForgingAndCraftingBlocks.DEEPSLATE_GRAPHITE_ORE.get().defaultBlockState())
        );

        register(context, ORE_GRAPHITE, Feature.ORE, new OreConfiguration(graphiteOres, 10));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(ForgingAndCrafting.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register
            (BootstapContext<ConfiguredFeature<?, ?>> context,
             ResourceKey<ConfiguredFeature<?, ?>> key,
             F feature,
             FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
