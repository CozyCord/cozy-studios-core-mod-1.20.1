package net.cozystudios.cozystudioscore.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum MushlingVariant {
    DEFAULT(0),
    ICE(1),
    CRIMSON(2),
    LUSH(3),
    WARPED(4);

    private static final MushlingVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(MushlingVariant::getId)).toArray(MushlingVariant[]::new);
    private final int id;

    MushlingVariant(int id) { this.id = id; }

    public int getId() { return this.id; }

    public static MushlingVariant byId(int id) { return BY_ID[id % BY_ID.length];}
}
