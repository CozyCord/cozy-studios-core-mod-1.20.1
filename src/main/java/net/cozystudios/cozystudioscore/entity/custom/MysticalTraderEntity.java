package net.cozystudios.cozystudioscore.entity.custom;

import net.cozystudios.cozystudioscore.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;

public class MysticalTraderEntity extends WanderingTraderEntity {

    public MysticalTraderEntity(EntityType<? extends WanderingTraderEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return VillagerEntity.createVillagerAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D);
    }

    @Override
    protected void fillRecipes() {
        TradeOfferList offers = this.getOffers();
        offers.clear();


        int berryCost = 4 + this.random.nextInt(5);
        int berryCount = 2 + this.random.nextInt(4);
        offers.add(new TradeOffer(
                new ItemStack(Items.EMERALD, berryCost),
                new ItemStack(ModItems.MYSTICAL_BERRIES, berryCount),
                8, 2, 0.05f));


        int crumbCost = 2 + this.random.nextInt(3);
        int crumbCount = 3 + this.random.nextInt(5);
        offers.add(new TradeOffer(
                new ItemStack(Items.EMERALD, crumbCost),
                new ItemStack(ModItems.COZY_CRUMBS, crumbCount),
                8, 2, 0.05f));


        if (this.random.nextFloat() < 0.15f) {
            int leafCost = 48 + this.random.nextInt(17);
            offers.add(new TradeOffer(
                    new ItemStack(Items.EMERALD, leafCost),
                    new ItemStack(ModItems.GOLDEN_LEAF, 1),
                    1, 10, 0.2f));
        }
    }

    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_VILLAGER_AMBIENT, 1.0F, 1.0F);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public void onSellingItem(ItemStack stack) {
        super.onSellingItem(stack);
    }

    private int despawnTimer = 0;

    public void setDespawnTimer(int ticks) {
        this.despawnTimer = ticks;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient && despawnTimer > 0) {
            despawnTimer--;
            if (despawnTimer <= 0 && !this.hasPassengers()) {
                this.discard();
            }
        }
    }
}