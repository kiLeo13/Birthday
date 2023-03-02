package oficina.birthday.commands;

import oficina.birthday.configuration.Birthdays;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        if (!hasOnlyLetters(args[0])) {
            player.sendRichMessage("<red>Key input can only contain letters.");
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
            player.sendRichMessage("<red>Day input has to be a number between 1 and 31, see console for errors.");
            e.printStackTrace();
            return true;
        }

        boolean exists = Birthdays.getInstance().birthdayExists(args[0]);
        player.sendRichMessage("<red>" + exists);

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

    private boolean hasOnlyLetters(String arg) {
        arg = arg.toLowerCase();
        String[] array = arg.split("");
        List<Character> letters = new ArrayList<>();

        for (byte i = 0; i < array.length; i++) {
            letters.add(arg.charAt(i));
        }

        for (char i : letters) {
            if (!Character.isLetter(i)) return false;
        }

        return true;
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
}