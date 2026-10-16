package it.lia.ninecrowns;

import com.google.gson.*;
import java.nio.file.*;
import java.util.*;
import net.minecraft.world.item.Item;

public final class WorldData {
    public final Roster roster = new Roster();

    private final Path path;

    // Active means the unique legendary currently exists somewhere in the world.
    private boolean swordActive;
    private boolean spearActive;
    private boolean crownActive;

    private long spearLightningNextUse;

    public WorldData(Path path) {
        this.path = path;
        if (!Files.exists(path)) return;

        try {
            var root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();

            // Migration from the 9-player 1.6.11 world: keep the existing roster and
            // leave one free position for the tenth participant.
            if (root.has("players") && root.get("players").isJsonObject()) {
                var players = root.getAsJsonObject("players");
                for (var e : players.entrySet()) {
                    if (roster.players().size() >= Roster.SIZE) break;
                    roster.register(UUID.fromString(e.getKey()), e.getValue().getAsString());
                }
            }

            if (root.has("spearLightningNextUse")) {
                spearLightningNextUse = Math.max(0L, root.get("spearLightningNextUse").getAsLong());
            }

            JsonObject state = null;
            if (root.has("active") && root.get("active").isJsonObject()) {
                state = root.getAsJsonObject("active");
            } else if (root.has("crafted") && root.get("crafted").isJsonObject()) {
                state = root.getAsJsonObject("crafted");
            }

            if (state != null) {
                swordActive = state.has("sword") && state.get("sword").getAsBoolean();
                spearActive = state.has("spear") && state.get("spear").getAsBoolean();
                crownActive = state.has("crown") && state.get("crown").getAsBoolean();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Nine Crowns world data error", e);
        }
    }

    public synchronized boolean isActive(Item item) {
        if (item == ModItems.SWORD) return swordActive;
        if (item == ModItems.SPEAR) return spearActive;
        if (item == ModItems.CROWN) return crownActive;
        return false;
    }

    public synchronized boolean tryActivate(Item item) {
        if (isActive(item)) return false;

        if (item == ModItems.SWORD) swordActive = true;
        else if (item == ModItems.SPEAR) spearActive = true;
        else if (item == ModItems.CROWN) crownActive = true;
        else return false;

        save();
        return true;
    }

    public synchronized boolean release(Item item) {
        boolean changed = false;

        if (item == ModItems.SWORD && swordActive) {
            swordActive = false;
            changed = true;
        } else if (item == ModItems.SPEAR && spearActive) {
            spearActive = false;
            spearLightningNextUse = 0L;
            changed = true;
        } else if (item == ModItems.CROWN && crownActive) {
            crownActive = false;
            changed = true;
        }

        if (changed) save();
        return changed;
    }

    public synchronized String craftedStatus() {
        return "Spada=" + (swordActive ? "ESISTE" : "disponibile")
            + ", Lancia=" + (spearActive ? "ESISTE" : "disponibile")
            + ", Corona=" + (crownActive ? "ESISTE" : "disponibile");
    }

    public synchronized long lightningRemaining(long now) {
        return Math.max(0L, spearLightningNextUse - now);
    }

    public synchronized boolean consumeLightning(long now, long cooldownTicks) {
        if (now < spearLightningNextUse) return false;
        spearLightningNextUse = now + Math.max(1L, cooldownTicks);
        save();
        return true;
    }

    public synchronized void save() {
        try {
            var root = new JsonObject();
            var ps = new JsonObject();
            var active = new JsonObject();

            roster.players().forEach((id, name) -> ps.addProperty(id.toString(), name));

            active.addProperty("sword", swordActive);
            active.addProperty("spear", spearActive);
            active.addProperty("crown", crownActive);

            root.addProperty("rosterSize", Roster.SIZE);
            root.add("players", ps);
            root.add("active", active);
            root.addProperty("spearLightningNextUse", spearLightningNextUse);

            Files.createDirectories(path.getParent());
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(root);

            Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
            Files.writeString(tmp, json);

            try {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Nine Crowns save error", e);
        }
    }
}
