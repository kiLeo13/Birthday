package cool.birthday.commands;

import cool.birthday.Birthday;
import cool.birthday.configuration.Birthdays;
import cool.birthday.runnables.MainBossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeneralBirthday implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendRichMessage("<red>You cannot run this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendRichMessage("<red>Incorrect usage!\nSee: <green>/birthday <gold><<yellow>argument<gold>>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {

            if (Birthdays.getInstance().reloadBirthdaysConfig()) {
                sender.sendRichMessage("<dark_gray>[</dark_gray><gold>birthdays.yml</gold><dark_gray>]</dark_gray> <green>Configuration successfully reloaded!");

                Birthday.getPlugin().reloadConfig();
                sender.sendRichMessage("<dark_gray>[</dark_gray><gold>config.yml</gold><dark_gray>]</dark_gray> <green>Configuration successfully reloaded!");

                System.out.println(ChatColor.GREEN + "Files successfully reloaded!");

            } else {
                sender.sendRichMessage("<red>Something went wrong, see console for errors.");
            }
        } else sender.sendRichMessage("<red>Unknown argument.");

        String monthNow = LocalDateTime.now().getMonth().toString().toLowerCase();
        int dayNow = LocalDateTime.now().getDayOfMonth();
        List<String> birthdays = Birthdays.getInstance().getBirthdaysToday(monthNow, dayNow);

        MainBossBar.smartBarSetVisibility();

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return new ArrayList<>() {
                {
                    add("reload");
                }
            };
        }
        return new ArrayList<>();
    }
}