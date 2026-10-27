package net.cozystudios.cozystudioscore.item.custom;

import net.cozystudios.cozystudioscore.network.ModNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class BisectCookieItem extends Item {

    public static final FoodComponent FOOD = new FoodComponent.Builder()
            .hunger(20)
            .saturationModifier(1.0f)
            .alwaysEdible()
            .build();

    private static final int BISECT_BLUE = 0x00DEFF;
    private static final String BISECT_URL = "https://bisecthosting.com/tokimi";
    private static final DustParticleEffect BISECT_DUST =
            new DustParticleEffect(new Vector3f(0f / 255f, 222f / 255f, 255f / 255f), 1.4f);

    public BisectCookieItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            player.setHealth(player.getMaxHealth());

            ServerWorld serverWorld = player.getServerWorld();
            Vec3d pos = player.getPos();
            serverWorld.spawnParticles(BISECT_DUST,
                    pos.x, pos.y + 1.0, pos.z,
                    60,
                    0.6, 0.9, 0.6,
                    0.02);

            serverWorld.playSound(null,
                    player.getBlockPos(),
                    SoundEvents.ITEM_TOTEM_USE,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.2f);

            ServerPlayNetworking.send(player, ModNetworking.BISECT_COOKIE_FLOAT, PacketByteBufs.empty());

            MutableText message = Text.literal("You've unlocked the power of BisectHosting! Start a server with your friends and use code ")
                    .formatted(Formatting.WHITE)
                    .append(Text.literal("TOKIMI").styled(s -> s
                            .withColor(BISECT_BLUE)
                            .withBold(true)))
                    .append(Text.literal(" at checkout for 25% off your first month. ").formatted(Formatting.WHITE))
                    .append(Text.literal("[Click here]").styled(s -> s
                            .withColor(BISECT_BLUE)
                            .withBold(true)
                            .withUnderline(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, BISECT_URL))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Text.literal(BISECT_URL).styled(h -> h.withColor(BISECT_BLUE))))));

            player.sendMessage(message, false);
        }

        return result;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Unlock the power of...").styled(s -> s
                .withColor(BISECT_BLUE)
                .withItalic(true)));
    }
}
