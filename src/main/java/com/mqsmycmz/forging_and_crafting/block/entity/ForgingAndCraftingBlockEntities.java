package com.mqsmycmz.forging_and_crafting.block.entity;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgingAndCraftingBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ForgingAndCrafting.MOD_ID);

    public static final RegistryObject<BlockEntityType<RockCrusherBlockEntity>> ROCK_CRUSHER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rock_crusher_block_entity", () ->
                    BlockEntityType.Builder.of(RockCrusherBlockEntity::new,
                            ForgingAndCraftingBlocks.ROCK_CRUSHER.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
