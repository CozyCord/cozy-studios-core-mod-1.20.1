package net.cozystudios.cozystudioscore.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.HashSet;
import java.util.Set;

public class TranquilLanternState extends PersistentState {

    public final Set<BlockPos> lanterns = new HashSet<>();

    public static TranquilLanternState create() {
        return new TranquilLanternState();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();

        for (BlockPos pos : lanterns) {
            NbtCompound tag = new NbtCompound();
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
            list.add(tag);
        }

        nbt.put("Lanterns", list);
        return nbt;
    }

    public static TranquilLanternState readNbt(NbtCompound nbt) {
        TranquilLanternState state = new TranquilLanternState();

        NbtList list = nbt.getList("Lanterns", 10);

        for (int i = 0; i < list.size(); i++) {
            NbtCompound tag = list.getCompound(i);
            BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            state.lanterns.add(pos);
        }

        return state;
    }
}