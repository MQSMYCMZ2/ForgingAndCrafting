package com.mqsmycmz.forging_and_crafting.world.menu;

import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.block.entity.ShapingTableBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ShapingTableMenu extends AbstractContainerMenu {
    public final ShapingTableBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ShapingTableMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory,
                (ShapingTableBlockEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(3));
    }

    public ShapingTableMenu(int containerId, Inventory inventory, ShapingTableBlockEntity blockEntity, ContainerData data) {
        super(ForgingAndCraftingMenuTypes.SHAPING_TABLE.get(), containerId);
        this.blockEntity = blockEntity;
        this.level = inventory.player.level();
        this.data = data;

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, ShapingTableBlockEntity.INPUT_SLOT, 107, 43) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return !isCrafting();
                }

                @Override
                public boolean mayPickup(Player playerIn) {
                    return !isCrafting();
                }

                private boolean isCrafting() {
                    int progress = ShapingTableMenu.this.data.get(0);
                    int total = ShapingTableMenu.this.data.get(1);
                    return progress > 0 && progress < total;
                }
            });

            this.addSlot(new SlotItemHandler(handler, ShapingTableBlockEntity.OUTPUT_SLOT, 172, 42) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }

                @Override
                public boolean mayPickup(Player playerIn) {
                    return data.get(2) == 1;
                }

                @Override
                public void onTake(Player pPlayer, ItemStack pStack) {
                    super.onTake(pPlayer, pStack);
                    // 取出输出后，重置状态，但不触发方块更新，避免干扰客户端显示
                    blockEntity.resetAfterTake();
                }
            });
        });

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        addDataSlots(data);
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9 + 9;
                int x = 18 + col * 18;
                int y = 106 + row * 18;
                addSlot(new Slot(inv, index, x, y));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int col = 0; col < 9; col++) {
            int x = 18 + col * 18;
            int y = 164;
            addSlot(new Slot(inv, col, x, y));
        }
    }

    public ShapingTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        // ★ 制作过程中，输入槽不能被快速移走
        if (index == 0 && menuIsCrafting()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < 2) {
            if (!this.moveItemStackTo(stack, 2, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    private boolean menuIsCrafting() {
        int progress = data.get(0);
        int total = data.get(1);
        return progress > 0 && progress < total;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ForgingAndCraftingBlocks.SHAPING_TABLE.get());
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int total = data.get(1);
        int arrowWidth = 27;
        return total != 0 ? progress * arrowWidth / total : 0;
    }

    public boolean isCrafting() {
        return data.get(0) > 0 && data.get(0) < data.get(1);
    }

    public boolean isCraftFinished() {
        return data.get(2) == 1;
    }
}