package com.mqsmycmz.forging_and_crafting.item;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
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

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
