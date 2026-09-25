package com.elduin.wolf_control.server;

import com.elduin.wolf_control.WolfControl;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.scores.PlayerTeam;

/**
 * The server half of the detector. While somebody holds one, nearby wolves get
 * the vanilla Glowing effect on an orange team, so their outline shines orange
 * straight through walls — and it works for anyone on the server, even players
 * who have not installed the mod on their own game.
 */
public final class WolfGlow {

	private static final String TEAM_NAME = "wolf_control_orange";
	private static final int EVERY_N_TICKS = 10;
	private static final int GLOW_TICKS = 40;

	private WolfGlow() {
	}

	public static void tick(MinecraftServer server) {
		if (server.getTickCount() % EVERY_N_TICKS != 0) {
			return;
		}

		PlayerTeam team = orangeTeam(server);

		for (ServerLevel level : server.getAllLevels()) {
			for (ServerPlayer player : level.players()) {
				if (!holdingDetector(player)) {
					continue;
				}
				for (Wolf wolf : level.getEntitiesOfClass(Wolf.class,
						player.getBoundingBox().inflate(WolfControl.DETECT_RANGE))) {
					markOrange(server, team, wolf);
				}
			}
		}
	}

	private static void markOrange(MinecraftServer server, PlayerTeam team, Wolf wolf) {
		wolf.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_TICKS, 0, false, false, false));

		PlayerTeam current = (PlayerTeam) wolf.getTeam();
		if (current == null || !TEAM_NAME.equals(current.getName())) {
			server.getScoreboard().addPlayerToTeam(wolf.getScoreboardName(), team);
		}
	}

	private static PlayerTeam orangeTeam(MinecraftServer server) {
		ServerScoreboard scoreboard = server.getScoreboard();
		PlayerTeam team = scoreboard.getPlayerTeam(TEAM_NAME);
		if (team == null) {
			team = scoreboard.addPlayerTeam(TEAM_NAME);
			team.setDisplayName(Component.literal("Wolf Control"));
			// GOLD is vanilla's orange. It is what the glow outline is drawn in.
			team.setColor(ChatFormatting.GOLD);
			team.setSeeFriendlyInvisibles(false);
		}
		return team;
	}

	private static boolean holdingDetector(ServerPlayer player) {
		for (InteractionHand hand : InteractionHand.values()) {
			if (player.getItemInHand(hand).is(WolfControl.WOLF_DETECTOR)) {
				return true;
			}
		}
		return false;
	}
}
