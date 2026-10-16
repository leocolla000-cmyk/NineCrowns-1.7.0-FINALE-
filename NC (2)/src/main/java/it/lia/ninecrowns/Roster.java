package it.lia.ninecrowns;

import java.util.*;

public final class Roster {
    public static final int SIZE = 10;
    private final LinkedHashMap<UUID, String> players = new LinkedHashMap<>();

    public boolean register(UUID id, String name) {
        if (players.containsKey(id)) {
            players.put(id, name);
            return true;
        }
        if (players.size() >= SIZE) return false;
        players.put(id, name);
        return true;
    }

    public boolean contains(UUID id) {
        return players.containsKey(id);
    }

    public Map<UUID, String> players() {
        return Collections.unmodifiableMap(players);
    }

    /**
     * All Heads is valid only when the crafter uses the heads of all NINE other
     * registered participants. The crafter's own head and duplicate heads fail.
     */
    public boolean validHeads(UUID crafter, List<UUID> heads) {
        if (players.size() != SIZE || !players.containsKey(crafter) || heads.size() != SIZE - 1) {
            return false;
        }

        Set<UUID> unique = new HashSet<>(heads);
        if (unique.size() != SIZE - 1 || unique.contains(crafter)) {
            return false;
        }

        Set<UUID> expected = new HashSet<>(players.keySet());
        expected.remove(crafter);
        return unique.equals(expected);
    }
}
