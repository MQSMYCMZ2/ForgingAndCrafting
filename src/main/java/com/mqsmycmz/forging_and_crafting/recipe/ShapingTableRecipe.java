package com.mqsmycmz.forging_and_crafting.recipe;

import com.google.gson.JsonObject;
import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public class ShapingTableRecipe implements Recipe<Container> {
    public static final RecipeType<ShapingTableRecipe> TYPE = new RecipeType<ShapingTableRecipe>() {};
    public static final ResourceLocation TYPE_ID = new ResourceLocation(ForgingAndCrafting.MOD_ID, "shaping_table");

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int craftingTime;
    private final int consumptionQuantity;  // 新增：消耗数量

    public ShapingTableRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, int craftingTime, int consumptionQuantity) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.craftingTime = craftingTime;
        this.consumptionQuantity = consumptionQuantity;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (pContainer.getContainerSize() < 1) return false;
        ItemStack input = pContainer.getItem(0);
        return ingredient.test(input);
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ShapingTableRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public int getConsumptionQuantity() {
        return consumptionQuantity;
    }

    // ------------------ 容器包装（用于配方匹配） ------------------
    public static class InventoryWrapper implements Container {
        private final ItemStack input;

        public InventoryWrapper(ItemStack input) {
            this.input = input;
        }

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return input.isEmpty();
        }

        @Override
        public ItemStack getItem(int pSlot) {
            return pSlot == 0 ? input : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int pSlot, int pAmount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int pSlot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int pSlot, ItemStack pStack) {
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player pPlayer) {
            return true;
        }

        @Override
        public void clearContent() {
        }
    }

    // ------------------ 序列化器 ------------------
    public static class ShapingTableRecipeSerializer implements RecipeSerializer<ShapingTableRecipe> {
        public static final ShapingTableRecipeSerializer INSTANCE = new ShapingTableRecipeSerializer();
        public static final ResourceLocation ID = new ResourceLocation(ForgingAndCrafting.MOD_ID, "shaping_table");

        @Override
        public ShapingTableRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            // 使用 ShapedRecipe.itemStackFromJson 解析 result 对象
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            int craftingTime = GsonHelper.getAsInt(json, "crafting_time", 100);
            int consumptionQuantity = GsonHelper.getAsInt(json, "consumption_quantity", 1);
            return new ShapingTableRecipe(recipeId, ingredient, result, craftingTime, consumptionQuantity);
        }

        @Override
        public @Nullable ShapingTableRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            int craftingTime = buffer.readInt();
            int consumptionQuantity = buffer.readInt();
            return new ShapingTableRecipe(recipeId, ingredient, result, craftingTime, consumptionQuantity);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapingTableRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeInt(recipe.craftingTime);
            buffer.writeInt(recipe.consumptionQuantity);
        }
    }
}