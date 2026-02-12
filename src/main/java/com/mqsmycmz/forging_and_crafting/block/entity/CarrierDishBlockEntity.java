package com.mqsmycmz.forging_and_crafting.block.entity;

import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.item.ChiselItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class CarrierDishBlockEntity extends BlockEntity {
    private ItemStack displayedItem = ItemStack.EMPTY;

    // 凿矿相关数据
    public static final int MAX_HEIGHT = 8; // 最大高度（8个阶段）
    private static final int CHISEL_INTERVAL = 30; // 10秒 = 200 ticks
    private int granulesPerChisel = 3; // 每次产出数量，根据凿子尖锐程度动态变化

    private int remainingHeight = MAX_HEIGHT; // 剩余高度（0-8）
    private int chiselProgress = 0; // 当前凿矿进度（0-200）
    private boolean isChiseling = false; // 是否正在凿
    private int animationTick = 0; // 客户端动画计时器

    // 玩家方向（用于渲染凿子动画）
    private Direction playerDirection = Direction.NORTH;

    // 当前使用的凿子尖锐程度
    private int currentChiselSharpness = ChiselItem.BASE_SHARPNESS;

    // 当前使用凿子的玩家UUID和手持槽位（用于消耗耐久和增加尖锐程度）
    private UUID chiselingPlayerUUID = null;
    private InteractionHand chiselingHand = InteractionHand.MAIN_HAND;

    // 矿石到碎粒物品的映射
    public static final Map<Item, Item> ORE_TO_GRANULES = Map.of(
            Items.RAW_IRON_BLOCK, ForgingAndCraftingBlocks.IRON_ORE_GRANULES.get().asItem(),
            Items.RAW_GOLD_BLOCK, ForgingAndCraftingBlocks.GOLD_ORE_GRANULES.get().asItem(),
            Items.RAW_COPPER_BLOCK, ForgingAndCraftingBlocks.COPPER_ORE_GRANULES.get().asItem()
    );

    // 粗矿块到粗矿物品的映射
    public static final Map<Item, Item> ORE_BLOCK_TO_RAW_ORE = Map.of(
            Items.RAW_IRON_BLOCK, Items.RAW_IRON,
            Items.RAW_GOLD_BLOCK, Items.RAW_GOLD,
            Items.RAW_COPPER_BLOCK, Items.RAW_COPPER
    );

    public CarrierDishBlockEntity(BlockPos pos, BlockState state) {
        super(ForgingAndCraftingBlockEntities.CARRIER_DISH.get(), pos, state);
    }

    // ========== 基础物品管理 ==========

    public ItemStack getDisplayedItem() {
        return displayedItem;
    }

    public void setDisplayedItem(ItemStack stack) {
        this.displayedItem = stack.copy();
        this.remainingHeight = MAX_HEIGHT; // 重置高度
        this.chiselProgress = 0;
        this.isChiseling = false;
        this.playerDirection = Direction.NORTH;
        this.currentChiselSharpness = ChiselItem.BASE_SHARPNESS;
        this.granulesPerChisel = ChiselItem.BASE_GRANULES_DROP;
        this.chiselingPlayerUUID = null;
        this.chiselingHand = InteractionHand.MAIN_HAND;
        this.setChanged();
        syncToClient();
    }

    public void clearItem() {
        this.displayedItem = ItemStack.EMPTY;
        this.remainingHeight = MAX_HEIGHT;
        this.chiselProgress = 0;
        this.isChiseling = false;
        this.playerDirection = Direction.NORTH;
        this.currentChiselSharpness = ChiselItem.BASE_SHARPNESS;
        this.granulesPerChisel = ChiselItem.BASE_GRANULES_DROP;
        this.chiselingPlayerUUID = null;
        this.chiselingHand = InteractionHand.MAIN_HAND;
        this.setChanged();
        syncToClient();
    }

    public boolean hasItem() {
        return !displayedItem.isEmpty();
    }

    // ========== 凿矿相关方法 ==========

    public boolean hasChiselerOre() {
        return hasItem() && ORE_TO_GRANULES.containsKey(displayedItem.getItem());
    }

    public boolean isChiseling() {
        return isChiseling;
    }

    public int getRemainingHeight() {
        return remainingHeight;
    }

    public int getAnimationTick() {
        return animationTick;
    }

    public Direction getPlayerDirection() {
        return playerDirection;
    }

    // 开始凿矿（由凿子调用）
    public void startChiseling(Player player, InteractionHand hand, int sharpness, ItemStack chiselStack) {
        if (!hasChiselerOre() || remainingHeight <= 0 || isChiseling) {
            return;
        }

        // 计算玩家相对于方块的方向
        this.playerDirection = calculatePlayerDirection(player);

        // 设置凿子尖锐程度和对应的产出数量
        this.currentChiselSharpness = sharpness;
        this.granulesPerChisel = ChiselItem.calculateGranulesDrop(sharpness);

        // 保存玩家信息以便后续处理
        this.chiselingPlayerUUID = player.getUUID();
        this.chiselingHand = hand;

        this.isChiseling = true;
        this.chiselProgress = 0;
        this.setChanged();
        syncToClient();

        // 播放开始音效
        if (level != null) {
            level.playSound(null, worldPosition,
                    net.minecraft.sounds.SoundEvents.STONE_HIT,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    1.0f, 1.0f);
        }
    }

    // 计算玩家相对于方块的水平方向
    private Direction calculatePlayerDirection(Player player) {
        Vec3 playerPos = player.position();
        Vec3 blockPos = Vec3.atCenterOf(this.worldPosition);

        double dx = playerPos.x - blockPos.x;
        double dz = playerPos.z - blockPos.z;

        // 根据dx和dz判断主要方向
        if (Math.abs(dx) > Math.abs(dz)) {
            // 东西方向
            return dx > 0 ? Direction.EAST : Direction.WEST;
        } else {
            // 南北方向
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    // 停止凿矿（由凿子调用或自动调用）
    public void stopChiseling() {
        if (!isChiseling) {
            return; // 本来就没在凿
        }

        this.isChiseling = false;
        this.chiselProgress = 0;
        this.chiselingPlayerUUID = null;
        this.setChanged();
        syncToClient();

        // 播放停止音效
        if (level != null) {
            level.playSound(null, worldPosition,
                    net.minecraft.sounds.SoundEvents.STONE_STEP,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    0.5f, 0.8f);
        }
    }

    // 每tick调用（需要在Block中注册Ticker）
    public static void tick(Level level, BlockPos pos, BlockState state, CarrierDishBlockEntity be) {
        if (level.isClientSide) {
            be.clientTick();
        } else {
            be.serverTick();
        }
    }

    private void clientTick() {
        if (isChiseling) {
            animationTick++;
        } else {
            animationTick = 0;
        }
    }

    private void serverTick() {
        if (!isChiseling || !hasChiselerOre()) {
            return;
        }

        chiselProgress++;

        // 每10秒完成一次凿矿（200 ticks）
        if (chiselProgress >= CHISEL_INTERVAL) {
            completeChisel();
        } else {
            // 同步进度到客户端（每20tick同步一次，减少网络负载）
            if (chiselProgress % 20 == 0) {
                setChanged();
                syncToClient();
            }
        }
    }

    private void completeChisel() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // 处理凿子：增加尖锐程度、消耗耐久、显示消息
        processChiselAfterComplete();

        // 获取对应的碎粒物品
        Item granuleItem = ORE_TO_GRANULES.get(displayedItem.getItem());
        if (granuleItem != null) {
            // 使用根据凿子尖锐程度计算的产出数量
            ItemStack output = new ItemStack(granuleItem, granulesPerChisel);

            // 弹出物品
            double x = worldPosition.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
            double y = worldPosition.getY() + 0.8;
            double z = worldPosition.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;

            ItemEntity itemEntity = new ItemEntity(level, x, y, z, output);
            itemEntity.setDefaultPickUpDelay();
            itemEntity.setDeltaMovement(
                    level.random.nextGaussian() * 0.05,
                    0.2,
                    level.random.nextGaussian() * 0.05
            );
            level.addFreshEntity(itemEntity);
        }

        // 降低高度
        remainingHeight--;

        // 关键修复：如果高度为0，清空物品以消除"幽灵"物品
        if (remainingHeight <= 0) {
            // 完全凿完，不掉落剩余粗矿，直接清空
            this.displayedItem = ItemStack.EMPTY;
            remainingHeight = MAX_HEIGHT;
        }

        // 重置进度
        chiselProgress = 0;

        // 播放完成音效
        level.playSound(null, worldPosition,
                net.minecraft.sounds.SoundEvents.STONE_BREAK,
                net.minecraft.sounds.SoundSource.BLOCKS,
                1.0f, 0.8f + level.random.nextFloat() * 0.4f);

        // 停止凿矿
        isChiseling = false;
        chiselingPlayerUUID = null;

        this.setChanged();
        syncToClient();
    }

    /**
     * 完成凿矿后处理凿子：自动增加尖锐程度、消耗耐久、显示消息
     */
    private void processChiselAfterComplete() {
        if (chiselingPlayerUUID == null || level == null) {
            return;
        }

        Player player = level.getPlayerByUUID(chiselingPlayerUUID);
        if (player == null) {
            return;
        }

        ItemStack chiselStack = player.getItemInHand(chiselingHand);
        if (chiselStack.isEmpty() || !(chiselStack.getItem() instanceof ChiselItem)) {
            return;
        }

        // 先增加尖锐程度（必须在消耗耐久之前，防止耐久耗尽破坏后无法增加）
        int newSharpness = ChiselItem.increaseSharpness(chiselStack);

        // 播放磨凿音效
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.GRINDSTONE_USE,
                net.minecraft.sounds.SoundSource.PLAYERS,
                0.5f, 1.0f);

        // 显示磨凿成功消息
        player.displayClientMessage(Component.translatable("message.forging_and_crafting.chisel_sharpness",
                newSharpness), true);

        // 消耗耐久
        int currentDamage = chiselStack.getDamageValue();
        int maxDamage = chiselStack.getMaxDamage();

        if (currentDamage + ChiselItem.DURABILITY_COST_PER_CHISEL >= maxDamage) {
            // 耐久耗尽，破坏凿子
            chiselStack.shrink(1);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.8f, 0.8f + level.random.nextFloat() * 0.4f);
        } else {
            chiselStack.setDamageValue(currentDamage + ChiselItem.DURABILITY_COST_PER_CHISEL);
        }
    }

    // 获取应该掉落的粗矿数量（根据剩余高度计算）
    public int getRemainingRawOreCount() {
        if (!hasItem() || !ORE_BLOCK_TO_RAW_ORE.containsKey(displayedItem.getItem())) {
            return 0;
        }

        // 如果高度已满（未开始凿），返回0表示应该掉落原物品
        if (remainingHeight >= MAX_HEIGHT) {
            return -1; // 特殊标记：返回原物品
        }

        // 计算剩余粗矿数量：8 - (remainingHeight / 2)
        int maxDrop = 8 - (remainingHeight / 2);
        return level != null ? level.random.nextInt(maxDrop + 1) : 0;
    }

    // 获取对应的粗矿物品类型
    public Item getRawOreItem() {
        if (!hasItem()) return null;
        return ORE_BLOCK_TO_RAW_ORE.get(displayedItem.getItem());
    }

    // ========== 数据同步 ==========

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("DisplayedItem", displayedItem.save(new CompoundTag()));
        tag.putInt("RemainingHeight", remainingHeight);
        tag.putInt("ChiselProgress", chiselProgress);
        tag.putBoolean("IsChiseling", isChiseling);
        tag.putInt("PlayerDirection", playerDirection.get2DDataValue());
        tag.putInt("ChiselSharpness", currentChiselSharpness);
        tag.putInt("GranulesPerChisel", granulesPerChisel);
        if (chiselingPlayerUUID != null) {
            tag.putUUID("ChiselingPlayer", chiselingPlayerUUID);
        }
        tag.putInt("ChiselingHand", chiselingHand == InteractionHand.MAIN_HAND ? 0 : 1);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        displayedItem = ItemStack.of(tag.getCompound("DisplayedItem"));
        remainingHeight = tag.getInt("RemainingHeight");
        if (remainingHeight == 0 && hasItem()) remainingHeight = MAX_HEIGHT;
        chiselProgress = tag.getInt("ChiselProgress");
        isChiseling = tag.getBoolean("IsChiseling");
        playerDirection = Direction.from2DDataValue(tag.getInt("PlayerDirection"));
        currentChiselSharpness = tag.getInt("ChiselSharpness");
        if (currentChiselSharpness == 0) currentChiselSharpness = ChiselItem.BASE_SHARPNESS;
        granulesPerChisel = tag.getInt("GranulesPerChisel");
        if (granulesPerChisel == 0) granulesPerChisel = ChiselItem.BASE_GRANULES_DROP;
        if (tag.hasUUID("ChiselingPlayer")) {
            chiselingPlayerUUID = tag.getUUID("ChiselingPlayer");
        }
        chiselingHand = tag.getInt("ChiselingHand") == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("DisplayedItem", displayedItem.save(new CompoundTag()));
        tag.putInt("RemainingHeight", remainingHeight);
        tag.putInt("ChiselProgress", chiselProgress);
        tag.putBoolean("IsChiseling", isChiseling);
        tag.putInt("PlayerDirection", playerDirection.get2DDataValue());
        tag.putInt("ChiselSharpness", currentChiselSharpness);
        tag.putInt("GranulesPerChisel", granulesPerChisel);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        displayedItem = ItemStack.of(tag.getCompound("DisplayedItem"));
        remainingHeight = tag.getInt("RemainingHeight");
        chiselProgress = tag.getInt("ChiselProgress");
        isChiseling = tag.getBoolean("IsChiseling");
        playerDirection = Direction.from2DDataValue(tag.getInt("PlayerDirection"));
        currentChiselSharpness = tag.getInt("ChiselSharpness");
        granulesPerChisel = tag.getInt("GranulesPerChisel");
    }
}