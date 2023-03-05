package cool.birthday.listeners;

import cool.birthday.runnables.ChatRunnable;
import cool.birthday.runnables.MainBirthday;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandProcess implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.equalsIgnoreCase("/reload confirm")) MainBirthday.getBossBar().removeAll();

        if (message.startsWith("/birthdayadd") && ChatRunnable.getMappedPlayers().contains(player)) {
            player.sendRichMessage("<red>You cannot run this command right now.");
            event.setCancelled(true);
        }
    }
}