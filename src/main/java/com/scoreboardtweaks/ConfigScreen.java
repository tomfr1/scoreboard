package com.scoreboardtweaks;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
	private final Screen parent;
	private ButtonWidget backgroundButton;
	private ButtonWidget numbersButton;

	public ConfigScreen(Screen parent) {
		super(Text.literal("Scoreboard-Einstellungen"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		ModConfig config = ModConfig.get();
		int w = 200;
		int x = this.width / 2 - w / 2;
		int y = this.height / 2 - 55;

		ScaleSlider slider = new ScaleSlider(x, y, w, 20);
		slider.refreshMessage();
		this.addDrawableChild(slider);

		backgroundButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
			config.hideBackground = !config.hideBackground;
			updateButtonTexts();
		}).dimensions(x, y + 24, w, 20).build());

		numbersButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
			config.hideNumbers = !config.hideNumbers;
			updateButtonTexts();
		}).dimensions(x, y + 48, w, 20).build());

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Zurücksetzen"), button -> {
			config.scale = 1.0f;
			config.hideBackground = false;
			config.hideNumbers = false;
			this.clearAndInit();
		}).dimensions(x, y + 78, w / 2 - 2, 20).build());

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Fertig"), button -> this.close())
			.dimensions(x + w / 2 + 2, y + 78, w / 2 - 2, 20).build());

		updateButtonTexts();
	}

	private void updateButtonTexts() {
		ModConfig config = ModConfig.get();
		backgroundButton.setMessage(Text.literal("Hintergrund ausblenden: " + (config.hideBackground ? "AN" : "AUS")));
		numbersButton.setMessage(Text.literal("Zahlen ausblenden: " + (config.hideNumbers ? "AN" : "AUS")));
	}

	/** Kein Weichzeichner/Abdunkeln, damit man das Scoreboard live im Hintergrund sieht. */
	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 75, 0xFFFFFFFF);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public void close() {
		this.client.setScreen(parent);
	}

	@Override
	public void removed() {
		ModConfig.save();
	}

	/** Regler von 25 % bis 300 % in 5-%-Schritten. */
	private static class ScaleSlider extends SliderWidget {
		ScaleSlider(int x, int y, int width, int height) {
			super(x, y, width, height, Text.empty(),
				(ModConfig.get().scale - ModConfig.MIN_SCALE) / (ModConfig.MAX_SCALE - ModConfig.MIN_SCALE));
		}

		void refreshMessage() {
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Text.literal("Größe: " + Math.round(ModConfig.get().scale * 100) + " %"));
		}

		@Override
		protected void applyValue() {
			float raw = ModConfig.MIN_SCALE + (float) this.value * (ModConfig.MAX_SCALE - ModConfig.MIN_SCALE);
			ModConfig.get().scale = Math.round(raw * 20f) / 20f;
		}
	}
}
