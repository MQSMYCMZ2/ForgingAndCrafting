package com.mqsmycmz.forging_and_crafting.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CarrierDishBlockEntity extends BlockEntity {
    private ItemStack displayedItem = ItemStack.EMPTY;

    public CarrierDishBlockEntity(BlockPos pos, BlockState state) {
        super(ForgingAndCraftingBlockEntities.CARRIER_DISH.get(), pos, state); // 替换为您的实际注册类型
    }

    public ItemStack getDisplayedItem() {
        return displayedItem;
    }

    public void setDisplayedItem(ItemStack stack) {
        this.displayedItem = stack.copy();
        this.setChanged();
        syncToClient(); // 手动触发客户端同步
    }

    public void clearItem() {
        this.displayedItem = ItemStack.EMPTY;
        this.setChanged();
        syncToClient();
    }

    public boolean hasItem() {
        return !displayedItem.isEmpty();
    }

    // ---------- 网络同步核心方法 ----------
    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("DisplayedItem", displayedItem.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        displayedItem = ItemStack.of(tag.getCompound("DisplayedItem"));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        // 保存需要同步到客户端的数据
        CompoundTag tag = super.getUpdateTag();
        tag.put("DisplayedItem", displayedItem.save(new CompoundTag()));
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        // 客户端从更新标签中加载数据
        super.handleUpdateTag(tag);
        displayedItem = ItemStack.of(tag.getCompound("DisplayedItem"));
    }
}