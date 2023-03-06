package cool.birthday.runnables;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRunnable extends BukkitRunnable {
    private static final HashMap<CommandSender, Integer> confirmTime = new HashMap<>();
    private static final HashMap<CommandSender, String[]> playerArgs = new HashMap<>();

    @Override
    public void run() {
        if (!confirmTime.isEmpty()) addOneSec();

        getMappedSenders();
    }

    private void addOneSec() {
        confirmTime.forEach((key, value) -> confirmTime.put(key, value + 1));
    }

    public static List<CommandSender> getMappedSenders() {
        List<CommandSender> keys = new ArrayList<>();
        List<CommandSender> confirmTimeToRemove = new ArrayList<>();
        List<CommandSender> senderArgsToRemove = new ArrayList<>();

        confirmTime.forEach((key, value) -> {
            if (confirmTime.get(key) > 10) {
                confirmTimeToRemove.add(key);

                senderArgsToRemove.add(key);

                key.sendRichMessage("<red>Command canceled.");
            } else keys.add(key);
        });

        confirmTimeToRemove.forEach(confirmTime::remove);
        senderArgsToRemove.forEach(playerArgs::remove);

        return keys;
    }

    public static void removeMappedSender(CommandSender sender) { confirmTime.remove(sender); }

    public static void addMappingSender(CommandSender sender, String[] args) {
        confirmTime.put(sender, 0);
        playerArgs.put(sender, args);
    }

    public static String[] getPlayerCommandArgs(CommandSender sender) { return playerArgs.get(sender); }
}