package com.mqsmycmz.forging_and_crafting.worldgen;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ForgingAndCraftingPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ORE_GRAPHITE = registerKey("ore_graphite");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures =
                context.lookup(Registries.CONFIGURED_FEATURE);

        register(context,
                ORE_GRAPHITE,
                configuredFeatures.getOrThrow(ForgingAndCraftingConfiguredFeatures.ORE_GRAPHITE),
                List.of(CountPlacement.of(UniformInt.of(1, 2)),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.of(TrapezoidHeight.of(
                                VerticalAnchor.absolute(-48),
                                VerticalAnchor.absolute(32),
                                0)),
                        BiomeFilter.biome()));
    }

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                new ResourceLocation(ForgingAndCrafting.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
