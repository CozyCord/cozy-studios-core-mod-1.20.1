package net.cozystudios.cozystudioscore.network;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.client.TranquilLanternClientState;
import net.cozystudios.cozystudioscore.client.render.TranquilLanternRadiusRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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

    public static final Identifier TRANQUIL_LANTERN_REQUEST_SYNC =
            new Identifier(CozyStudiosCore.MOD_ID, "tranquil_lantern_request_sync");

    public static final Identifier TRANQUIL_LANTERN_CONFIG_SYNC =
            new Identifier(CozyStudiosCore.MOD_ID, "tranquil_lantern_config_sync");

    public static void init() {}

    public static void initClient() {

        // Receive config sync from server
        ClientPlayNetworking.registerGlobalReceiver(TRANQUIL_LANTERN_CONFIG_SYNC, (client, handler, buf, responseSender) -> {
            int tranquilRadius = buf.readInt();
            int goldenRadius = buf.readInt();
            int diamondRadius = buf.readInt();
            int netheriteRadius = buf.readInt();

            client.execute(() -> {
                TranquilLanternClientState.setServerRadiusValues(tranquilRadius, goldenRadius, diamondRadius, netheriteRadius);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(TRANQUIL_LANTERN_SYNC, (client, handler, buf, responseSender) -> {
            int size = buf.readInt();
            Set<BlockPos> positions = new HashSet<>(size);

            for (int i = 0; i < size; i++) {
                positions.add(buf.readBlockPos().toImmutable());
            }

            client.execute(() -> {
                TranquilLanternClientState.setAll(positions);

                if (TranquilLanternRadiusRenderer.SHOW_RADIUS) {
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(TRANQUIL_LANTERN_ADD, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos().toImmutable();
            client.execute(() -> {
                TranquilLanternClientState.add(pos);

                if (TranquilLanternRadiusRenderer.SHOW_RADIUS) {
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(TRANQUIL_LANTERN_REMOVE, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos().toImmutable();
            client.execute(() -> {
                TranquilLanternClientState.remove(pos);

                if (TranquilLanternRadiusRenderer.SHOW_RADIUS) {
                }
            });
        });
    }

    public static void requestLanternSyncFromServer() {
        if (ClientPlayNetworking.canSend(TRANQUIL_LANTERN_REQUEST_SYNC)) {
            PacketByteBuf buf = PacketByteBufs.create();
            ClientPlayNetworking.send(TRANQUIL_LANTERN_REQUEST_SYNC, buf);
        }
    }
}