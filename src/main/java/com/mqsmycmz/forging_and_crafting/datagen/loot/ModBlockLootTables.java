package com.mqsmycmz.forging_and_crafting.datagen.loot;

import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ForgingAndCraftingBlocks.CLAY_BRICK.get());
        this.dropSelf(ForgingAndCraftingBlocks.ROCK_CRUSHER.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgingAndCraftingBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
