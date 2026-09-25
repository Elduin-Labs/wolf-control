package com.elduin.wolf_control.platform.fabric;

//? fabric {

import com.elduin.wolf_control.WolfControl;
import com.elduin.wolf_control.server.StarterChest;
import com.elduin.wolf_control.server.WolfGlow;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class FabricEventSubscriber {

	public static void registerEvents() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
				StarterChest.onJoin(handler.getPlayer()));

		ServerTickEvents.END_SERVER_TICK.register(WolfGlow::tick);

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
			entries.accept(WolfControl.WOLF_DETECTOR);
			entries.accept(WolfControl.WOLF_CALLER);
		});
	}
}
//?}
