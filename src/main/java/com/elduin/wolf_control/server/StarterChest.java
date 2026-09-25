package com.elduin.wolf_control.server;

import com.elduin.wolf_control.WolfControl;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/**
 * When you arrive with no detector on you, a chest full of them appears right in
 * front of where you are standing. If there is nowhere to put a chest (mid-air,
 * inside a wall) the items go straight into your inventory instead, so you are
 * never stranded without them.
 */
public final class StarterChest {

	private static final int DETECTORS = 16;
	private static final int CALLERS = 16;

	private StarterChest() {
	}

	public static void onJoin(ServerPlayer player) {
		if (alreadyHasOne(player)) {
			return;
		}

		boolean placed = placeChestInFront(player);
		if (!placed) {
			player.getInventory().add(new ItemStack(WolfControl.WOLF_DETECTOR, 4));
			player.getInventory().add(new ItemStack(WolfControl.WOLF_CALLER, 4));
		}

		tell(player, placed
				? "Wolf Control: there is a chest of detectors in front of you."
				: "Wolf Control: detectors are in your bag.");
		tell(player, "Right-click a detector for creative and wings. Sneak + right-click to go back.");
	}

	private static boolean alreadyHasOne(ServerPlayer player) {
		return player.getInventory().contains(new ItemStack(WolfControl.WOLF_DETECTOR));
	}

	private static boolean placeChestInFront(ServerPlayer player) {
		ServerLevel level = player.getLevel();
		Direction facing = player.getDirection();
		BlockPos ahead = player.blockPosition().relative(facing, 2);

		// Try the block in front, then one down, then two down, so a chest still
		// lands somewhere sensible on sloping ground.
		for (int drop = 0; drop <= 2; drop++) {
			BlockPos spot = ahead.below(drop);
			boolean roomHere = level.getBlockState(spot).isAir();
			boolean floorBelow = !level.getBlockState(spot.below()).isAir();
			if (roomHere && floorBelow) {
				return fillChest(level, spot, facing.getOpposite());
			}
		}
		return false;
	}

	private static boolean fillChest(ServerLevel level, BlockPos pos, Direction facing) {
		level.setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing));

		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!(blockEntity instanceof ChestBlockEntity chest)) {
			return false;
		}

		for (int slot = 0; slot < 9; slot++) {
			chest.setItem(slot, new ItemStack(WolfControl.WOLF_DETECTOR, DETECTORS));
		}
		for (int slot = 9; slot < 18; slot++) {
			chest.setItem(slot, new ItemStack(WolfControl.WOLF_CALLER, CALLERS));
		}
		chest.setChanged();
		return true;
	}

	private static void tell(ServerPlayer player, String message) {
		player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.GOLD));
	}
}
