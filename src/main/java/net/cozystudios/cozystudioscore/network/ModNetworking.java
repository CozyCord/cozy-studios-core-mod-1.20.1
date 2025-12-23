package net.cozystudios.cozystudioscore.network;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.client.TranquilLanternClientState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class ModNetworking {

    public static final Identifier TRANQUIL_LANTERN_ADD =
            new Identifier(CozyStudiosCore.MOD_ID, "tranquil_lantern_add");

    public static final Identifier TRANQUIL_LANTERN_REMOVE =
            new Identifier(CozyStudiosCore.MOD_ID, "tranquil_lantern_remove");

    public static final Identifier TRANQUIL_LANTERN_SYNC =
            new Identifier(CozyStudiosCore.MOD_ID, "tranquil_lantern_sync");

    public static void initClient() {

        ClientPlayNetworking.registerGlobalReceiver(
                TRANQUIL_LANTERN_SYNC,
                (client, handler, buf, responseSender) -> {
                    Set<BlockPos> lanterns = readLanternSet(buf);
                    client.execute(() ->
                            TranquilLanternClientState.setAll(lanterns)
                    );
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                TRANQUIL_LANTERN_ADD,
                (client, handler, buf, responseSender) -> {
                    BlockPos pos = buf.readBlockPos();
                    client.execute(() ->
                            TranquilLanternClientState.add(pos)
                    );
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                TRANQUIL_LANTERN_REMOVE,
                (client, handler, buf, responseSender) -> {
                    BlockPos pos = buf.readBlockPos();
                    client.execute(() ->
                            TranquilLanternClientState.remove(pos)
                    );
                }
        );

        ClientPlayConnectionEvents.DISCONNECT.register(
                (handler, client) -> TranquilLanternClientState.clear()
        );
    }


    private static Set<BlockPos> readLanternSet(PacketByteBuf buf) {
        int size = buf.readInt();
        Set<BlockPos> set = new HashSet<>(Math.max(16, size));
        for (int i = 0; i < size; i++) {
            set.add(buf.readBlockPos());
        }
        return set;
    }
}