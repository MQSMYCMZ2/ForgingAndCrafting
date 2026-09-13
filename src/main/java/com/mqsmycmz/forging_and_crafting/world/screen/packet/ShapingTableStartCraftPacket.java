package com.mqsmycmz.forging_and_crafting.world.screen.packet;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.entity.ShapingTableBlockEntity;
import com.mqsmycmz.forging_and_crafting.world.menu.ShapingTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class ShapingTableStartCraftPacket {
    public static void encode(ShapingTableStartCraftPacket message, FriendlyByteBuf buf) {

    }

    public static ShapingTableStartCraftPacket decode(FriendlyByteBuf buf) {
        return new ShapingTableStartCraftPacket();
    }

    public static void handle(ShapingTableStartCraftPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer player = contextSupplier.get().getSender();
            if (player == null) return;
            if (player.containerMenu instanceof ShapingTableMenu menu) {
                ShapingTableBlockEntity blockEntity = menu.getBlockEntity();
                if (blockEntity != null) {
                    blockEntity.startCrafting();
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public static void sendToServer() {
        ForgingAndCrafting.CHANNEL.send(PacketDistributor.SERVER.noArg(),
                new ShapingTableStartCraftPacket());
    }
}
