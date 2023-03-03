package oficina.birthday;

import oficina.birthday.commands.AddBirthday;
import oficina.birthday.commands.MainBirthday;
import oficina.birthday.commands.RemoveBirthday;
import oficina.birthday.listeners.ChatAsync;
import oficina.birthday.runnables.BirthdayTest;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Birthday extends JavaPlugin {
    private static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Register stuff
        registerCommands();
        registerListeners();

        startRunnable();

        saveDefaultConfig();

    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.LIGHT_PURPLE + "Birthday plugin is gone, bye bye!");
    }

    public static Plugin getPlugin() { return plugin; }

    private void startRunnable() {
        BirthdayTest test = new BirthdayTest();
        test.runTaskTimer(this, 0, 1200);
    }

    private void registerCommands() {
        PluginCommand birthdayAdd = this.getCommand("birthdayadd");
        if (birthdayAdd != null) birthdayAdd.setExecutor(new AddBirthday());

        PluginCommand birthdayRemove = this.getCommand("birthdayremove");
        if (birthdayRemove != null) birthdayRemove.setExecutor(new RemoveBirthday());

        PluginCommand birthday = this.getCommand("birthday");
        if (birthday != null) birthday.setExecutor(new MainBirthday());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatAsync(), this);
    }
}