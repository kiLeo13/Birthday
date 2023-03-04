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
    private static BossBar bossBar = null;
    private final Plugin plugin;
    private static String month;
    private static int day;
    private static int hour;
    private static int minute;
    private static int minuteOfDay;

    public MainBirthday(Plugin plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        updateTime();
        runTest(month.toLowerCase(), day);
    }

    private void runTest(String month, int day) {
        List<String> birthdays = Birthdays.getInstance().getBirthdaysToday(month, day);
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        players.forEach(player -> {
            if (birthdays.isEmpty()) {
                updateBossBar(birthdays);
                bossBar.removeAll();
                return;
            }

            /* Call other functions if time is exactly 00:00 (12 AM) */
            updateBossBar(birthdays);
            bossBar.addPlayer(player);

            if (hour == 0 && minute == 0) additionalAnnouncements(player);
        });
    }

    public static void updateTime() {
        month = LocalDateTime.now().getMonth().toString().toLowerCase();
        day = LocalDateTime.now().getDayOfMonth();
        hour = LocalDateTime.now().getHour();
        minute = LocalDateTime.now().getMinute();

        minuteOfDay = (hour * 60) + minute;
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

    public static void updateBossBar(List<String> birthdays) {
        StringBuilder builder = new StringBuilder();
        BarColor color = BarColor.PURPLE;
        double progress = minuteOfDay / 1440.0;

        if (birthdays.isEmpty()) {
            bossBar.setTitle(null);
            return;
        }

        ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");
        String bossCelebrantName;

        if (birthdays.size() > 1) {

            for (String name : birthdays){
                builder.append(getPlayerBirthdayName(name)).append(ChatColor.DARK_GRAY).append(", ").append(ChatColor.YELLOW);
            }

            bossCelebrantName = builder.toString().stripTrailing();
            bossCelebrantName = StringUtils.chop(bossCelebrantName);

        } else {
            if (section == null) bossCelebrantName = "Unknown";
            else {
                color = BarColor.valueOf(section.getString(birthdays.get(0) + ".barcolor"));
                bossCelebrantName = section.getString(birthdays.get(0) + ".name");
            }
        }

        bossBar.setProgress(progress);

        if (birthdays.size() == 1) {
            bossBar.setTitle(ChatColor.GOLD + "Celebrant: " + ChatColor.YELLOW + bossCelebrantName);
            bossBar.setColor(color);
        } else {
            bossBar.setTitle(ChatColor.GOLD + "Celebrants: " + ChatColor.YELLOW + bossCelebrantName);
            bossBar.setColor(color);
        }
    }

    public static BossBar getBossBar() { return bossBar; }

    public static void createBossBar() {
        if (bossBar == null) bossBar = Bukkit.createBossBar("", BarColor.PURPLE, BarStyle.SOLID);
    }

    private static String getPlayerBirthdayName(String key) {
        ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");

        if (section == null) return "Unknown";

        return section.getString(key + ".name");
    }
}