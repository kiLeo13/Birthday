package cool.birthday.commands;

import cool.birthday.configuration.Birthdays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GetBirthday implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendRichMessage("<red>You cannot run this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendRichMessage("<red>Incorrect usage!\nSee: <green>/birthdayget <gold><<yellow>player<gold> [<yellow>information<gold>]");
            return true;
        }

        if (!Birthdays.getInstance().birthdayExists(args[0])) {
            sender.sendRichMessage("<red>Key not found. Try creating a new one using <yellow>/birthdayadd");
            return true;
        }

        ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");

        if (section == null) {
            sender.sendRichMessage("<red>Could not find section 'birthdays', check <gold>birthdays.yml<red> file or delete it to generate a new one.");
            return true;
        }

        String key = args[0];
        String name = section.getString(key + ".name");
        String barColor = section.getString(key + ".barcolor");
        String month = section.getString(key + ".month");
        int day = section.getInt(key + ".day");

        if (args.length == 1) {

            sender.sendRichMessage("");

        }




        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return keys(args[0]);
        if (args.length == 2) return getArgumentInformation(args[1]);

        return new ArrayList<>();
    }

    private List<String> keys(String arg) {
        ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");
        List<String> birthdays = new ArrayList<>();

        if (section == null) return birthdays;
        Set<String> keys = section.getKeys(false);

        birthdays.addAll(keys);

        return birthdays.stream().filter(e -> e.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }

    private List<String> getArgumentInformation(String arg) {
        ArrayList<String> info = new ArrayList<>() {
            {
                add("name");
                add("barcolor");
                add("month");
                add("day");
            }
        };

        return info.stream().filter(e -> e.toLowerCase().startsWith(arg)).collect(Collectors.toList());
    }
}