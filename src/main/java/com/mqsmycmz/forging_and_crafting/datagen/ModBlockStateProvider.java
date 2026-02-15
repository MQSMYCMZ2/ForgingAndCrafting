package com.mqsmycmz.forging_and_crafting.datagen;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.block.PrimaryElectricEnergyTransmissionPipeline;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ForgingAndCrafting.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ForgingAndCraftingBlocks.CLAY_BRICK.get(), cubeAll(ForgingAndCraftingBlocks.CLAY_BRICK.get()));

        horizontalBlock(ForgingAndCraftingBlocks.IRON_ORE_GRANULES.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/iron_ore_granules")));
        simpleBlockItem(ForgingAndCraftingBlocks.IRON_ORE_GRANULES.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/iron_ore_granules")));

        horizontalBlock(ForgingAndCraftingBlocks.COPPER_ORE_GRANULES.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/copper_ore_granules")));
        simpleBlockItem(ForgingAndCraftingBlocks.COPPER_ORE_GRANULES.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/copper_ore_granules")));

        horizontalBlock(ForgingAndCraftingBlocks.GOLD_ORE_GRANULES.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/gold_ore_granules")));
        simpleBlockItem(ForgingAndCraftingBlocks.GOLD_ORE_GRANULES.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/gold_ore_granules")));

        horizontalBlock(ForgingAndCraftingBlocks.CARRIER_DISH.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/carrier_dish")));
        simpleBlockItem(ForgingAndCraftingBlocks.CARRIER_DISH.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/carrier_dish")));

        horizontalBlock(ForgingAndCraftingBlocks.MELTING_POT.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/melting_pot")));
        simpleBlockItem(ForgingAndCraftingBlocks.MELTING_POT.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/melting_pot")));

        simpleBlockItem(ForgingAndCraftingBlocks.PRIMARY_ELECTRIC_ENERGY_TRANSMISSION_PIPELINE.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/primary_electric_energy_transmission_pipeline")));

        simpleBlockItem(ForgingAndCraftingBlocks.PRIMARY_SOLUTION_DELIVERY_PIPELINE.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/primary_solution_delivery_pipeline")));
    }

//    public void horizontalBlock(Block block, ModelFile model) {
//        getVariantBuilder(block)
//                .forAllStates(state -> {
//                    Direction dir = state.getValue(RockCrusherBlock.FACING);
//                    return ConfiguredModel.builder()
//                            .modelFile(model)
//                            .rotationY((int) dir.toYRot())
//                            .build();
//                });
//    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
