package cool.birthday.runnables;

import cool.birthday.configuration.Birthdays;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class MainBirthday extends BukkitRunnable {
    private final Plugin plugin;
    private static String month;
    private static int day;
    private static int hour;
    private static int minute;

    public MainBirthday(Plugin plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        updateTime();
        runTest(month.toLowerCase(), day);

        setBossBarProgress(hour);
    }

    private void runTest(String month, int day) {
        List<String> birthdays = Birthdays.getInstance().getBirthdaysToday(month, day);
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        players.forEach(player -> {
            if (birthdays.isEmpty()) {
                getBossBar(birthdays).removePlayer(player);
                return;
            }

            /* Call other functions if time is exactly 00:00 (12 AM) */
            getBossBar(birthdays).removePlayer(player);
            getBossBar(birthdays).addPlayer(player);
            if (hour == 0 && minute == 0) additionalAnnouncements(player);
        });
    }

    private void updateTime() {
        month = LocalDateTime.now().getMonth().toString().toLowerCase();
        day = LocalDateTime.now().getDayOfMonth();
        hour = LocalDateTime.now().getHour();
        minute = LocalDateTime.now().getMinute();
    }

    public void additionalAnnouncements(Player player) {
        /* Playsound if enabled */
        String sound = plugin.getConfig().getString("sound-effect");
        Sound soundEffect;

        if (sound == null) sound = "";

        if (sound.equalsIgnoreCase("default")) soundEffect = Sound.ENTITY_ENDER_DRAGON_GROWL;
        else soundEffect = Sound.valueOf(sound);

        if (!sound.equalsIgnoreCase("")) player.playSound(player.getLocation(), soundEffect, SoundCategory.MASTER, 1.0f, 1.0f);

        /* Show Title and Subtitle if enabled */
        Title title = callTitle();
        if (title != null) player.showTitle(title);

        /* Send announcement message if enabled */
        String announcementMessage = plugin.getConfig().getString("announcement-message");

        if (announcementMessage == null) announcementMessage = "";

        if (!announcementMessage.equalsIgnoreCase("")) player.sendMessage(MiniMessage.miniMessage().deserialize(announcementMessage));
    }

    private Title callTitle() {
        String title = plugin.getConfig().getString("title-shown");
        String subtitle = plugin.getConfig().getString("subtitle-shown");

        long fadeIn = plugin.getConfig().getIntegerList("times").get(0);
        long stay = plugin.getConfig().getIntegerList("times").get(1);
        long fadeOut = plugin.getConfig().getIntegerList("times").get(2);

        if (title == null || title.equalsIgnoreCase("")) return null;
        if (subtitle == null || subtitle.equalsIgnoreCase("")) subtitle = "";

        String finalSubtitle = subtitle;

        return Title.title(MiniMessage.miniMessage().deserialize(title), MiniMessage.miniMessage().deserialize(finalSubtitle), Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut)));
    }

    private BossBar getBossBar(List<String> birthdays) {
        BossBar bossBar;

        StringBuilder builder = new StringBuilder();
        BarColor color = BarColor.PURPLE;

        if (birthdays.isEmpty()) {
            bossBar = Bukkit.createBossBar("", BarColor.PURPLE, BarStyle.SOLID);
            return bossBar;
        }

        String bossCelebrant;

        if (birthdays.size() > 1) {
            for (String name : birthdays){
                builder.append(name).append(ChatColor.DARK_GRAY).append(", ");
            }

            bossCelebrant = builder.toString().stripTrailing();
            bossCelebrant = StringUtils.chop(bossCelebrant);
        } else {
            bossCelebrant = birthdays.get(0);
            ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");

            if (section != null) color = BarColor.valueOf(section.getString(birthdays.get(0) + ".barcolor"));
        }

        if (birthdays.size() == 1) bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Birthday Celebrant: ", color, BarStyle.SOLID);
        else bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Birthday Celebrants: " + ChatColor.YELLOW + bossCelebrant, color, BarStyle.SOLID);

        return bossBar;
    }

    private void setBossBarProgress(int hour) {
        List<String> birthdays = Birthdays.getInstance().getBirthdaysToday(month, day);
        double progress = hour / 24.0;

        getBossBar(birthdays).setProgress(progress);
    }
}