package com.elduin.wolf_control.item;

import com.elduin.wolf_control.WolfControl;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Right-click: flip into creative mode and hand out a set of wings.
 * Sneak + right-click: drop back to survival. The detector keeps working either
 * way — the orange boxes are drawn from whether you are holding this, not from
 * which game mode you are in.
 */
public class WolfDetectorItem extends Item {

	public WolfDetectorItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);

		// Game mode lives on the server. The client just plays along.
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResultHolder.success(held);
		}

		if (player.isShiftKeyDown()) {
			serverPlayer.setGameMode(GameType.SURVIVAL);
			say(serverPlayer, "Survival again. The detector still works.");
		} else {
			serverPlayer.setGameMode(GameType.CREATIVE);
			giveWings(serverPlayer);
			say(serverPlayer, "Creative on. " + countWolves(serverPlayer) + " wolf(s) in range.");
		}

		level.playSound(null, player.blockPosition(), SoundEvents.WOLF_HOWL,
				SoundSource.PLAYERS, 0.7F, 1.0F);

		return InteractionResultHolder.success(held);
	}

	/** The spare wings. Only fills an empty chest slot, so armour is never eaten. */
	private static void giveWings(ServerPlayer player) {
		if (player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
			player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
		}
	}

	private static int countWolves(ServerPlayer player) {
		return player.level.getEntitiesOfClass(Wolf.class,
				player.getBoundingBox().inflate(WolfControl.DETECT_RANGE)).size();
	}

	private static void say(ServerPlayer player, String message) {
		player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.GOLD), true);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
		lines.add(Component.literal("Right-click: creative mode + wings").withStyle(ChatFormatting.GRAY));
		lines.add(Component.literal("Sneak + right-click: back to survival").withStyle(ChatFormatting.GRAY));
		lines.add(Component.literal("Hold it to box every wolf in orange").withStyle(ChatFormatting.GOLD));
	}
}
