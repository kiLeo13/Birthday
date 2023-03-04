package cool.birthday.commands;

import cool.birthday.configuration.Birthdays;
import cool.birthday.runnables.ChatRunnable;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AddBirthday implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can run this command.");
            return true;
        }

        if (args.length != 5) {
            player.sendRichMessage("<red>Incorrect usage!\nSee: <green>/birthdayadd <gold><<yellow>key<gold>> <gold><<yellow>displayname<gold>> <gold><<yellow>barcolor<gold>> <gold><<yellow>month<gold>> <gold><<yellow>day<gold>>");
            return true;
        }

        if (!isColorFine(args[2])) {
            player.sendRichMessage("<red>BarColor input invalid.\nPossible values: " + barColorsString());
            return true;
        }

        if (!Birthdays.getInstance().months().contains(args[3])) {
            player.sendRichMessage("<red>Month input invalid. Please write the name of a month, don't use numbers.");
            return true;
        }

        String key = args[0];
        String realName = args[1];
        BarColor barColor = BarColor.valueOf(args[2]);
        String month = args[3];
        byte day;

        try { day = Byte.parseByte(args[4]); }
        catch (NumberFormatException e) {
            sender.sendRichMessage("<red>Day input has to be a number between 1 and 31, see console for errors.");
            e.printStackTrace();
            return true;
        }

        boolean exists = Birthdays.getInstance().birthdayExists(args[0]);

        if (exists) {
            ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");

            if (section == null) {
                player.sendRichMessage("<red>There was a problem attempting to create a new birthday. Section 'birthdays' is null. Check <gold>birthdays.yml<red> file or delete it to a new one be generated.");
                return true;
            }

            String currentCelebrantName = section.getString(key + ".name");
            String currentCelebrantBarColor = getFormattedBarColor(section, key);
            String currentCelebrantMonth = getFormattedMonth(section, key);
            String currentCelebrantDay = getFormattedDay(section, key, player);

            player.sendRichMessage("<yellow>There is already a key with this name, would like to override it?\n\n<gold>Name: <yellow>" + currentCelebrantName + "</yellow>\nBarColor: <yellow>" + currentCelebrantBarColor + "</yellow>\nDate: <yellow>" + currentCelebrantMonth + ", " + currentCelebrantDay + "</yellow>\n\nReply with: <red>YES <yellow> or <red>NO");
            ChatRunnable.addMappingPlayer(player, args);
            return true;
        }

        Birthdays.getInstance().addBirthDay(key, realName, barColor, month, day);
        player.sendRichMessage("<dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray> <green>Successfully scheduled <gold>" + realName + "</gold>'s birthday!</green>");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return allPlayers();
        if (args.length == 3) return barColor();
        if (args.length == 4) return months();
        if (args.length == 5) return days(args[3].toLowerCase());

        return new ArrayList<>();
    }

    private List<String> barColor() {
        List<String> colors = new ArrayList<>();

        for (BarColor i : BarColor.values())
            colors.add(i.toString());

        return colors;
    }

    private List<String> months() { return Birthdays.getInstance().months(); }

    private List<String> days(String month) {
        List<String> returned = new ArrayList<>();

        List<String> months30 = Birthdays.getInstance().months30();
        List<String> months31 = Birthdays.getInstance().months31();

        if (month.equalsIgnoreCase("february"))
            for (byte i = 1; i < 30; i++)
                returned.add(String.valueOf(i));

        if (months30.contains(month.toLowerCase()))
            for (byte i = 1; i <= 30; i++)
                returned.add(String.valueOf(i));

        if (months31.contains(month.toLowerCase()))
            for (byte i = 1; i <= 31; i++)
                returned.add(String.valueOf(i));

        return returned;
    }

    private String barColorsString() {
        List<String> colors = new ArrayList<>(List.of(Arrays.toString(BarColor.values())));
        StringBuilder builder = new StringBuilder();
        builder.append("<gray>[</gray>");

        String finalString;

        for (String i : colors) {
            builder.append("<gold>").append(i).append("<gray>, ");
        }

        finalString = builder.toString().stripTrailing();

        return finalString + "<gray>]</gray>";
    }

    private List<String> allPlayers() {
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        List<String> finalPlayers = new ArrayList<>();

        for (OfflinePlayer i : offlinePlayers)
            finalPlayers.add(i.getName());

        return finalPlayers;
    }

    private boolean isColorFine(String arg) {
        try {
            BarColor.valueOf(arg);
            return true;
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getFormattedMonth(ConfigurationSection section, String key) {
        String rawMonth = section.getString(key + ".month");

        if (rawMonth == null) return "Not found";

        String[] monthSplit = rawMonth.split("");
        StringBuilder builder = new StringBuilder();

        for (byte i = 0; i < monthSplit.length; i++) {
            if (i == 0) builder.append(monthSplit[0].toUpperCase(Locale.ROOT));
            else builder.append(monthSplit[i].toLowerCase());
        }

        return builder.toString();
    }

    private String getFormattedDay(ConfigurationSection section, String key, Player sender) throws NumberFormatException {
        int day = 0;
        String formattedDay;

        try {
            day = section.getInt(key + ".day");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sender.sendRichMessage("<red>Day value format is invalid in <gold>birthdays.yml<red for key <gold>" + key + " <red>. Please, use a valid format or delete the file to generate a new one!");
        }

        if (day < 10) formattedDay = "0" + day;
        else formattedDay = String.valueOf(day);

        return formattedDay;
    }

    private String getFormattedBarColor(ConfigurationSection section, String key) {
        String rawColor = section.getString(key + ".barcolor");

        if (rawColor == null) return "Unknown";

        if (rawColor.equalsIgnoreCase("BLUE")) return "<aqua>BLUE";
        if (rawColor.equalsIgnoreCase("GREEN")) return "<green>GREEN";
        if (rawColor.equalsIgnoreCase("PINK")) return "<light_purple>PINK";
        if (rawColor.equalsIgnoreCase("PURPLE")) return "<dark_purple>PURPLE";
        if (rawColor.equalsIgnoreCase("RED")) return "<red>RED";
        if (rawColor.equalsIgnoreCase("WHITE")) return "<white>WHITE";
        if (rawColor.equalsIgnoreCase("YELLOW")) return "<yellow>YELLOW";

        return "Unknown";
    }
}