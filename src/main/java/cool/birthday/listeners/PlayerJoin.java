package cool.birthday.listeners;

import cool.birthday.configuration.Birthdays;
import cool.birthday.runnables.MainBirthday;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.util.List;

public class PlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        String month = LocalDateTime.now().getMonth().toString().toLowerCase();
        int day = LocalDateTime.now().getDayOfMonth();

        List<String> birthdayCelebrants = Birthdays.getInstance().getBirthdaysToday(month, day);

        MainBirthday.updateTime();
        BossBar bossBar = MainBirthday.getBossBar(birthdayCelebrants);

        bossBar.addPlayer(player);
    }
}