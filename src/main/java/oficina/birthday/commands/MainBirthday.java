package oficina.birthday.commands;

import oficina.birthday.Birthday;
import oficina.birthday.configuration.Birthdays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainBirthday implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendRichMessage("<red>You cannot run this command.");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (Birthdays.getInstance().reloadBirthdaysConfig()) {
                sender.sendRichMessage("<gray>[</gray><gold>birthdays.yml</gold><gray>]</gray> <green>Configuration successfully reloaded!");

                Birthday.getPlugin().reloadConfig();
                sender.sendRichMessage("<gray>[</gray><gold>config.yml</gold><gray>]</gray> <green>Configuration successfully reloaded!");

                System.out.println(ChatColor.GREEN + "Files successfully reloaded!");
            } else {
                sender.sendRichMessage("<red>Something went wrong, see console for errors.");
            }
            return true;
        } else sender.sendRichMessage("<red>Unknown argument.");

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