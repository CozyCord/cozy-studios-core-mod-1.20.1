package net.cozystudios.cozystudioscore.save;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FirstJoinGiftState extends PersistentState {
    private static final String KEY = "cozystudioscore_first_join_gift";
    private static final String TAG = "gifted";
    private final Set<UUID> gifted = new HashSet<>();

    public static FirstJoinGiftState get(MinecraftServer server) {
        PersistentStateManager mgr = server.getOverworld().getPersistentStateManager();
        return mgr.getOrCreate(FirstJoinGiftState::fromNbt, FirstJoinGiftState::new, KEY);
    }

    public boolean markIfNew(UUID uuid) {
        boolean added = gifted.add(uuid);
        if (added) this.markDirty();
        return added;
    }

    public static FirstJoinGiftState fromNbt(NbtCompound nbt) {
        FirstJoinGiftState s = new FirstJoinGiftState();
        NbtList list = nbt.getList(TAG, NbtElement.STRING_TYPE);
        for (int i = 0; i < list.size(); i++) {
            s.gifted.add(UUID.fromString(list.getString(i)));
        }
        return s;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (UUID id : gifted) list.add(NbtString.of(id.toString()));
        nbt.put(TAG, list);
        return nbt;
    }
}