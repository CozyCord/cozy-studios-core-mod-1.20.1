package net.cozystudios.cozystudioscore.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum FernlingVariant {
    DEFAULT(0),
    DESERT(1),
    ICE(2),
    SWAMP(3);

    private static final FernlingVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(FernlingVariant::getId)).toArray(FernlingVariant[]::new);
    private final int id;

    FernlingVariant(int id) { this.id = id; }

    public int getId() { return this.id; }

    public static FernlingVariant byId(int id) { return BY_ID[id % BY_ID.length];}
}
