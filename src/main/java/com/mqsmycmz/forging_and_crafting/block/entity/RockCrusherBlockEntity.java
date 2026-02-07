package com.mqsmycmz.forging_and_crafting.block.entity;

import com.mqsmycmz.forging_and_crafting.recipe.RockCrusherRecipe;
import com.mqsmycmz.forging_and_crafting.world.menu.RockCrusherMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
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

public class RockCrusherBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemStackHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            // 关键：物品变化时立即更新缓存并同步
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            } else if (level != null && level.isClientSide()) {
                // 客户端立即更新缓存
                updateClientResultCache();
            }
        }
    };

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private ItemStack clientResultCache = ItemStack.EMPTY;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 80;

    public RockCrusherBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ForgingAndCraftingBlockEntities.ROCK_CRUSHER_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> RockCrusherBlockEntity.this.progress;
                    case 1 -> RockCrusherBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> RockCrusherBlockEntity.this.progress = pValue;
                    case 1 -> RockCrusherBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemStackHandler.getSlots());
        for (int i = 0; i < itemStackHandler.getSlots(); i ++) {
            inventory.setItem(i, itemStackHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemStackHandler);
        // 确保客户端加载时更新缓存
        if (level != null && level.isClientSide()) {
            updateClientResultCache();
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("entity.forging_and_crafting.rock_crusher");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory inventory, Player player) {
        return new RockCrusherMenu(pContainerId, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemStackHandler.serializeNBT());
        pTag.putInt("rock_crusher.progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemStackHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("rock_crusher.progress");
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        // 接收同步数据后立即更新缓存
        if (level != null && level.isClientSide()) {
            updateClientResultCache();
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if (hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress ++;
    }

    private boolean hasRecipe() {
        Optional<RockCrusherRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack result = recipe.get().getResultItem(null);
        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<RockCrusherRecipe> getCurrentRecipe() {
        if (level == null) return Optional.empty();

        SimpleContainer inventory = new SimpleContainer(this.itemStackHandler.getSlots());
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemStackHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(RockCrusherRecipe.Type.INSTANCE, inventory, level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemStackHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemStackHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemStackHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemStackHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private void craftItem() {
        Optional<RockCrusherRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        ItemStack result = recipe.get().getResultItem(null);
        this.itemStackHandler.extractItem(INPUT_SLOT, 1, false);
        this.itemStackHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemStackHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public void updateClientResultCache() {
        if (level == null || !level.isClientSide()) return;

        ItemStack input = itemStackHandler.getStackInSlot(INPUT_SLOT);

        if (input.isEmpty()) {
            clientResultCache = ItemStack.EMPTY;
            return;
        }

        Optional<RockCrusherRecipe> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            clientResultCache = recipe.get().getResultItem(null);
        }
    }

    public ItemStack getResultItemForDisplay() {
        if (level == null) return ItemStack.EMPTY;

        // 客户端使用缓存
        if (level.isClientSide()) {
            // 如果缓存为空但输入不为空，尝试更新
            ItemStack input = itemStackHandler.getStackInSlot(INPUT_SLOT);
            if (!input.isEmpty() && clientResultCache.isEmpty()) {
                updateClientResultCache();
            }
            return clientResultCache;
        }

        // 服务端实时计算
        ItemStack input = itemStackHandler.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) return ItemStack.EMPTY;

        Optional<RockCrusherRecipe> recipe = getCurrentRecipe();
        return recipe.map(r -> r.getResultItem(null)).orElse(ItemStack.EMPTY);
    }

    public boolean hasValidRecipe() {
        return getCurrentRecipe().isPresent();
    }
}