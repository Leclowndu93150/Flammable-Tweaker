package com.leclowndu93150.flamabletweaker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlammabilityConfig {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File("config");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "flamabletweaker.json");

    public static List<String> flammableKeywords = Collections.emptyList();
    public static List<String> excludedKeywords = Collections.emptyList();

    public static void loadConfig() {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    flammableKeywords = data.flammableKeywords.stream().map(String::toLowerCase).toList();
                    excludedKeywords = data.excludedKeywords.stream().map(String::toLowerCase).toList();
                    LOGGER.info("FlammableTweaker config loaded.");
                    LOGGER.info("Flammable keywords: " + flammableKeywords);
                    LOGGER.info("Excluded keywords: " + excludedKeywords);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to read FlammableTweaker config file.", e);
            }
        } else {
            createDefaultConfig();
        }
    }

    private static void createDefaultConfig() {
        ConfigData defaultConfig = new ConfigData();
        flammableKeywords = defaultConfig.flammableKeywords.stream().map(String::toLowerCase).toList();
        excludedKeywords = defaultConfig.excludedKeywords.stream().map(String::toLowerCase).toList();

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(defaultConfig, writer);
            LOGGER.info("Created default FlammableTweaker config file.");
        } catch (IOException e) {
            LOGGER.error("Failed to create default FlammableTweaker config file.", e);
        }
    }

    private static class ConfigData {
        @SerializedName("_description")
        String description = "Configuration for Flammable Tweaker. Add keywords to make blocks flammable or to exclude them from becoming flammable.";

        @SerializedName("flammable_keywords")
        List<String> flammableKeywords = Arrays.asList(
                "paper", "oak", "wood", "log", "plank", "leaves", "wool", "cloth", "vine", "plant", "flower", "grass", "hay", "straw"
        );

        @SerializedName("excluded_keywords")
        List<String> excludedKeywords = Arrays.asList(
                "stone", "obsidian", "nether", "brick", "andesite", "diorite", "granite", "petrified", "iron", "gold", "diamond", "emerald", "netherite", "copper"
        );
    }
}
