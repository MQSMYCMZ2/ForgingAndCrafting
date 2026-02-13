package com.mqsmycmz.forging_and_crafting.tab;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ForgingAndCraftingCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ForgingAndCrafting.MOD_ID);

    public static final RegistryObject<CreativeModeTab> FORGING_AND_CRAFTING_TAB =
            CREATIVE_MODE_TABS.register("forging_and_crafting_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ForgingAndCraftingBlocks.CLAY_BRICK.get()))
                    .title(Component.translatable("itemGroup.forging_and_crafting_tab"))
                    .displayItems((pParameters, pOutput) -> {
                pOutput.accept(ForgingAndCraftingBlocks.CLAY_BRICK.get());
                pOutput.accept(ForgingAndCraftingBlocks.IRON_ORE_GRANULES.get());
                pOutput.accept(ForgingAndCraftingBlocks.COPPER_ORE_GRANULES.get());
                pOutput.accept(ForgingAndCraftingBlocks.GOLD_ORE_GRANULES.get());
                pOutput.accept(ForgingAndCraftingBlocks.CARRIER_DISH.get());

                pOutput.accept(ForgingAndCraftingItems.DUST_PARTICLES.get());
                pOutput.accept(ForgingAndCraftingItems.ROCK_CRUSHER_BLOCK_ITEM.get());
                pOutput.accept(ForgingAndCraftingItems.GEAR.get());
                pOutput.accept(ForgingAndCraftingItems.CHISEL.get());
                pOutput.accept(ForgingAndCraftingItems.COPPER_ORE_POWDER_PARTICLES.get());
                pOutput.accept(ForgingAndCraftingItems.IRON_ORE_POWDER_PARTICLES.get());
                pOutput.accept(ForgingAndCraftingItems.GOLD_ORE_POWDER_PARTICLES.get());
    }).build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
