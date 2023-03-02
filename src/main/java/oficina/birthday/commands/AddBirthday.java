package oficina.birthday.commands;

import oficina.birthday.configuration.Birthdays;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

        if (barColors(args[2]) == null) {
            player.sendRichMessage("<red>This color is not valid!\nPossible values: " + barColors(args[2]));
            return true;
        }

        if (!hasOnlyLetters(args[0])) {
            player.sendRichMessage("<red>Key input can only contain letters.");
            return true;
        }

        byte month;
        byte day;
        String key = args[0];

        try { month = Byte.parseByte(args[3]); }
        catch (NumberFormatException e) {
            player.sendRichMessage("<red>Month input has to be a number between 1 and 12, see console for errors.");
            e.printStackTrace();
            return true;
        }

        try { day = Byte.parseByte(args[4]); }
        catch (NumberFormatException e) {
            player.sendRichMessage("<red>Day input has to be a number between 1 and 31, see console for errors.");
            e.printStackTrace();
            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return allPlayers();
        if (args.length == 3) return thirdArg();
        if (args.length == 4) return fourthArg();
        if (args.length == 5) return fifthArg(args[4]);

        return new ArrayList<>();
    }

    private ArrayList<String> thirdArg() {
        return new ArrayList<>() {
            {
                add("BLUE");
                add("GREEN");
                add("PINK");
                add("PURPLE");
                add("RED");
                add("WHITE");
                add("YELLOW");
            }
        };
    }

    private List<String> fourthArg() { return Birthdays.getInstance().months(); }

    private List<String> fifthArg(String args) { return days(args); }

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

    private String barColors(String arg) {
        List<String> colors = new ArrayList<>() {
            {
                add("BLUE");
                add("GREEN");
                add("PINK");
                add("PURPLE");
                add("RED");
                add("WHITE");
                add("YELLOW");
            }
        };

        if (!colors.contains(arg)) return null;

        StringBuilder builder = new StringBuilder();
        builder.append("<gray>[</gray>");

        for (String i : colors)
            builder.append("<gold>").append(i).append("<gray>,</gray> ");

        String finalMessage = builder.toString();
        finalMessage = finalMessage.stripTrailing();

        finalMessage += "<gray>]</gray>";
        return finalMessage;
    }

    private List<String> allPlayers() {
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        List<String> finalPlayers = new ArrayList<>();

        for (OfflinePlayer i : offlinePlayers)
            finalPlayers.add(i.getName());

        return finalPlayers;
    }

    private boolean hasOnlyLetters(String arg) {
        String[] array = arg.split("");
        List<Character> letters = new ArrayList<>();

        for (byte i = 0; i < array.length; i++) {
            letters.add(array[i].charAt(i));
        }

        for (char i : letters) {
            if (!Character.isLetter(i)) return false;
        }

        return true;
    }
}