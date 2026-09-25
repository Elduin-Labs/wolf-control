package com.elduin.wolf_control;

import com.elduin.wolf_control.item.WolfCallerItem;
import com.elduin.wolf_control.item.WolfDetectorItem;
import com.elduin.wolf_control.platform.Platform;
import com.elduin.wolf_control.platform.fabric.FabricPlatform;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WolfControl {

	public static final String MOD_ID = /*$ mod_id*/ "wolf_control";
	public static final String MOD_VERSION = /*$ mod_version*/ "1.0.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Wolf Control";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Every wolf this mod spawns carries this tag, so we can find our own again. */
	public static final String WOLF_TAG = "wolf_control_wolf";

	/** How far the detector sees, in blocks. */
	public static final double DETECT_RANGE = 64.0D;

	public static final Item WOLF_DETECTOR = new WolfDetectorItem(new Item.Properties());
	public static final Item WOLF_CALLER = new WolfCallerItem(new Item.Properties());

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
		LOGGER.info("Initializing {} on {}", MOD_ID, xplat().loader());
		Registry.register(BuiltInRegistries.ITEM, id("wolf_detector"), WOLF_DETECTOR);
		Registry.register(BuiltInRegistries.ITEM, id("wolf_caller"), WOLF_CALLER);
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing {} Client on {}", MOD_ID, xplat().loader());
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		return new FabricPlatform();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
