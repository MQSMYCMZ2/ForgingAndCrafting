package com.mqsmycmz.forging_and_crafting.mixin;

import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public class BlockMixin {

    // 这个方法是带玩家和工具的版本
    @Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("HEAD"), cancellable = true)
    private static void onGetDrops(BlockState state, ServerLevel level, BlockPos pos,
                                   @Nullable BlockEntity blockEntity, @Nullable net.minecraft.world.entity.Entity entity,
                                   ItemStack tool, CallbackInfoReturnable<List<ItemStack>> cir) {

        // 只处理玩家破坏的情况
        if (!(entity instanceof Player player)) {
            return;
        }

        Block block = state.getBlock();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);

        if (blockId == null) {
            return;
        }

        String blockName = blockId.toString();

        if (blockName.equals("minecraft:iron_ore") ||
                blockName.equals("minecraft:deepslate_iron_ore")) {

            if (hasSilkTouch(player)) {
                return; // 使用精准采集时不修改
            }

            handleGranulesDrop(cir, player, ForgingAndCraftingBlocks.IRON_ORE_GRANULES.get(), level);
        }
        else if (blockName.equals("minecraft:gold_ore") ||
                blockName.equals("minecraft:deepslate_gold_ore")) {

            if (hasSilkTouch(player)) {
                return;
            }

            handleGranulesDrop(cir, player, ForgingAndCraftingBlocks.GOLD_ORE_GRANULES.get(), level);
        }
        else if (blockName.equals("minecraft:copper_ore") ||
                blockName.equals("minecraft:deepslate_copper_ore")) {

            if (hasSilkTouch(player)) {
                return;
            }

            handleGranulesDrop(cir, player, ForgingAndCraftingBlocks.COPPER_ORE_GRANULES.get(), level);
        }
    }

    private static boolean hasSilkTouch(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        return EnchantmentHelper.getTagEnchantmentLevel(Enchantments.SILK_TOUCH, mainHand) > 0 ||
                EnchantmentHelper.getTagEnchantmentLevel(Enchantments.SILK_TOUCH, offHand) > 0;
    }

    private static void handleGranulesDrop(CallbackInfoReturnable<List<ItemStack>> cir,
                                           Player player, Block granulesBlock, Level level) {

        int fortuneLevel = EnchantmentHelper.getTagEnchantmentLevel(
                Enchantments.BLOCK_FORTUNE, player.getMainHandItem()
        );

        int count = 1;

        if (fortuneLevel > 0) {
            count += level.random.nextInt(fortuneLevel + 1);
        }

        List<ItemStack> drops = new ArrayList<>();
        ItemStack granules = new ItemStack(granulesBlock.asItem(), count);
        drops.add(granules);

        cir.setReturnValue(drops);
    }
}