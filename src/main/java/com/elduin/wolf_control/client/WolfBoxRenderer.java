package com.elduin.wolf_control.client;

import com.elduin.wolf_control.WolfControl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Draws a giant orange cage around every wolf in range, but only while the
 * player is holding a Wolf Detector. Game mode makes no difference — survival
 * and creative both get the boxes.
 */
public final class WolfBoxRenderer {

	/** Orange. */
	private static final float R = 1.00F;
	private static final float G = 0.45F;
	private static final float B = 0.05F;

	/** Half-width of the box, in blocks. 1.3 makes it about 2.6 blocks across. */
	private static final double HALF_WIDTH = 1.3D;
	private static final double HEIGHT = 2.6D;

	private WolfBoxRenderer() {
	}

	public static void register() {
		WorldRenderEvents.AFTER_ENTITIES.register(WolfBoxRenderer::render);
	}

	private static void render(WorldRenderContext context) {
		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;
		if (player == null || client.level == null || !holdingDetector(player)) {
			return;
		}

		PoseStack poses = context.matrixStack();
		MultiBufferSource consumers = context.consumers();
		if (poses == null || consumers == null) {
			return;
		}

		VertexConsumer lines = consumers.getBuffer(RenderType.lines());
		Vec3 camera = context.camera().getPosition();
		float partialTick = context.tickDelta();

		AABB searchArea = player.getBoundingBox().inflate(WolfControl.DETECT_RANGE);
		for (Wolf wolf : client.level.getEntitiesOfClass(Wolf.class, searchArea)) {
			AABB box = boxAround(wolf, partialTick);

			poses.pushPose();
			poses.translate(-camera.x, -camera.y, -camera.z);
			// Three nested boxes so the lines read as thick bars in VR, where a
			// single one-pixel wireframe almost disappears.
			LevelRenderer.renderLineBox(poses, lines, box, R, G, B, 1.00F);
			LevelRenderer.renderLineBox(poses, lines, box.inflate(0.04D), R, G, B, 0.75F);
			LevelRenderer.renderLineBox(poses, lines, box.inflate(0.08D), R, G, B, 0.50F);
			poses.popPose();
		}
	}

	private static AABB boxAround(Wolf wolf, float partialTick) {
		Vec3 feet = wolf.getPosition(partialTick);
		return new AABB(
				feet.x - HALF_WIDTH, feet.y - 0.05D, feet.z - HALF_WIDTH,
				feet.x + HALF_WIDTH, feet.y + HEIGHT, feet.z + HALF_WIDTH);
	}

	private static boolean holdingDetector(LocalPlayer player) {
		for (InteractionHand hand : InteractionHand.values()) {
			if (player.getItemInHand(hand).is(WolfControl.WOLF_DETECTOR)) {
				return true;
			}
		}
		return false;
	}
}
