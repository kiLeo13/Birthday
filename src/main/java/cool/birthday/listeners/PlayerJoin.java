package cool.birthday.listeners;

import cool.birthday.runnables.MainBossBar;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        MainBossBar.updateTime();
        BossBar bossBar = MainBossBar.getBossBar();

        bossBar.addPlayer(player);
    }
}