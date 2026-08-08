package com.mqsmycmz.forging_and_crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "forging_and_crafting", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VanillaToolRecipesRemover {
    private static final Set<Item> SIMPLE_TOOL_RECIPES_SET = Set.of(
            Items.STONE_AXE,
            Items.STONE_HOE,
            Items.STONE_PICKAXE,
            Items.STONE_SHOVEL,
            Items.STONE_SWORD,

            Items.IRON_AXE,
            Items.IRON_HOE,
            Items.IRON_PICKAXE,
            Items.IRON_SHOVEL,
            Items.IRON_SWORD,

            Items.GOLDEN_AXE,
            Items.GOLDEN_HOE,
            Items.GOLDEN_PICKAXE,
            Items.GOLDEN_SHOVEL,
            Items.GOLDEN_SWORD,

            Items.DIAMOND_AXE,
            Items.DIAMOND_HOE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_SHOVEL,
            Items.DIAMOND_SWORD
    );

    private static final Set<Item> NETHERITE_TOOL_RECIPES_SET = Set.of(
            Items.NETHERITE_AXE,
            Items.NETHERITE_HOE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_SHOVEL,
            Items.NETHERITE_SWORD
    );

    @SubscribeEvent
    public static void onServerAboutToStar(ServerAboutToStartEvent event) {
        RecipeManager recipeManager = event.getServer().getRecipeManager();
        RegistryAccess registryAccess = event.getServer().registryAccess();

        try {
            Field recipesField = RecipeManager.class.getDeclaredField("recipes");
            recipesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> allRecipes =
                    (Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>)recipesField.get(recipeManager);

            if (allRecipes == null) return;

            Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> mutableAllRecipes = new HashMap<>(allRecipes);

            //删除石质-钻石质工具的合成配方
            if (mutableAllRecipes.containsKey(RecipeType.CRAFTING)) {
                Map<ResourceLocation, Recipe<?>> craftingRecipeMap = new HashMap<>(mutableAllRecipes.get(RecipeType.CRAFTING));
                craftingRecipeMap.entrySet().removeIf(entry -> {
                    Recipe<?> recipe = entry.getValue();
                    ItemStack result = recipe.getResultItem(registryAccess);
                    return !result.isEmpty() &&
                            SIMPLE_TOOL_RECIPES_SET.contains(result.getItem()) &&
                            "minecraft".equals(entry.getKey().getNamespace());
                });
                mutableAllRecipes.put(RecipeType.CRAFTING, craftingRecipeMap);
            }

            //删除下界合金工具的合成配方
            if (mutableAllRecipes.containsKey(RecipeType.SMITHING)) {
                Map<ResourceLocation, Recipe<?>> smithingRecipes = new HashMap<>(mutableAllRecipes.get(RecipeType.SMITHING));

                smithingRecipes.entrySet().removeIf(entry -> {
                   Recipe<?> recipe = entry.getValue();
                   ItemStack result = recipe.getResultItem(registryAccess);
                   return !result.isEmpty() &&
                           NETHERITE_TOOL_RECIPES_SET.contains(result.getItem()) &&
                           "minecraft".equals(entry.getKey().getNamespace());
                });

                mutableAllRecipes.put(RecipeType.SMITHING, smithingRecipes);
            }

            recipesField.set(recipeManager, mutableAllRecipes);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
