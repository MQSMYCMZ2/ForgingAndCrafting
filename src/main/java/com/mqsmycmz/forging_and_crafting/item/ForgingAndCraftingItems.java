package com.mqsmycmz.forging_and_crafting.item;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
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

public static final RegistryObject<Item> ROCK_CRUSHER_BLOCK_ITEM = ITEMS.register("rock_crusher",
        () -> new RockCrusherBlockItem(ForgingAndCraftingBlocks.ROCK_CRUSHER.get(), new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
