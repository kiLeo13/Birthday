package cool.birthday.commands;

import cool.birthday.configuration.Birthdays;
import cool.birthday.runnables.ChatRunnable;
import cool.birthday.runnables.MainBossBar;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AddBirthday implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendRichMessage("<red>Only players can run this command.");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendRichMessage("\n<dark_gray>[<gold>Help</gold>]</dark_gray>\n\n<red><b>|</b></red> <yellow><></yellow> <gray>Means <i>required</i></gray>\n<yellow><b>|</b></yellow> <yellow>[]</yellow> <gray>Means <i>optional</i></gray>\n\n<gray><b>|</b></gray> <red><key></red> <gray>=</gray> <yellow>Just a way to internally referenciate to that player.</yellow>\n<gray><b>|</b></gray> <red><realname></red> <gray>=</gray> <yellow>The name that will be displayed to the others.</yellow>\n<gray><b>|</b></gray> <red><barcolor></red> <gray>=</gray> <yellow>The color of the BossBar of that player.</yellow>\n<gray><b>|</b></gray> <red><year></red> <gray>=</gray> <yellow>The year that the person was born.</yellow>\n<gray><b>|</b></gray> <red><month></red> <gray>=</gray> <yellow>The birthday month of that person.</yellow>\n<gray><b>|</b></gray> <red><day></red> <gray>=</gray> <yellow>The birthday day of that person.</yellow>");
            return true;
        }

        if (args.length != 6) {
            sender.sendRichMessage("<red>Incorrect usage!\nSee: <green>/birthdayadd <gold><<yellow>key<gold>> <gold><<yellow>realname<gold>> <gold><<yellow>barcolor<gold>> <gold><<yellow>month<gold>> <gold><<yellow>day<gold>> <<yellow>age<gold>>\n\n<yellow>/birthdayadd help<red> to see what each option requires.");
            return true;
        }

        if (!isColorFine(args[2].toUpperCase())) {
            sender.sendRichMessage("<red>BarColor input invalid.\nPossible values: " + barColorsString());
            return true;
        }

        String key = args[0];
        String realName = args[1];
        BarColor barColor = BarColor.valueOf(args[2].toUpperCase());
        long yearBorn;
        String month = args[3].toLowerCase();
        byte day;

        try { day = Byte.parseByte(args[4]); }
        catch (NumberFormatException e) {
            sender.sendRichMessage("<red>Day input has to be a number between 1 and 31, see console for errors.");
            e.printStackTrace();
            return true;
        }

        try { yearBorn = Long.parseLong(args[5]); }
        catch (NumberFormatException e) {
            yearBorn = -1;
        }

        if (!Birthdays.getInstance().months().contains(month)) {
            sender.sendRichMessage("<red>Month input invalid. Please write the name of a month, don't use numbers.");
            return true;
        }

        if (!Birthdays.getInstance().dayExists(day, month)) {
            sender.sendRichMessage("<red>The provided day does not exist in the given month, please, provide a valid one.");
            return true;
        }

        boolean birthdayExists = Birthdays.getInstance().birthdayExists(args[0]);

        if (birthdayExists) {
            ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");

            if (section == null) {
                sender.sendRichMessage("<red>There was a problem attempting to create a new birthday because section 'birthdays' is null. Check <gold>birthdays.yml<red> file or delete it to generate a new one.");
                return true;
            }

            String currentName = section.getString(key + ".name");
            String currentBarColor = getFormattedBarColor(section, key);
            String currentYear = getFormattedYear(section, key);
            String currentMonth = getFormattedMonth(section, key);
            String currentDay = getFormattedDay(section, key, sender);

            String currentRealBarColor = section.getString(key + ".barcolor");
            long currentRealYear;
            byte currentRealDay = 0;

            try {
                currentRealYear = section.getLong(key + ".year");
            } catch (NumberFormatException e) {
                currentRealYear = 0;
            }

            try {
                currentRealDay = (byte) section.getInt(key + ".day");
            } catch (NumberFormatException e) {
                sender.sendRichMessage("<red>Something went wrong attempting to fetch the day of the player's birthday so 0 will be displayed instead. Check console for errors");
                e.printStackTrace();
            }

            if (currentName == null) currentName = "Unknown";

            if (areTheSame(currentName, currentBarColor, currentRealYear, currentMonth, currentRealDay, realName, currentRealBarColor, yearBorn, month, day)) {
                sender.sendRichMessage("\n<gray><b>|</b></gray> <red>The information you provided is already assigned to this register.\n");
                return true;
            }

            sender.sendRichMessage("\n<dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray> <yellow>There is already a key with this name, would like to override it?\n\n<red>Name: <yellow>" + currentName + "</yellow>\nBarColor: <yellow>" + currentBarColor + "</yellow>\nYear: <yellow>" + currentYear + "</yellow>\nDate: <yellow>" + currentMonth + ", " + currentDay + "</yellow>\n\nReply with: <green><b>YES</b></green> <yellow>or</yellow> <red><b>NO</b></red>");
            ChatRunnable.addCreatingMappingSender(sender, args);
            return true;
        }

        try {
            Birthdays.getInstance().addBirthDay(key, realName, barColor, month, day, yearBorn);

            MainBossBar.smartBarSetVisibility();

            if (realName.endsWith("s")) sender.sendRichMessage("\n<dark_gray>[<light_purple>" + key + "</light_purple>]></dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "</gold><green>' birthday has been successfully registered!</green>\n");
            else sender.sendRichMessage("\n<dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "</gold><green>'s birthday has been successfully registered!</green>\n");
        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Something went wrong, are all values set properly? Check console for errors.");
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return allPlayers(args[0].toLowerCase());

        if (args[0].equalsIgnoreCase("help")) return new ArrayList<>();

        if (args.length == 3) return barColor(args[2].toLowerCase());
        if (args.length == 4) return months(args[3].toLowerCase());
        if (args.length == 5) return days(args[3].toLowerCase());

        return new ArrayList<>();
    }

    private List<String> barColor(String arg) {
        List<String> colors = new ArrayList<>();

        for (BarColor i : BarColor.values())
            colors.add(i.toString());

        return colors.stream().filter(e -> e.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }

    private List<String> months(String arg) {
        List<String> months = Birthdays.getInstance().months();

        return months.stream().filter(e -> e.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }

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

    private List<String> allPlayers(String arg) {
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        List<String> finalPlayers = new ArrayList<>();

        for (OfflinePlayer i : offlinePlayers)
            finalPlayers.add(i.getName());

        finalPlayers.add("help");

        return finalPlayers.stream().filter(e -> e.toLowerCase().startsWith(arg)).collect(Collectors.toList());
    }

    private boolean isColorFine(String arg) {
        try {
            BarColor.valueOf(arg.toUpperCase());
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

    private String getFormattedDay(ConfigurationSection section, String key, CommandSender sender) throws NumberFormatException {
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

        if (rawColor.equalsIgnoreCase("BLUE")) return "<aqua>BLUE</aqua>";
        if (rawColor.equalsIgnoreCase("GREEN")) return "<green>GREEN</green>";
        if (rawColor.equalsIgnoreCase("PINK")) return "<light_purple>PINK</light_purple>";
        if (rawColor.equalsIgnoreCase("PURPLE")) return "<dark_purple>PURPLE</dark_purple>";
        if (rawColor.equalsIgnoreCase("RED")) return "<red>RED</red>";
        if (rawColor.equalsIgnoreCase("WHITE")) return "<white>WHITE</white>";
        if (rawColor.equalsIgnoreCase("YELLOW")) return "<yellow>YELLOW</yellow>";

        return "Unknown";
    }

    private boolean areTheSame(String currName, String currBarColor, long currYear, String currMonth, byte currDay, String newName, String newBarColor, long newYear, String newmonth, byte newDay) {
        List<Boolean> finalTesting = new ArrayList<>();

        if (currName.equals(newName)) finalTesting.add(true);
        else finalTesting.add(false);

        if (currBarColor.equalsIgnoreCase(newBarColor)) finalTesting.add(true);
        else finalTesting.add(false);

        if (currYear == newYear) finalTesting.add(true);
        else finalTesting.add(false);

        if (currMonth.equalsIgnoreCase(newmonth)) finalTesting.add(true);
        else finalTesting.add(false);

        if (currDay == newDay) finalTesting.add(true);
        else finalTesting.add(false);

        for (boolean i : finalTesting)
            if (!i) return false;

        return true;
    }

    private String getFormattedYear(ConfigurationSection section, String key) {
        long year;
        StringBuilder builder = new StringBuilder();

        try {
            year = section.getLong(key + ".year");
        } catch (NumberFormatException e) {
            return null;
        }

        if (year < 0)
            builder.append(year * -1).append(" B.C");
        else
            builder.append(year);

        return builder.toString().stripTrailing();
    }
}