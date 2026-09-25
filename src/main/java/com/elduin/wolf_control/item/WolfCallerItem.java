package com.elduin.wolf_control.item;

import com.elduin.wolf_control.WolfControl;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Right-click a block: place a wolf there with its brain switched off, so it
 * stands exactly where you put it and never wanders.
 * Sneak + right-click a block: every still wolf you own jumps to that spot.
 * That is the "control" part — they only move when you say so.
 */
public class WolfCallerItem extends Item {

	public WolfCallerItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();

		if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.SUCCESS;
		}

		BlockPos target = context.getClickedPos().relative(context.getClickedFace());

		if (player.isShiftKeyDown()) {
			int moved = gather(serverPlayer, target);
			say(serverPlayer, moved == 0 ? "No still wolves to call yet." : "Called " + moved + " wolf(s) here.");
		} else {
			placeStillWolf(serverPlayer, target);
			say(serverPlayer, "Wolf placed. It will not move on its own.");
		}

		level.playSound(null, target, SoundEvents.WOLF_WHINE, SoundSource.NEUTRAL, 0.7F, 1.2F);
		return InteractionResult.SUCCESS;
	}

	private static void placeStillWolf(ServerPlayer player, BlockPos pos) {
		Wolf wolf = EntityType.WOLF.create(player.level);
		if (wolf == null) {
			return;
		}
		wolf.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot() + 180.0F, 0.0F);
		wolf.tame(player);
		wolf.setNoAi(true);          // the whole point: no brain, no wandering
		wolf.setPersistenceRequired();
		wolf.setInvulnerable(false);
		wolf.addTag(WolfControl.WOLF_TAG);
		player.level.addFreshEntity(wolf);
	}

	/** Teleports this player's still wolves onto the clicked spot. */
	private static int gather(ServerPlayer player, BlockPos pos) {
		List<Wolf> mine = player.level.getEntitiesOfClass(Wolf.class,
				player.getBoundingBox().inflate(WolfControl.DETECT_RANGE),
				wolf -> wolf.getTags().contains(WolfControl.WOLF_TAG)
						&& player.getUUID().equals(wolf.getOwnerUUID()));

		int i = 0;
		for (Wolf wolf : mine) {
			// Fan them out a little so they do not all stack on one block.
			double angle = (Math.PI * 2.0D / Math.max(1, mine.size())) * i;
			double radius = mine.size() == 1 ? 0.0D : 1.2D;
			wolf.teleportTo(pos.getX() + 0.5D + Math.cos(angle) * radius,
					pos.getY(),
					pos.getZ() + 0.5D + Math.sin(angle) * radius);
			wolf.setNoAi(true);
			i++;
		}
		return mine.size();
	}

	private static void say(ServerPlayer player, String message) {
		player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.GOLD), true);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
		lines.add(Component.literal("Right-click a block: place a still wolf").withStyle(ChatFormatting.GRAY));
		lines.add(Component.literal("Sneak + right-click: call your wolves there").withStyle(ChatFormatting.GRAY));
	}
}
