package oficina.birthday.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class ChatAsync implements Listener {
    private static final HashMap<Player, Boolean> inConfirmation = new HashMap<>();
    private static Component message;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {

        Player player = event.getPlayer();
        message = event.originalMessage();

        if (!isInConfirmation(player)) return;

        event.setCancelled(true);
        removeOverriting(player);
    }

    public static boolean isInConfirmation(Player player) { return inConfirmation.containsKey(player); }

    public static void setOverriting(Player player) { inConfirmation.put(player, true); }

    public static void removeOverriting(Player player) { inConfirmation.remove(player); }

    public static Component getMessage() { return message; }
}