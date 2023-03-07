package cool.birthday.commands;

import cool.birthday.Birthday;
import cool.birthday.configuration.Birthdays;
import cool.birthday.runnables.ChatRunnable;
import cool.birthday.runnables.MainBossBar;
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
            sender.sendRichMessage("\n<gray><b>|</b></gray> <red>Key not found. Register a new one using <yellow>/birthdayadd\n");
            return true;
        }

        boolean hasDeleteConfirmation = Birthday.getPlugin().getConfig().getBoolean("delete-confirmation");

        String realName = getRealNameBirthday(args[0]);
        if (realName == null) realName = "Unknown";

        if (hasDeleteConfirmation) {
            sender.sendRichMessage("\n<dark_gray>[<light_purple>" + args[0] + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <red>Are you sure you want to delete it?\n<gray><b>|</b></gray> This process cannot be undone.\n\n<yellow>Reply with: <green><b>YES</b></green> or <red><b>NO</b></red>.</yellow>\n");
            ChatRunnable.addDeletingMappingSender(sender, args[0]);
            return true;
        }

        Birthdays.getInstance().removeBirthday(args[0]);
        MainBossBar.smartBarSetVisibility();

        if (realName.endsWith("s")) sender.sendRichMessage("\n<dark_gray>[<light_purple>" + args[0] + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "'</gold><green> birthday has been successfully removed!</green>\n");
        else sender.sendRichMessage("\n<dark_gray>[<light_purple>" + args[0] + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "'s</gold><green> birthday has been successfully removed!</green>\n");

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