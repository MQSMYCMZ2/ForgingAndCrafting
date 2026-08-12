package com.mqsmycmz.forging_and_crafting.worldgen;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ForgingAndCraftingWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ForgingAndCraftingConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ForgingAndCraftingPlacedFeatures::bootstrap);

    public ForgingAndCraftingWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ForgingAndCrafting.MOD_ID));
    }
}
