package com.mqsmycmz.forging_and_crafting.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ForgingAndCrafting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OreProcessingDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String FOLDER = "carrier_dish";

    // 单例实例
    private static final OreProcessingDataLoader INSTANCE = new OreProcessingDataLoader();

    // 矿石到碎粒的映射
    private Map<Item, Item> oreToGranules = new HashMap<>();
    // 粗矿块到粗矿的映射
    private Map<Item, Item> oreBlockToRawOre = new HashMap<>();
    // 有效矿石列表
    private Map<Item, OreProcessingEntry> processingEntries = new HashMap<>();

    public OreProcessingDataLoader() {
        super(GSON, FOLDER);
    }

    public static OreProcessingDataLoader getInstance() {
        return INSTANCE;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        oreToGranules.clear();
        oreBlockToRawOre.clear();
        processingEntries.clear();

        ForgingAndCrafting.LOGGER.info("Loading ore processing recipes...");

        for (Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            ResourceLocation id = entry.getKey();
            try {
                JsonObject json = entry.getValue().getAsJsonObject();
                parseEntry(id, json);
            } catch (Exception e) {
                ForgingAndCrafting.LOGGER.error("Failed to parse ore processing entry {}: {}", id, e.getMessage());
            }
        }

        ForgingAndCrafting.LOGGER.info("Loaded {} ore processing recipes", processingEntries.size());
    }

    private void parseEntry(ResourceLocation id, JsonObject json) {
        // 解析输入矿石
        if (!json.has("input")) {
            throw new IllegalArgumentException("Missing 'input' field");
        }
        Item inputItem = parseItem(json.get("input").getAsString(), "input");

        // 解析输出碎粒
        if (!json.has("output_granules")) {
            throw new IllegalArgumentException("Missing 'output_granules' field");
        }
        Item granulesItem = parseItem(json.get("output_granules").getAsString(), "output_granules");

        // 解析输出粗矿（可选，默认为原矿物品）
        Item rawOreItem = null;
        if (json.has("output_raw_ore")) {
            rawOreItem = parseItem(json.get("output_raw_ore").getAsString(), "output_raw_ore");
        } else {
            // 尝试自动推断：如果输入是粗矿块，尝试找到对应的粗矿
            rawOreItem = inferRawOre(inputItem);
        }

        // 注册映射
        oreToGranules.put(inputItem, granulesItem);
        if (rawOreItem != null) {
            oreBlockToRawOre.put(inputItem, rawOreItem);
        }

        // 保存完整条目
        processingEntries.put(inputItem, new OreProcessingEntry(inputItem, granulesItem, rawOreItem));

        ForgingAndCrafting.LOGGER.debug("Registered ore processing: {} -> {} (raw: {})",
                inputItem, granulesItem, rawOreItem);
    }

    private Item parseItem(String itemId, String fieldName) {
        ResourceLocation rl = new ResourceLocation(itemId);
        Item item = ForgeRegistries.ITEMS.getValue(rl);
        if (item == null || item == Items.AIR) {
            throw new IllegalArgumentException("Unknown item in field '" + fieldName + "': " + itemId);
        }
        return item;
    }

    private Item inferRawOre(Item inputItem) {
        // 简单的推断逻辑：如果输入是粗矿块，尝试找到对应的粗矿
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(inputItem);
        if (id == null) return null;

        String path = id.getPath();
        // raw_iron_block -> raw_iron
        if (path.endsWith("_block")) {
            String rawPath = path.substring(0, path.length() - 6); // 去掉 "_block"
            ResourceLocation rawId = new ResourceLocation(id.getNamespace(), rawPath);
            Item rawItem = ForgeRegistries.ITEMS.getValue(rawId);
            if (rawItem != null && rawItem != Items.AIR) {
                return rawItem;
            }
        }
        return null;
    }

    // ========== 查询方法 ==========

    public Map<Item, Item> getOreToGranulesMap() {
        return new HashMap<>(oreToGranules);
    }

    public Map<Item, Item> getOreBlockToRawOreMap() {
        return new HashMap<>(oreBlockToRawOre);
    }

    public boolean isValidOre(Item item) {
        return oreToGranules.containsKey(item);
    }

    public Item getGranulesForOre(Item ore) {
        return oreToGranules.get(ore);
    }

    public Item getRawOreForBlock(Item oreBlock) {
        return oreBlockToRawOre.get(oreBlock);
    }

    public OreProcessingEntry getEntry(Item input) {
        return processingEntries.get(input);
    }

    // ========== 事件订阅 ==========

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    // ========== 数据条目类 ==========

    public record OreProcessingEntry(Item input, Item outputGranules, Item outputRawOre) {
    }
}