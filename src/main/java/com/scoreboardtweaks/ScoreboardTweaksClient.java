package com.scoreboardtweaks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ScoreboardTweaksClient implements ClientModInitializer {
	public static final String MOD_ID = "scoreboardtweaks";

	private static KeyBinding openSettingsKey;

	@Override
	public void onInitializeClient() {
		ModConfig.load();

		// Hotkey für das Einstellungsmenü (standardmäßig K, in den Steuerungs-Einstellungen änderbar)
		KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));
		openSettingsKey = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.scoreboardtweaks.open", GLFW.GLFW_KEY_K, category)
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openSettingsKey.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new ConfigScreen(null));
				}
			}
		});

		// Scoreboard-Größe: das Vanille-Scoreboard wird mit einer Skalierung umhüllt.
		// Skaliert wird um den rechten Bildschirmrand / die vertikale Mitte,
		// dort sitzt das Scoreboard normalerweise.
		HudElementRegistry.replaceElement(VanillaHudElements.SCOREBOARD, original -> (context, tickCounter) -> {
			float scale = ModConfig.get().scale;
			if (scale == 1.0f) {
				original.render(context, tickCounter);
				return;
			}
			float anchorX = context.getScaledWindowWidth();
			float anchorY = context.getScaledWindowHeight() / 2f;
			var matrices = context.getMatrices();
			matrices.pushMatrix();
			matrices.translate(anchorX, anchorY);
			matrices.scale(scale, scale);
			matrices.translate(-anchorX, -anchorY);
			original.render(context, tickCounter);
			matrices.popMatrix();
		});
	}
}
