package com.scoreboardtweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.scoreboardtweaks.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.BlankNumberFormat;
import net.minecraft.scoreboard.number.NumberFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	private static final String SIDEBAR =
		"renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V";

	/** Die beiden Hintergrund-Rechtecke (Titelzeile + Zeilen) nicht zeichnen. */
	@WrapOperation(
		method = SIDEBAR,
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V")
	)
	private void scoreboardtweaks$hideBackground(DrawContext context, int x1, int y1, int x2, int y2, int color,
												 Operation<Void> original) {
		if (!ModConfig.get().hideBackground) {
			original.call(context, x1, y1, x2, y2, color);
		}
	}

	/** Zahlenformat "leer" benutzen -> keine (roten) Punktzahlen, Scoreboard wird automatisch schmaler. */
	@WrapOperation(
		method = SIDEBAR,
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/scoreboard/ScoreboardObjective;getNumberFormatOr(Lnet/minecraft/scoreboard/number/NumberFormat;)Lnet/minecraft/scoreboard/number/NumberFormat;")
	)
	private NumberFormat scoreboardtweaks$hideNumbers(ScoreboardObjective objective, NumberFormat fallback,
													 Operation<NumberFormat> original) {
		if (ModConfig.get().hideNumbers) {
			return BlankNumberFormat.INSTANCE;
		}
		return original.call(objective, fallback);
	}
}
