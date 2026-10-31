package com.scoreboardtweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Einstellungen, gespeichert in config/scoreboardtweaks.json */
public final class ModConfig {
	public static final float MIN_SCALE = 0.25f;
	public static final float MAX_SCALE = 3.0f;

	private static final Logger LOGGER = LoggerFactory.getLogger("scoreboardtweaks");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static ModConfig instance = new ModConfig();

	/** 1.0 = normale Größe */
	public float scale = 1.0f;
	public boolean hideBackground = false;
	public boolean hideNumbers = false;

	private ModConfig() {
	}

	public static ModConfig get() {
		return instance;
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("scoreboardtweaks.json");
	}

	public static void load() {
		Path file = path();
		if (Files.exists(file)) {
			try (Reader reader = Files.newBufferedReader(file)) {
				ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
				if (loaded != null) {
					instance = loaded;
				}
			} catch (Exception e) {
				LOGGER.warn("Konnte Config nicht lesen, nutze Standardwerte", e);
			}
		}
		instance.scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, instance.scale));
	}

	public static void save() {
		try (Writer writer = Files.newBufferedWriter(path())) {
			GSON.toJson(instance, writer);
		} catch (IOException e) {
			LOGGER.warn("Konnte Config nicht speichern", e);
		}
	}
}
