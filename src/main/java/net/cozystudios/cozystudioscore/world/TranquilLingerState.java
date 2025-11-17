package net.cozystudios.cozystudioscore.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TranquilLingerState extends PersistentState {

    public final Map<UUID, Integer> lingerTicks = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();

        for (Map.Entry<UUID, Integer> e : lingerTicks.entrySet()) {
            NbtCompound tag = new NbtCompound();
            tag.putUuid("uuid", e.getKey());
            tag.putInt("ticks", e.getValue());
            list.add(tag);
        }

        nbt.put("entries", list);
        return nbt;
    }

    public static TranquilLingerState readNbt(NbtCompound nbt) {
        TranquilLingerState state = new TranquilLingerState();

        NbtList list = nbt.getList("entries", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : list) {
            NbtCompound tag = (NbtCompound) element;
            UUID id = tag.getUuid("uuid");
            int ticks = tag.getInt("ticks");
            state.lingerTicks.put(id, ticks);
        }

        return state;
    }

    public static TranquilLingerState get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                TranquilLingerState::readNbt,
                TranquilLingerState::new,
                "tranquil_linger"
        );
    }
}