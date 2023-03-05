package cool.birthday.commands;

import cool.birthday.configuration.Birthdays;
import cool.birthday.runnables.MainBirthday;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
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

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can run this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendRichMessage("<red>Incorrect usage!\nSee: <green>/birthdayremove <gold><<yellow>key<gold>>");
            return true;
        }

        Birthdays.getInstance().reloadBirthdaysConfig();

        String monthNow = LocalDateTime.now().getMonth().toString();
        int dayNow = LocalDateTime.now().getDayOfMonth();
        List<String> birthdays = Birthdays.getInstance().getBirthdaysToday(monthNow, dayNow);

        Birthdays.getInstance().removeBirthday(args[0]);
        MainBirthday.updateBossBar(birthdays);

        if (birthdays.isEmpty()) MainBirthday.getBossBar().removeAll();
        else {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            players.forEach(player1 -> MainBirthday.getBossBar().addPlayer(player));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) return Birthdays.getInstance().getBirthdays(args[0]);

        return new ArrayList<>();
    }
}