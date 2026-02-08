package com.mqsmycmz.forging_and_crafting.datagen;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.block.RockCrusherBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
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
