package com.mqsmycmz.forging_and_crafting.datapack;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class RecipeRemovalConfig {
    public static final Set<Item> SIMPLE_TOOL_RECIPES_SET = new HashSet<>();
    public static final Set<Item> SMITHING_TOOL_RECIPES_SET = new HashSet<>();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Path config_dir = FMLPaths.CONFIGDIR.get().resolve("forging_and_crafting");
    private static final Path config_file = config_dir.resolve("recipe_removal.json");

    public static void load() {
        SIMPLE_TOOL_RECIPES_SET.clear();
        SMITHING_TOOL_RECIPES_SET.clear();

        if (!Files.exists(config_file)) {
            createDefaultConfig();
        }

        try (Reader reader = Files.newBufferedReader(config_file)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json == null) return;

            if (json.has("crafting") && json.get("crafting").isJsonArray()) {
                parseArray(json.getAsJsonArray("crafting"), SIMPLE_TOOL_RECIPES_SET);
            }

            if (json.has("smithing") && json.get("smithing").isJsonArray()) {
                parseArray(json.getAsJsonArray("smithing"), SMITHING_TOOL_RECIPES_SET);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void parseArray(JsonArray array, Set<Item> set) {
        for (JsonElement element : array) {
            String itemId = element.getAsString();
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
            if (item == null || item == Items.AIR) {
                System.err.println("recipe_removal.json中的无效物品: " + itemId);

                continue;
            }
            set.add(item);
        }
    }

    private static void createDefaultConfig() {
        try {
            Files.createDirectories(config_dir);
            JsonObject defaultConfig = new JsonObject();

            JsonArray crafting = new JsonArray();
            crafting.add("minecraft:stone_axe");
            crafting.add("minecraft:stone_hoe");
            crafting.add("minecraft:stone_pickaxe");
            crafting.add("minecraft:stone_shovel");
            crafting.add("minecraft:stone_sword");
            crafting.add("minecraft:iron_axe");
            crafting.add("minecraft:iron_hoe");
            crafting.add("minecraft:iron_pickaxe");
            crafting.add("minecraft:iron_shovel");
            crafting.add("minecraft:iron_sword");
            crafting.add("minecraft:golden_axe");
            crafting.add("minecraft:golden_hoe");
            crafting.add("minecraft:golden_pickaxe");
            crafting.add("minecraft:golden_shovel");
            crafting.add("minecraft:golden_sword");
            crafting.add("minecraft:diamond_axe");
            crafting.add("minecraft:diamond_hoe");
            crafting.add("minecraft:diamond_pickaxe");
            crafting.add("minecraft:diamond_shovel");
            crafting.add("minecraft:diamond_sword");

            JsonArray smithing = new JsonArray();
            smithing.add("minecraft:netherite_axe");
            smithing.add("minecraft:netherite_hoe");
            smithing.add("minecraft:netherite_pickaxe");
            smithing.add("minecraft:netherite_shovel");
            smithing.add("minecraft:netherite_sword");

            defaultConfig.add("crafting", crafting);
            defaultConfig.add("smithing", smithing);

            try (Writer writer = Files.newBufferedWriter(config_file)) {
                gson.toJson(defaultConfig, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
