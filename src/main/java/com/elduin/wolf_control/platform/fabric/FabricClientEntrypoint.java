package com.elduin.wolf_control.platform.fabric;

//? fabric {

import com.elduin.wolf_control.WolfControl;
import com.elduin.wolf_control.client.WolfBoxRenderer;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		WolfControl.onInitializeClient();
		WolfBoxRenderer.register();
	}

}
//?}
