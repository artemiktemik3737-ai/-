
package dev.aurora.visuals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class Config {
    public static class Data {
        public Map<String, Boolean> modules = new HashMap<>();
        public int weatherMode = 1;
        public int strength = 100;
        public int wind = 50;
        public int density = 50;
        public int hue = 170;
        public int hour = 12;
    }

    public static Data d = new Data();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("auroravisuals.json");
    }

    public static void load() {
        try {
            Path f = file();
            if (Files.exists(f)) {
                Data x = GSON.fromJson(Files.readString(f), Data.class);
                if (x != null) {
                    d = x;
                    if (d.modules == null) d.modules = new HashMap<>();
                }
            }
        } catch (Exception e) {
            AuroraClient.LOG.warn("Не удалось прочитать конфиг, беру настройки по умолчанию", e);
        }
        for (Feature f : Features.ALL) {
            f.enabled = d.modules.getOrDefault(f.id, false);
        }
    }

    public static void save() {
        for (Feature f : Features.ALL) d.modules.put(f.id, f.enabled);
        try {
            Files.writeString(file(), GSON.toJson(d));
        } catch (IOException e) {
            AuroraClient.LOG.warn("Не удалось сохранить конфиг", e);
        }
    }

    /** Цвет эффектов (RGB) из ползунка оттенка. */
    public static int effectColor() {
        return MathHelper.hsvToRgb(d.hue / 360f, 0.65f, 1.0f);
    }

    private Config() {}
}
