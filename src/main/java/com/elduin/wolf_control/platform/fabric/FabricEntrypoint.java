package com.elduin.wolf_control.platform.fabric;

//? fabric {

import com.elduin.wolf_control.WolfControl;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		WolfControl.onInitialize();
		FabricEventSubscriber.registerEvents();
	}
}
//?}
