package com.mqsmycmz.forging_and_crafting.world.menu;

import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.block.entity.RockCrusherBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class RockCrusherMenu extends AbstractContainerMenu {
    public final RockCrusherBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public RockCrusherMenu(int pContainerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(pContainerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public RockCrusherMenu(int pContainerId, Inventory inventory , BlockEntity entity, ContainerData data) {
        super(ForgingAndCraftingMenuTypes.ROCK_CRUSHER_MENU.get(), pContainerId);
        checkContainerSize(inventory, 2);
        blockEntity = ((RockCrusherBlockEntity) entity);
        this.level =inventory.player.level();
        this.data = data;

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 10, 38));
            this.addSlot(new SlotItemHandler(iItemHandler, 1, 171, 38));
        });

        addDataSlots(data);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 2;

    //快捷移动物品(shift)
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // 1. 玩家背包/工具栏 → 方块槽（不能放进输出槽 3）
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // 目标范围：0~2（输入槽），跳过 3（输出槽）
            if (!moveItemStackTo(sourceStack,
                    TE_INVENTORY_FIRST_SLOT_INDEX,
                    TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT - 1,  // 3 号排除
                    false)) {
                return ItemStack.EMPTY;
            }
        }
        // 2. 方块槽 → 玩家背包/工具栏
        else if (index >= TE_INVENTORY_FIRST_SLOT_INDEX &&
                index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack,
                    VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
                    false)) {
                return ItemStack.EMPTY;
            }
        }
        else {
            // 理论上不会进入
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ForgingAndCraftingBlocks.ROCK_CRUSHER.get());
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9 + 9;
                int x = 18 + col * 18;
                int y = 102 + row * 18;
                addSlot(new Slot(inv, index, x, y));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int col = 0; col < 9; col++) {
            int x = 18 + col * 18;
            int y = 160;
            addSlot(new Slot(inv, col, x, y));
        }
    }

    //提供entity的方法
    public RockCrusherBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    //把当前进度（0 ~ maxProgress）线性映射成 GUI 上进度条的实际像素宽度。
    public int getScaledProgress() {
        int progress = this.data.get(0);//当前进度(progress)
        int maxProgress = this.data.get(1);//最大进度(maxProgress)
        int progressGearSize = 122; //宽度 (weight)

        return maxProgress != 0 && progress != 0 ? progress * progressGearSize / maxProgress : 0;
    }

    //当progress不等于0时，认定其为正在工作，以便其他调用
    public boolean isCrafting() {
        return data.get(0) > 0;
    }
}
