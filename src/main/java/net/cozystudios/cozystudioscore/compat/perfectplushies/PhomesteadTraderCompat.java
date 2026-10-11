package net.cozystudios.cozystudioscore.compat.perfectplushies;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PhomesteadTraderCompat {

    private static final int[] RARE_SLOTS = {7, 9, 13, 16, 17, 18, 19, 20, 21, 23};
    private static final int[] EPIC_SLOTS = {48, 49, 50};

    private static final String[] PP_RARE = {
            "daniel_plushie",
            "geode_plushie",
            "june_plushie",
            "nyf_plushie",
            "rocris_plushie",
            "silk_plushie",
            "sirjain_plushie",
            "sizableshrimp_plushie",
            "tslat_plushie"
    };
    private static final String[] PP_EPIC = {
            "gamerpotion_plushie_rare",
            "sirjain_plushie_rare"
    };

    private static final int COMMON_COST = 10;
    private static final int RARE_COST = 20;
    private static final int EPIC_COST = 32;

    private static boolean injected = false;

    private PhomesteadTraderCompat() {}

    public static void register() {
        FabricLoader loader = FabricLoader.getInstance();
        if (!loader.isModLoaded("perfectplushieapi")) return;
        if (!loader.isModLoaded("phomesteadplushies")) return;

        ServerLifecycleEvents.SERVER_STARTING.register(server -> injectOnce());
    }

    private static synchronized void injectOnce() {
        if (injected) return;
        injected = true;

        try {
            Class<?> traderClass = Class.forName("com.nyfaria.perfectplushieapi.entity.WanderingPlushieTrader");
            Field tradesField = traderClass.getField("TRADES");
            @SuppressWarnings("unchecked")
            Int2ObjectMap<TradeOffers.Factory[]> trades =
                    (Int2ObjectMap<TradeOffers.Factory[]>) tradesField.get(null);

            Set<Integer> rare = new HashSet<>();
            for (int s : RARE_SLOTS) rare.add(s);
            Set<Integer> epic = new HashSet<>();
            for (int s : EPIC_SLOTS) epic.add(s);

            List<TradeOffers.Factory> commonAdds = new ArrayList<>();
            List<TradeOffers.Factory> rareAdds = new ArrayList<>();
            List<TradeOffers.Factory> epicAdds = new ArrayList<>();

            for (int i = 1; i <= 50; i++) {
                Identifier id = new Identifier("phomesteadplushies", "homestead_player_" + i);
                Block block = Registries.BLOCK.get(id);
                if (block == Blocks.AIR) continue;

                if (epic.contains(i)) {
                    epicAdds.add(new TradeOffers.SellItemFactory(block, EPIC_COST, 1, 1, 1));
                } else if (rare.contains(i)) {
                    rareAdds.add(new TradeOffers.SellItemFactory(block, RARE_COST, 1, 1, 1));
                } else {
                    commonAdds.add(new TradeOffers.SellItemFactory(block, COMMON_COST, 1, 1, 1));
                }
            }

            for (String name : PP_RARE) {
                Block block = Registries.BLOCK.get(new Identifier("perfectplushies", name));
                if (block == Blocks.AIR) continue;
                rareAdds.add(new TradeOffers.SellItemFactory(block, RARE_COST, 1, 1, 1));
            }
            for (String name : PP_EPIC) {
                Block block = Registries.BLOCK.get(new Identifier("perfectplushies", name));
                if (block == Blocks.AIR) continue;
                epicAdds.add(new TradeOffers.SellItemFactory(block, EPIC_COST, 1, 1, 1));
            }

            trades.put(2, merge(trades.get(2), commonAdds));
            trades.put(3, merge(trades.get(3), rareAdds));
            trades.put(4, merge(trades.get(4), epicAdds));

            CozyStudiosCore.LOGGER.info(
                    "Injected plushie trades into Wandering Plushie Trader: {} common, {} rare, {} epic",
                    commonAdds.size(), rareAdds.size(), epicAdds.size());
        } catch (Throwable t) {
            CozyStudiosCore.LOGGER.warn("Failed to inject Homestead plushies into Wandering Plushie Trader", t);
        }
    }

    private static TradeOffers.Factory[] merge(TradeOffers.Factory[] existing, List<TradeOffers.Factory> toAdd) {
        if (existing == null) existing = new TradeOffers.Factory[0];
        TradeOffers.Factory[] result = Arrays.copyOf(existing, existing.length + toAdd.size());
        for (int i = 0; i < toAdd.size(); i++) {
            result[existing.length + i] = toAdd.get(i);
        }
        return result;
    }
}
