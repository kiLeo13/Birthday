package oficina.birthday.commands;

import oficina.birthday.configuration.Birthdays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RemoveBirthday implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can run this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendRichMessage("<red>Incorrect usage!\nSee: <green>/birthdayremove <gold><<yellow>key<gold>>");
            return true;
        }

        Birthdays.getInstance().removeBirthday(args[0]);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) return Birthdays.getInstance().getBirthdays(args[0]);

        return new ArrayList<>();
    }
}