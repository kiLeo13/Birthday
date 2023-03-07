package cool.birthday.runnables;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRunnable extends BukkitRunnable {
    private static final HashMap<CommandSender, Integer> confirmCreatingTime = new HashMap<>();
    private static final HashMap<CommandSender, Integer> confirmDeletingTime = new HashMap<>();
    private static final HashMap<CommandSender, String[]> playerCreatingArgs = new HashMap<>();
    private static final HashMap<CommandSender, String> playerDeletingArg = new HashMap<>();

    @Override
    public void run() {
        if (!confirmCreatingTime.isEmpty()) addCreatingOneSec();
        if (!confirmDeletingTime.isEmpty()) addDeletingOneSec();

        getMappedCreatingSenders();
    }

    private void addCreatingOneSec() {
        confirmCreatingTime.forEach((key, value) -> confirmCreatingTime.put(key, value + 1));
    }

    private void addDeletingOneSec() {
        confirmDeletingTime.forEach((key, value) -> confirmDeletingTime.put(key, value + 1));
    }

    public static List<CommandSender> getMappedCreatingSenders() {
        List<CommandSender> keys = new ArrayList<>();
        List<CommandSender> confirmTimeToRemove = new ArrayList<>();
        List<CommandSender> senderArgsToRemove = new ArrayList<>();

        confirmCreatingTime.forEach((key, value) -> {
            if (confirmCreatingTime.get(key) > 10) {
                confirmTimeToRemove.add(key);

                senderArgsToRemove.add(key);

                key.sendRichMessage("<red>Command canceled.");
            } else keys.add(key);
        });

        confirmTimeToRemove.forEach(confirmCreatingTime::remove);
        senderArgsToRemove.forEach(playerCreatingArgs::remove);

        return keys;
    }

    public static List<CommandSender> getMappedDeletingSenders() {
        List<CommandSender> keys = new ArrayList<>();
        List<CommandSender> confirmTimeToRemove = new ArrayList<>();
        List<CommandSender> senderArgToRemove = new ArrayList<>();

        confirmDeletingTime.forEach((key, value) -> {
            if (confirmDeletingTime.get(key) > 10) {
                confirmTimeToRemove.add(key);

                senderArgToRemove.add(key);

                key.sendRichMessage("<red>Command canceled.");
            } else keys.add(key);
        });

        confirmTimeToRemove.forEach(confirmDeletingTime::remove);
        senderArgToRemove.forEach(playerDeletingArg::remove);

        return keys;
    }

    public static void removeCreatingMappedSender(CommandSender sender) {
        confirmCreatingTime.remove(sender);
        playerDeletingArg.remove(sender);
    }

    public static void removeDeletingMappedSender(CommandSender sender) {
        confirmDeletingTime.remove(sender);
        playerDeletingArg.remove(sender);
    }

    public static void addCreatingMappingSender(CommandSender sender, String[] args) {
        confirmCreatingTime.put(sender, 0);
        playerCreatingArgs.put(sender, args);
    }

    public static void addDeletingMappingSender(CommandSender sender, String arg) {
        confirmDeletingTime.put(sender, 0);
        playerDeletingArg.put(sender, arg);
    }

    public static String[] getPlayerCreatingCommandArgs(CommandSender sender) { return playerCreatingArgs.get(sender); }

    public static String getPlayerDeletingCommandArg(CommandSender sender) { return playerDeletingArg.get(sender); }
}