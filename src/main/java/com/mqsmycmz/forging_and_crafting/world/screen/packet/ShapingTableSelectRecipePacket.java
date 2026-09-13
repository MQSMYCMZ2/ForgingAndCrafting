package com.mqsmycmz.forging_and_crafting.world.screen.packet;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.entity.ShapingTableBlockEntity;
import com.mqsmycmz.forging_and_crafting.world.menu.ShapingTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class ShapingTableSelectRecipePacket {
    private final ResourceLocation recipeId;

    public ShapingTableSelectRecipePacket(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public static void encode(ShapingTableSelectRecipePacket message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.recipeId);
    }

    public static ShapingTableSelectRecipePacket decode(FriendlyByteBuf buf) {
        return new ShapingTableSelectRecipePacket(buf.readResourceLocation());
    }

    public static void handle(ShapingTableSelectRecipePacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer player = contextSupplier.get().getSender();
            if (player == null) return;
            if (player.containerMenu instanceof ShapingTableMenu menu) {
                ShapingTableBlockEntity blockEntity = menu.getBlockEntity();
                if (blockEntity != null) {
                    blockEntity.handleRecipeClick(message.recipeId, player);
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public static void sendToServer(ResourceLocation recipeId) {
        ForgingAndCrafting.CHANNEL.send(PacketDistributor.SERVER.noArg(),
                new ShapingTableSelectRecipePacket(recipeId));
    }
}
