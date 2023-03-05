package cool.birthday.runnables;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRunnable extends BukkitRunnable {
    private static final HashMap<Player, Integer> confirmTime = new HashMap<>();
    private static final HashMap<Player, String[]> playerArgs = new HashMap<>();

    @Override
    public void run() {
        if (!confirmTime.isEmpty()) addOneSec();

        getMappedPlayers();
    }

    private void addOneSec() {
        confirmTime.forEach((key, value) -> confirmTime.put(key, value + 1));
    }

    public static List<Player> getMappedPlayers() {
        List<Player> keys = new ArrayList<>();
        List<Player> confirmTimeToRemove = new ArrayList<>();
        List<Player> playerArgsToRemove = new ArrayList<>();

        confirmTime.forEach((key, value) -> {
            if (confirmTime.get(key) > 10) {
                confirmTimeToRemove.add(key);

                playerArgsToRemove.add(key);

                key.sendRichMessage("<red>Command canceled.");
            } else keys.add(key);
        });

        confirmTimeToRemove.forEach(confirmTime::remove);
        playerArgsToRemove.forEach(playerArgs::remove);

        return keys;
    }

    public static void removeMappedPlayer(Player player) { confirmTime.remove(player); }

    public static void addMappingPlayer(Player player, String[] args) {
        confirmTime.put(player, 0);
        playerArgs.put(player, args);
    }

    public static String[] getPlayerCommandArgs(Player player) { return playerArgs.get(player); }
}