package net.cozystudios.cozystudioscore.events;

import net.cozystudios.cozystudioscore.config.ModConfig;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static net.minecraft.block.Blocks.*;
import static net.minecraft.block.Block.*; // <-- for NOTIFY_* / SKIP_DROPS / FORCE_STATE flags

public final class RightClickRecolorHandler {
    private RightClickRecolorHandler() {}

    public static void register() {
        UseBlockCallback.EVENT.register(RightClickRecolorHandler::onUseBlock);
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        ModConfig cfg = ModConfig.get();
        if (!cfg.enableRightClickRecolor) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);
        if (!(held.getItem() instanceof DyeItem dyeItem)) return ActionResult.PASS;

        if (world.isClient) return ActionResult.SUCCESS;

        DyeColor target = dyeItem.getColor();
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // Wool
        if (cfg.recolorWool && state.isIn(BlockTags.WOOL)) {
            Block newWool = woolFor(target);
            if (newWool != null && block != newWool) {
                world.setBlockState(pos, newWool.getDefaultState(), NOTIFY_ALL);
                if (!player.isCreative()) held.decrement(1);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }

        // Beds
        if (cfg.recolorBeds && block instanceof BedBlock) {
            Block newBed = bedFor(target);
            if (newBed == null || block == newBed) return ActionResult.PASS;

            if (state.get(BedBlock.OCCUPIED)) return ActionResult.PASS;

            Direction facing = state.get(BedBlock.FACING);
            var part = state.get(BedBlock.PART);

            BlockPos headPos = (part == net.minecraft.block.enums.BedPart.HEAD) ? pos : pos.offset(facing);
            BlockPos footPos = headPos.offset(facing.getOpposite());

            BlockState headState = world.getBlockState(headPos);
            BlockState footState = world.getBlockState(footPos);
            if (!(headState.getBlock() instanceof BedBlock) || !(footState.getBlock() instanceof BedBlock)) {
                return ActionResult.PASS;
            }

            BlockState newHead = newBed.getDefaultState()
                    .with(BedBlock.FACING, facing)
                    .with(BedBlock.PART, net.minecraft.block.enums.BedPart.HEAD)
                    .with(BedBlock.OCCUPIED, false);

            BlockState newFoot = newBed.getDefaultState()
                    .with(BedBlock.FACING, facing)
                    .with(BedBlock.PART, net.minecraft.block.enums.BedPart.FOOT)
                    .with(BedBlock.OCCUPIED, false);

            final int flags = NOTIFY_ALL | SKIP_DROPS | FORCE_STATE;
            world.setBlockState(headPos, newHead, flags);
            world.setBlockState(footPos, newFoot, flags);

            if (!player.isCreative()) held.decrement(1);
            return ActionResult.SUCCESS;
        }

        // Banners
        if (cfg.recolorBanners && (block instanceof BannerBlock || block instanceof WallBannerBlock)) {
            boolean isWall = block instanceof WallBannerBlock;

            NbtCompound bannerNbt = null;
            var be = world.getBlockEntity(pos);
            if (be instanceof BannerBlockEntity bbe) {
                bannerNbt = bbe.createNbt();
            }

            Block newBanner = bannerFor(target, isWall);
            if (newBanner == null || block == newBanner) return ActionResult.PASS;

            if (isWall) {
                Direction facing = state.get(WallBannerBlock.FACING);
                world.setBlockState(pos, newBanner.getDefaultState().with(WallBannerBlock.FACING, facing), NOTIFY_ALL);
            } else {
                int rot = state.get(BannerBlock.ROTATION);
                world.setBlockState(pos, newBanner.getDefaultState().with(BannerBlock.ROTATION, rot), NOTIFY_ALL);
            }

            if (bannerNbt != null) {
                bannerNbt.putInt("Base", target.getId());
                var newBe = world.getBlockEntity(pos);
                if (newBe instanceof BannerBlockEntity newBBE) {
                    newBBE.readNbt(bannerNbt);
                    newBBE.markDirty();
                }
            }

            if (!player.isCreative()) held.decrement(1);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }


    private static Block woolFor(DyeColor c) {
        return switch (c) {
            case WHITE -> WHITE_WOOL;
            case ORANGE -> ORANGE_WOOL;
            case MAGENTA -> MAGENTA_WOOL;
            case LIGHT_BLUE -> LIGHT_BLUE_WOOL;
            case YELLOW -> YELLOW_WOOL;
            case LIME -> LIME_WOOL;
            case PINK -> PINK_WOOL;
            case GRAY -> GRAY_WOOL;
            case LIGHT_GRAY -> LIGHT_GRAY_WOOL;
            case CYAN -> CYAN_WOOL;
            case PURPLE -> PURPLE_WOOL;
            case BLUE -> BLUE_WOOL;
            case BROWN -> BROWN_WOOL;
            case GREEN -> GREEN_WOOL;
            case RED -> RED_WOOL;
            case BLACK -> BLACK_WOOL;
        };
    }

    private static Block bedFor(DyeColor c) {
        return switch (c) {
            case WHITE -> WHITE_BED;
            case ORANGE -> ORANGE_BED;
            case MAGENTA -> MAGENTA_BED;
            case LIGHT_BLUE -> LIGHT_BLUE_BED;
            case YELLOW -> YELLOW_BED;
            case LIME -> LIME_BED;
            case PINK -> PINK_BED;
            case GRAY -> GRAY_BED;
            case LIGHT_GRAY -> LIGHT_GRAY_BED;
            case CYAN -> CYAN_BED;
            case PURPLE -> PURPLE_BED;
            case BLUE -> BLUE_BED;
            case BROWN -> BROWN_BED;
            case GREEN -> GREEN_BED;
            case RED -> RED_BED;
            case BLACK -> BLACK_BED;
        };
    }

    private static Block bannerFor(DyeColor c, boolean wall) {
        if (wall) {
            return switch (c) {
                case WHITE -> WHITE_WALL_BANNER;
                case ORANGE -> ORANGE_WALL_BANNER;
                case MAGENTA -> MAGENTA_WALL_BANNER;
                case LIGHT_BLUE -> LIGHT_BLUE_WALL_BANNER;
                case YELLOW -> YELLOW_WALL_BANNER;
                case LIME -> LIME_WALL_BANNER;
                case PINK -> PINK_WALL_BANNER;
                case GRAY -> GRAY_WALL_BANNER;
                case LIGHT_GRAY -> LIGHT_GRAY_WALL_BANNER;
                case CYAN -> CYAN_WALL_BANNER;
                case PURPLE -> PURPLE_WALL_BANNER;
                case BLUE -> BLUE_WALL_BANNER;
                case BROWN -> BROWN_WALL_BANNER;
                case GREEN -> GREEN_WALL_BANNER;
                case RED -> RED_WALL_BANNER;
                case BLACK -> BLACK_WALL_BANNER;
            };
        } else {
            return switch (c) {
                case WHITE -> WHITE_BANNER;
                case ORANGE -> ORANGE_BANNER;
                case MAGENTA -> MAGENTA_BANNER;
                case LIGHT_BLUE -> LIGHT_BLUE_BANNER;
                case YELLOW -> YELLOW_BANNER;
                case LIME -> LIME_BANNER;
                case PINK -> PINK_BANNER;
                case GRAY -> GRAY_BANNER;
                case LIGHT_GRAY -> LIGHT_GRAY_BANNER;
                case CYAN -> CYAN_BANNER;
                case PURPLE -> PURPLE_BANNER;
                case BLUE -> BLUE_BANNER;
                case BROWN -> BROWN_BANNER;
                case GREEN -> GREEN_BANNER;
                case RED -> RED_BANNER;
                case BLACK -> BLACK_BANNER;
            };
        }
    }
}