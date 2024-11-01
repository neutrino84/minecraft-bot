package com.lukes.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.ServerPlayerEntity;
import com.lukes.MBot;

public class BotSyncPacket {
    public static final Identifier BOT_SYNC = new Identifier(MBot.MOD_ID, "bot_sync");

    public static void sendToClient(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        
        // Add a timestamp or some data to ensure the packet triggers an update
        buf.writeLong(System.currentTimeMillis());
        ServerPlayNetworking.send(player, BOT_SYNC, buf);
    }
}
