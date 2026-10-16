package it.lia.ninecrowns;

import com.google.gson.GsonBuilder;
import java.nio.file.*;

public final class Config {
    // OP Sword: 14 total raw damage with Sharpness V.
    public double swordDamage = 14;

    // OP Spear jab: 10 total raw damage WITH Sharpness V = 5 hearts before armor/protection.
    public double spearMeleeDamage = 10;
    public int spearCooldownTicks = 20;

    public double poisonChance = .20;
    public double slownessChance = .10;
    public int effectTicks = 60;

    // Special spear attack: 4 visual lightning bolts, one real 16-damage hit every 30 seconds.
    public int lightningRechargeSeconds = 30;
    public int lightningVisualBolts = 4;
    public double lightningRange = 24;
    public float lightningDamage = 16; // 8 hearts before armor/protection

    public static Config load(Path path) {
        try {
            var gson = new GsonBuilder().setPrettyPrinting().create();
            Config c = new Config();

            if (Files.exists(path)) {
                Config loaded = gson.fromJson(Files.readString(path), Config.class);
                if (loaded != null) c = loaded;
            }

            // Fixed server balance values for this build. Old configs are normalized.
            c.swordDamage = 14;
            c.spearMeleeDamage = 10;
            c.spearCooldownTicks = 20;
            c.poisonChance = .20;
            c.slownessChance = .10;
            c.effectTicks = 60;
            c.lightningRechargeSeconds = 30;
            c.lightningVisualBolts = 4;
            c.lightningRange = 24;
            c.lightningDamage = 16;

            Files.createDirectories(path.getParent());
            Files.writeString(path, gson.toJson(c));
            return c;
        } catch (Exception e) {
            throw new IllegalStateException("Nine Crowns config error", e);
        }
    }
}
