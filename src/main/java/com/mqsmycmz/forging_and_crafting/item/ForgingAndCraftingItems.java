package com.mqsmycmz.forging_and_crafting.item;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgingAndCraftingItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ForgingAndCrafting.MOD_ID);

    public static final RegistryObject<Item> DUST_PARTICLES = ITEMS.register("dust_particles", () ->
            new Item(new Item.Properties()));

    public static final RegistryObject<Item> GEAR = ITEMS.register("gear", () ->
            new Item(new Item.Properties()));

    public static final RegistryObject<Item> SIMPLE_STONE_CHISEL = ITEMS.register("simple_stone_chisel", () ->
            new SimpleStoneChiselItem(new Item.Properties()));

    public static final RegistryObject<Item> COPPER_ORE_POWDER_PARTICLES = ITEMS.register("copper_ore_powder_particles", () ->
            new Item(new Item.Properties()));

    public static final RegistryObject<Item> IRON_ORE_POWDER_PARTICLES = ITEMS.register("iron_ore_powder_particles", () ->
            new Item(new Item.Properties()));

    public static final RegistryObject<Item> GOLD_ORE_POWDER_PARTICLES = ITEMS.register("gold_ore_powder_particles", () ->
            new Item(new Item.Properties()));

    public static final RegistryObject<Item> GRAPHITE_POWDER = ITEMS.register("graphite_powder", () ->
            new GraphitePowderItem(new Item.Properties()));

    public static final RegistryObject<Item> PURE_GRAPHITE_POWDER = ITEMS.register("pure_graphite_powder", () ->
            new PureGraphitePowderItem(new Item.Properties()));

    public static final RegistryObject<Item> WOODEN_BUCKET_ITEM = ITEMS.register("wooden_bucket_item", () ->
            new WoodenBucketItem(ForgingAndCraftingBlocks.WOODEN_BUCKET.get(), new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WATER_WOODEN_BUCKET_ITEM = ITEMS.register("water_wooden_bucket_item", () ->
            new WaterWoodenBucketItem(ForgingAndCraftingBlocks.WATER_WOODEN_BUCKET.get(), new Item.Properties().stacksTo(1)));

    // 在 ForgingAndCraftingItems.java 中添加：
    public static final RegistryObject<Item> GRINDING_TABLE_ANIMATED = ITEMS.register("grinding_table_animated",
            () -> new GrindingTableAnimatedItem(new Item.Properties()));

    public static final RegistryObject<Item> ROCK_CRUSHER_BLOCK_ITEM = ITEMS.register("rock_crusher", () ->
            new RockCrusherBlockItem(ForgingAndCraftingBlocks.ROCK_CRUSHER.get(), new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}