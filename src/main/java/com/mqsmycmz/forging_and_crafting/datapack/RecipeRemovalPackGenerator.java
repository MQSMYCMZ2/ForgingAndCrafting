package com.mqsmycmz.forging_and_crafting.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RecipeRemovalPackGenerator {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Path GENERATED_PATH = FMLPaths.GAMEDIR.get()
            .resolve("forging_and_crafting")
            .resolve("generated_pack");

    private static final JsonObject UNOBTAINABLE_ITEM = new JsonObject();
    static {
        UNOBTAINABLE_ITEM.addProperty("item", "minecraft:barrier");
    }

    private static final JsonObject AIR_RESULT = new JsonObject();
    static {
        AIR_RESULT.addProperty("item", "minecraft:air");
    }

    public static Path generate() {
        try {
            if (Files.exists(GENERATED_PATH)) {
                deleteDirectory(GENERATED_PATH);
            }

            Path recipesPath = GENERATED_PATH.resolve("data").resolve("minecraft").resolve("recipes");
            Files.createDirectories(recipesPath);

            JsonObject packMeta = new JsonObject();
            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", 15);
            pack.addProperty("description", "Forging and Crafting Generated Pack");
            packMeta.add("pack", pack);
            Files.writeString(GENERATED_PATH.resolve("pack.mcmeta"), gson.toJson(packMeta));

            for (Item item : RecipeRemovalConfig.SIMPLE_TOOL_RECIPES_SET) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                if (id == null) continue;

                JsonObject recipe = new JsonObject();
                recipe.addProperty("type", "minecraft:crafting_shaped");

                JsonArray pattern = new JsonArray();
                pattern.add("X");
                recipe.add("pattern", pattern);

                JsonObject key = new JsonObject();
                key.add("X", UNOBTAINABLE_ITEM);
                recipe.add("key", key);
                recipe.add("result", AIR_RESULT);

                Files.writeString(recipesPath.resolve(id.getPath() + ".json"), gson.toJson(recipe));
            }

            for (Item item : RecipeRemovalConfig.SMITHING_TOOL_RECIPES_SET) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                if (id == null) continue;

                String recipeName = id.getPath() + "_smithing";

                JsonObject recipe = new JsonObject();
                addForgeFalseCondition(recipe);
                recipe.addProperty("type", "minecraft:smithing_transform");

                Files.writeString(recipesPath.resolve(recipeName + ".json"), gson.toJson(recipe));
            }

            for (ResourceLocation id : RecipeRemovalConfig.SMELTING_RECIPES_SET) {
                JsonObject recipe = new JsonObject();
                addForgeFalseCondition(recipe);
                recipe.addProperty("type", "minecraft:smelting");
                JsonObject ingredient = new JsonObject();
                ingredient.addProperty("item", "minecraft:barrier");
                recipe.add("ingredient", ingredient);
                recipe.addProperty("result", "minecraft:air");
                recipe.addProperty("experience", 0.0);
                recipe.addProperty("cookingtime", 200);
                Files.writeString(recipesPath.resolve(id.getPath() + ".json"), gson.toJson(recipe));
            }

            for (ResourceLocation id : RecipeRemovalConfig.BLASTING_RECIPES_SET) {
                JsonObject recipe = new JsonObject();
                addForgeFalseCondition(recipe);
                recipe.addProperty("type", "minecraft:blasting");
                JsonObject ingredient = new JsonObject();
                ingredient.addProperty("item", "minecraft:barrier");
                recipe.add("ingredient", ingredient);
                recipe.addProperty("result", "minecraft:air");
                recipe.addProperty("experience", 0.0);
                recipe.addProperty("cookingtime", 100);
                Files.writeString(recipesPath.resolve(id.getPath() + ".json"), gson.toJson(recipe));
            }

            return GENERATED_PATH;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addForgeFalseCondition(JsonObject recipe) {
        JsonArray conditions = new JsonArray();
        JsonObject falseCondition = new JsonObject();
        falseCondition.addProperty("type", "forge:false");
        conditions.add(falseCondition);
        recipe.add("forge:conditions", conditions);
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                stream.forEach(child -> {
                    try {
                        deleteDirectory(child);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
        }
        Files.deleteIfExists(path);
    }
}
