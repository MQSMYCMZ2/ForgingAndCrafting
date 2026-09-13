package com.mqsmycmz.forging_and_crafting.block.entity;

import com.mqsmycmz.forging_and_crafting.recipe.ShapingTableRecipe;
import com.mqsmycmz.forging_and_crafting.world.menu.ShapingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ShapingTableBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int SLOT_COUNT = 2;

    // 防止在制作完成处理过程中触发输入槽变化重置
    private boolean isProcessingCraftComplete = false;

    private final ItemStackHandler itemStackHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            // 只有非制作完成流程中，输入槽变化才重置选中状态，且不同步方块更新（容器自动同步）
            if (slot == INPUT_SLOT && level != null && !level.isClientSide && !isProcessingCraftComplete) {
                if (selectedRecipeId != null && !craftFinished && craftProgress == 0 && !isSelectionValid()) {
                    resetSelectionInternal();
                    sync();
                }
            }
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    @Nullable
    private ResourceLocation selectedRecipeId = null;
    private int requestedCount = 1;
    private int craftProgress = 0;
    private int totalCraftTime = 0;
    private boolean craftFinished = false;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> craftProgress;
                case 1 -> totalCraftTime;
                case 2 -> craftFinished ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> craftProgress = pValue;
                case 1 -> totalCraftTime = pValue;
                case 2 -> craftFinished = pValue == 1;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public ShapingTableBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ForgingAndCraftingBlockEntities.SHAPING_TABLE.get(), pPos, pBlockState);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemStackHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemStackHandler.getSlots());
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            if (i == OUTPUT_SLOT && !craftFinished) {
                continue;
            }
            inventory.setItem(i, itemStackHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    // ---------- 服务端逻辑 ----------
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        if (selectedRecipeId != null && !craftFinished && craftProgress == 0 && !isSelectionValid()) {
            resetSelectionInternal();
            sync();
            return;
        }

        if (craftProgress > 0 && craftProgress < totalCraftTime) {
            craftProgress++;
            setChanged();
            if (craftProgress >= totalCraftTime) {
                completeCraft();
            }
        }
    }

    private void completeCraft() {
        isProcessingCraftComplete = true;
        try {
            ShapingTableRecipe recipe = findSelectedRecipe();
            if (recipe == null) {
                resetSelectionInternal();
                return;
            }

            ItemStack input = itemStackHandler.getStackInSlot(INPUT_SLOT);
            int count = Math.max(1, requestedCount);
            int totalConsume = recipe.getConsumptionQuantity() * count;

            if (input.isEmpty()
                    || input.getCount() < totalConsume
                    || !recipe.matches(getInventoryWrapper(), level)) {
                resetSelectionInternal();
                return;
            }

            input.shrink(totalConsume);
            if (input.isEmpty()) {
                itemStackHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
            } else {
                itemStackHandler.setStackInSlot(INPUT_SLOT, input);
            }

            ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
            int totalOutput = result.getCount() * count;
            int maxStack = result.getMaxStackSize();
            result.setCount(Math.min(totalOutput, maxStack));
            itemStackHandler.setStackInSlot(OUTPUT_SLOT, result);
            craftFinished = true;
            craftProgress = 0;
        } finally {
            isProcessingCraftComplete = false;
            setChanged();
        }
    }

    public ItemStack getInput() {
        return itemStackHandler.getStackInSlot(INPUT_SLOT);
    }

    private ShapingTableRecipe.InventoryWrapper getInventoryWrapper() {
        return new ShapingTableRecipe.InventoryWrapper(getInput());
    }

    @Nullable
    private ShapingTableRecipe findSelectedRecipe() {
        if (selectedRecipeId == null || level == null) return null;
        return level.getRecipeManager().byKey(selectedRecipeId)
                .filter(r -> r instanceof ShapingTableRecipe)
                .map(r -> (ShapingTableRecipe) r)
                .orElse(null);
    }

    private void resetSelectionInternal() {
        selectedRecipeId = null;
        requestedCount = 1;
        craftProgress = 0;
        totalCraftTime = 0;
        craftFinished = false;
        itemStackHandler.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
    }

    private int computeMaxCraftTable(ShapingTableRecipe recipe) {
        ItemStack input = getInput();
        if (input.isEmpty()) return 0;
        int consumePer = recipe.getConsumptionQuantity();
        if (consumePer <= 0) return 0;
        int byMaterial = input.getCount() / consumePer;

        ItemStack resultTemplater = recipe.getResultItem(level.registryAccess());

        int resultPer = Math.max(1, resultTemplater.getCount());
        int maxStack = resultTemplater.getMaxStackSize();
        int byOutputStack = maxStack / resultPer;
        return Math.max(0, Math.min(byMaterial, byOutputStack));
    }

    private void updatePreview(ShapingTableRecipe recipe) {
        ItemStack preview = recipe.getResultItem(level.registryAccess()).copy();
        int total = preview.getCount() * Math.max(1, requestedCount);
        int maxStack = preview.getMaxStackSize();
        preview.setCount(Math.min(total, maxStack));
        itemStackHandler.setStackInSlot(OUTPUT_SLOT, preview);
    }

    public void handleRecipeClick(@Nullable ResourceLocation recipeId, Player player) {
        if (level == null || level.isClientSide()) return;

        if (craftProgress > 0) return;

        boolean wasFinished = craftFinished;

        if (wasFinished) {
            collectOutput(player);
            craftFinished = false;
            craftProgress = 0;
            totalCraftTime = 0;
        }

        if (recipeId == null) {
            resetSelectionInternal();
            sync();
            return;
        }

        ShapingTableRecipe recipe = level.getRecipeManager().byKey(recipeId)
                .filter(r -> r instanceof ShapingTableRecipe)
                .map(r -> (ShapingTableRecipe) r)
                .orElse(null);

        if (recipe == null || !recipe.matches(getInventoryWrapper(), level)) {
            resetSelectionInternal();
            sync();
            return;
        }

        int max = computeMaxCraftTable(recipe);
        if (max < 1) {
            resetSelectionInternal();
            sync();
            return;
        }

        if (!wasFinished && recipeId.equals(selectedRecipeId)) {
            if (requestedCount < max) {
                requestedCount ++;
            }
        } else {
            selectedRecipeId = recipeId;
            requestedCount = 1;
        }

        updatePreview(recipe);
        totalCraftTime = recipe.getCraftingTime();
        craftProgress = 0;
        craftFinished = false;
        setChanged();
        sync();
    }

    private void collectOutput(Player player) {
        ItemStack output = itemStackHandler.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) return;

        ItemStack remaining = output.copy();
        player.getInventory().add(remaining);
        if (!remaining.isEmpty()) {
            player.drop(remaining, false);
        }
        itemStackHandler.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
        setChanged();
    }

//    public void selectRecipe(@Nullable ResourceLocation recipeId) {
//        if (level == null || level.isClientSide()) return;
//
//        // 如果 recipeId 为 null，则取消选择
//        if (recipeId == null) {
//            resetSelectionInternal();
//            sync(); // 取消选中也需同步状态
//            return;
//        }
//
//        ShapingTableRecipe recipe = level.getRecipeManager().byKey(recipeId)
//                .filter(r -> r instanceof ShapingTableRecipe)
//                .map(r -> (ShapingTableRecipe) r)
//                .orElse(null);
//        if (recipe == null || !recipe.matches(getInventoryWrapper(), level)) {
//            // 不匹配，取消选择
//            resetSelectionInternal();
//            sync();
//            return;
//        }
//
//        selectedRecipeId = recipeId;
//        ItemStack preview = recipe.getResultItem(level.registryAccess()).copy();
//        itemStackHandler.setStackInSlot(OUTPUT_SLOT, preview);
//        craftProgress = 0;
//        totalCraftTime = recipe.getCraftingTime();
//        craftFinished = false;
//        sync(); // 同步选中状态到客户端
//    }

    private boolean isSelectionValid() {
        ShapingTableRecipe recipe = findSelectedRecipe();
        if (recipe == null) return false;
        if (getInput().isEmpty()) return false;
        return recipe.matches(getInventoryWrapper(), level);
    }

    public void startCrafting() {
        if (level == null || level.isClientSide()) return;
        if (craftProgress > 0) return;
        if (craftFinished) return;

        ShapingTableRecipe recipe = findSelectedRecipe();
        if (recipe == null) {
            resetSelectionInternal();
            return;
        }
        if (!recipe.matches(getInventoryWrapper(), level)) {
            resetSelectionInternal();
            return;
        }

        ItemStack input = getInput();
        if (input.isEmpty()) return;
        int totalConsume = recipe.getConsumptionQuantity();
        if (input.getCount() < totalConsume) return;

        itemStackHandler.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
        craftProgress = 1;
        totalCraftTime = recipe.getCraftingTime();
        craftFinished = false;
        setChanged(); // 进度由 ContainerData 同步，无需 sync
    }

    // 专门用于取出输出后的重置，不触发方块更新，避免干扰客户端显示
    public void resetAfterTake() {
        if (level != null && !level.isClientSide()) {
            resetSelectionInternal();
            setChanged();
            sync();
        }
    }

    @Nullable
    public ResourceLocation getSelectedRecipeId() {
        return selectedRecipeId;
    }

    public int getRequestedCount() {
        return requestedCount;
    }

    // ---------- 数据同步辅助 ----------
    private void sync() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    // ---------- 数据持久化 ----------
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemStackHandler.serializeNBT());
        if (selectedRecipeId != null) {
            pTag.putString("selectedRecipe", selectedRecipeId.toString());
        }
        pTag.putInt("requestedCount", requestedCount);
        pTag.putInt("craftProgress", craftProgress);
        pTag.putInt("totalCraftTime", totalCraftTime);
        pTag.putBoolean("craftFinished", craftFinished);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("inventory")) {
            itemStackHandler.deserializeNBT(pTag.getCompound("inventory"));
        }
        if (pTag.contains("selectedRecipe")) {
            selectedRecipeId = new ResourceLocation(pTag.getString("selectedRecipe"));
        } else {
            selectedRecipeId = null;
        }
        requestedCount = Math.max(1, pTag.getInt("requestedCount"));
        craftProgress = pTag.getInt("craftProgress");
        totalCraftTime = pTag.getInt("totalCraftTime");
        craftFinished = pTag.getBoolean("craftFinished");
    }

    // ---------- 网络同步（仅同步状态，不同步容器物品） ----------
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("inventory", itemStackHandler.serializeNBT());
        if (selectedRecipeId != null) {
            tag.putString("selectedRecipe", selectedRecipeId.toString());
        }
        tag.putInt("requestedCount", requestedCount);
        tag.putInt("craftProgress", craftProgress);
        tag.putInt("totalCraftTime", totalCraftTime);
        tag.putBoolean("craftFinished", craftFinished);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("inventory")) {
            itemStackHandler.deserializeNBT(tag.getCompound("inventory"));
        }
        String idStr = tag.getString("selectedRecipe");
        selectedRecipeId = idStr.isEmpty() ? null : new ResourceLocation(idStr);
        requestedCount = Math.max(1, tag.getInt("requestedCount"));
        craftProgress = tag.getInt("craftProgress");
        totalCraftTime = tag.getInt("totalCraftTime");
        craftFinished = tag.getBoolean("craftFinished");
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag == null) return;
        if (tag.contains("inventory")) {
            itemStackHandler.deserializeNBT(tag.getCompound("inventory"));
        }
        String idStr = tag.getString("selectedRecipe");
        this.selectedRecipeId = idStr.isEmpty() ? null : new ResourceLocation(idStr);
        this.requestedCount = Math.max(1, tag.getInt("requestedCount"));
        this.craftProgress = tag.getInt("craftProgress");
        this.totalCraftTime = tag.getInt("totalCraftTime");
        this.craftFinished = tag.getBoolean("craftFinished");
    }

    // ---------- MenuProvider ----------
    @Override
    public Component getDisplayName() {
        return Component.translatable("entity.forging_and_crafting.shaping_table");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ShapingTableMenu(pContainerId, pPlayerInventory, this, this.data);
    }
}