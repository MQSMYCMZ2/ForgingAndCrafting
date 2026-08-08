package com.mqsmycmz.forging_and_crafting;

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
    private static final Set<Item> ITEM_SET = Set.of(
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

    @SubscribeEvent
    public static void onServerAboutToStar(ServerAboutToStartEvent event) {
        RecipeManager recipeManager = event.getServer().getRecipeManager();
        try {
            Field recipesField = RecipeManager.class.getDeclaredField("recipes");
            recipesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> allRecipes =
                    (Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>)recipesField.get(recipeManager);

            if (allRecipes == null || !allRecipes.containsKey(RecipeType.CRAFTING)) return;

            Map<ResourceLocation, Recipe<?>> craftingRecipes = new HashMap<>(allRecipes.get(RecipeType.CRAFTING));

            int removedCount = 0;

            craftingRecipes.entrySet().removeIf(entry -> {
                Recipe<?> recipe = entry.getValue();
                ItemStack result =
                        recipe.getResultItem(event.getServer().registryAccess());

                if (!result.isEmpty() && ITEM_SET.contains(result.getItem())) {
                    return "minecraft".equals(entry.getKey().getNamespace());
                }
                return false;
            });
            removedCount = allRecipes.get(RecipeType.CRAFTING).size() - craftingRecipes.size();

            Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newAllRecipes = new HashMap<>(allRecipes);
            newAllRecipes.put(RecipeType.CRAFTING, craftingRecipes);
            recipesField.set(recipeManager, newAllRecipes);

            //testing
            System.out.println("ForgingAndCrafting 成功删除" + removedCount + "个原版工具合成配方");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
