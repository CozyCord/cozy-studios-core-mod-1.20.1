package net.cozystudios.cozystudioscore.util;

import net.cozystudios.cozystudioscore.config.GeneralConfig;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

public class ModMasonTrades {

    public static void registerCustomTrades() {
        if (!GeneralConfig.get().enableMasonTrades) return;

        TradeOfferHelper.registerVillagerOffers(
                VillagerProfession.MASON,
                TradeLevel.NOVICE,
                factories -> factories.add(new EmeraldToItemOffer(
                        new ItemStack(Items.PACKED_MUD, 1), 1, 16, 2, 0.2f))
        );

        TradeOfferHelper.registerVillagerOffers(
                VillagerProfession.MASON,
                TradeLevel.APPRENTICE,
                factories -> {
                    factories.add(new EmeraldToItemOffer(new ItemStack(Items.BRICKS, 4), 1, 16, 5, 0.2f));
                    factories.add(new EmeraldToItemOffer(new ItemStack(Items.MUD_BRICKS, 4), 1, 16, 5, 0.2f));
                }
        );

        TradeOfferHelper.registerVillagerOffers(
                VillagerProfession.MASON,
                TradeLevel.JOURNEYMAN,
                factories -> {
                    factories.add(new EmeraldToItemOffer(new ItemStack(Items.TUFF, 4), 1, 16, 10, 0.2f));
                    factories.add(new EmeraldToItemOffer(new ItemStack(Items.TERRACOTTA, 1), 1, 16, 10, 0.2f));
                }
        );

        TradeOfferHelper.registerVillagerOffers(
                VillagerProfession.MASON,
                TradeLevel.MASTER,
                factories -> factories.add(new EmeraldToItemOffer(
                        new ItemStack(Items.CALCITE, 1), 1, 8, 20, 0.2f))
        );
    }

    private static final class TradeLevel {
        public static final int NOVICE = 1;
        public static final int APPRENTICE = 2;
        public static final int JOURNEYMAN = 3;
        public static final int EXPERT = 4;
        public static final int MASTER = 5;
    }

    private record EmeraldToItemOffer(
            ItemStack sell, int price, int maxUses, int experience, float multiplier
    ) implements TradeOffers.Factory {
        @Override
        public TradeOffer create(Entity entity, Random random) {
            return new TradeOffer(
                    new ItemStack(Items.EMERALD, this.price),
                    this.sell,
                    this.maxUses,
                    this.experience,
                    this.multiplier
            );
        }
    }
}