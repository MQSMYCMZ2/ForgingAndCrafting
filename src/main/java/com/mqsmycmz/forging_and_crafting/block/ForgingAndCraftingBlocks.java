package com.mqsmycmz.forging_and_crafting.block;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.entity.GoldOreGranulesItem;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ForgingAndCraftingBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ForgingAndCrafting.MOD_ID);

    public static final RegistryObject<Block> CLAY_BRICK =
            registerBlock("clay_brick", () -> new Block(BlockBehaviour.Properties.of()
                    .strength(0.8f, 10f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> IRON_ORE_GRANULES =
            registerBlock("iron_ore_granules", () -> new IronOreGranulesItem(BlockBehaviour.Properties.of()
                    .strength(1f, 10f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> COPPER_ORE_GRANULES =
            registerBlock("copper_ore_granules", () -> new CopperOreGranules(BlockBehaviour.Properties.of()
                    .strength(1f, 10f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> GOLD_ORE_GRANULES =
            registerBlock("gold_ore_granules", () -> new GoldOreGranulesItem(BlockBehaviour.Properties.of()
                    .strength(1f, 10f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> ROCK_CRUSHER = BLOCKS.register("rock_crusher",
            () -> new RockCrusherBlock(BlockBehaviour.Properties.of().noOcclusion()));

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ForgingAndCraftingItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> blocks = BLOCKS.register(name, block);
        registerBlockItem(name, blocks);
        return blocks;
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
