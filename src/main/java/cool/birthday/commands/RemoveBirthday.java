package cool.birthday.commands;

import cool.birthday.Birthday;
import cool.birthday.configuration.Birthdays;
import cool.birthday.runnables.ChatRunnable;
import cool.birthday.runnables.MainBirthday;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RemoveBirthday implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendRichMessage("<red>Only players can run this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendRichMessage("<red>Incorrect usage!\nSee: <green>/birthdayremove <gold><<yellow>key<gold>>");
            return true;
        }

        Birthdays.getInstance().reloadBirthdaysConfig();

        if (!Birthdays.getInstance().birthdayExists(args[0])) {
            sender.sendRichMessage("<red>Key not found. Register a new one using <yellow>/birthdayadd");
            return true;
        }

        String monthNow = LocalDateTime.now().getMonth().toString();
        int dayNow = LocalDateTime.now().getDayOfMonth();
        List<String> birthdays = Birthdays.getInstance().getBirthdaysToday(monthNow, dayNow);
        boolean hasDeleteConfirmation = Birthday.getPlugin().getConfig().getBoolean("delete-confirmation");

        String realName = getRealNameBirthday(args[0]);
        if (realName == null) realName = "Unknown";

        if (hasDeleteConfirmation) {
            sender.sendRichMessage("<gold>==================================================</gold>\n\n<gray><b>|</b></gray> <red>Are you sure you want to delete it? This process cannot be undone.\n\n<yellow>Reply with: <green><b>YES</b></green> or <red><b>NO</b></red>.</yellow>");
            ChatRunnable.addDeletingMappingSender(sender, args[0]);
            return true;
        }

        Birthdays.getInstance().removeBirthday(args[0]);
        MainBirthday.updateBossBar(birthdays);

        if (realName.endsWith("s")) sender.sendRichMessage("<gold>==================================================</gold>\n<gray><b>╰</b></gray><dark_gray>[<light_purple>" + args[0] + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "'</gold><green> birthday has been successfully removed!</green>\n\n<gold>==================================================</gold>");
        else sender.sendRichMessage("<gold>==================================================</gold>\n<gray><b>╰</b></gray><dark_gray>[<light_purple>" + args[0] + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "'s</gold><green> birthday has been successfully removed!</green>\n\n<gold>==================================================</gold>");

        if (birthdays.isEmpty()) MainBirthday.getBossBar().removeAll();
        else {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            players.forEach(player -> MainBirthday.getBossBar().addPlayer(player));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) return Birthdays.getInstance().getBirthdays(args[0]);

        return new ArrayList<>();
    }

    private String getRealNameBirthday(String key) {
        ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");

        if (section == null) return null;

        return section.getString(key + ".name");
    }
}