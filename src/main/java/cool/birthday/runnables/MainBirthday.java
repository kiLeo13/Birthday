package cool.birthday.runnables;

import cool.birthday.Birthday;
import cool.birthday.configuration.Birthdays;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
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
    private static int second;
    private static int minuteOfDay;

    public MainBirthday(Plugin plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        updateTime();
        runTest(month.toLowerCase(), day);
    }

    private void runTest(String month, int day) {
        int hourAnnouncement = plugin.getConfig().getInt("main-announcement.hour");
        int minuteAnnouncement = plugin.getConfig().getInt("main-announcement.minute");
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

            if (hour == hourAnnouncement && minute == minuteAnnouncement && second == 0)
                additionalAnnouncements(player);
        });
    }

    public static void updateTime() {
        month = LocalDateTime.now().getMonth().toString().toLowerCase();
        day = LocalDateTime.now().getDayOfMonth();
        hour = LocalDateTime.now().getHour();
        minute = LocalDateTime.now().getMinute();
        second = LocalDateTime.now().getSecond();

        minuteOfDay = (hour * 60) + minute;
    }

    public void additionalAnnouncements(Player player) {
        /* Playsound if enabled */
        String sound = plugin.getConfig().getString("sound-effect");
        Sound soundEffect = getProperSound(sound);

        if (getProperSound(sound) != null) player.playSound(player.getLocation(), soundEffect, SoundCategory.MASTER, 1.0f, 1.0f);

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

        long fadeIn = plugin.getConfig().getInt("times.fadeIn");
        long stay = plugin.getConfig().getInt("times.stay");
        long fadeOut = plugin.getConfig().getInt("times.fadeOut");

        if (title == null || title.equalsIgnoreCase("")) return null;
        if (subtitle == null || subtitle.equalsIgnoreCase("")) subtitle = "";

        String finalSubtitle = subtitle;

        return Title.title(MiniMessage.miniMessage().deserialize(title), MiniMessage.miniMessage().deserialize(finalSubtitle), Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut)));
    }

    public static void updateBossBar(List<String> birthdays) {
        StringBuilder builder = new StringBuilder();
        BarColor color = BarColor.valueOf(Birthday.getPlugin().getConfig().getString("barcolor-multiple-celebrants"));
        double progress = (1440 - minuteOfDay) / 1440.0;

        if (birthdays.isEmpty()) {
            bossBar.setTitle(null);
            bossBar.removeAll();
            return;
        }

        ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");
        String bossCelebrantName;

        if (birthdays.size() > 1) {

            for (String name : birthdays){
                builder.append(ChatColor.YELLOW).append(getPlayerBirthdayName(name)).append(ChatColor.DARK_GRAY).append(", ");
            }

            bossCelebrantName = builder.toString().stripTrailing().substring(0, builder.length()-2);

        } else {
            if (section == null) bossCelebrantName = "Unknown";
            else {
                color = BarColor.valueOf(section.getString(birthdays.get(0) + ".barcolor"));
                bossCelebrantName = section.getString(birthdays.get(0) + ".name");
            }
        }

        bossBar.setProgress(progress);

        if (birthdays.size() == 1) {
            bossBar.setTitle(ChatColor.LIGHT_PURPLE + "⭐ " + ChatColor.GOLD + "Celebrant: " + ChatColor.YELLOW + bossCelebrantName + ChatColor.LIGHT_PURPLE + " ⭐");
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

    private Sound getProperSound(String soundFile) {
        Sound sound;

        if (soundFile == null || soundFile.equalsIgnoreCase("")) return null;

        if (soundFile.equalsIgnoreCase("default")) return Sound.ENTITY_ENDER_DRAGON_GROWL;

        try {
            sound = Sound.valueOf(soundFile);
            return sound;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}