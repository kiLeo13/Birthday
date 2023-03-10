package cool.birthday;

import cool.birthday.commands.AddBirthday;
import cool.birthday.commands.GeneralBirthday;
import cool.birthday.commands.GetBirthday;
import cool.birthday.listeners.CommandProcess;
import cool.birthday.listeners.PlayerJoin;
import cool.birthday.runnables.ChatRunnable;
import cool.birthday.runnables.MainBossBar;
import cool.birthday.commands.RemoveBirthday;
import cool.birthday.listeners.ChatAsync;
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

        // Create BossBar
        MainBossBar.createBossBar();
    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.LIGHT_PURPLE + "Birthday plugin is gone, bye bye!");
    }

    public static Plugin getPlugin() { return plugin; }

    private void startRunnable() {
        MainBossBar test = new MainBossBar(this);
        test.runTaskTimer(this, 0, 20);

        ChatRunnable chat = new ChatRunnable();
        chat.runTaskTimer(this, 0, 20);
    }

    private void registerCommands() {
        PluginCommand birthdayAdd = this.getCommand("birthdayadd");
        if (birthdayAdd != null) birthdayAdd.setExecutor(new AddBirthday());

        PluginCommand birthdayRemove = this.getCommand("birthdayremove");
        if (birthdayRemove != null) birthdayRemove.setExecutor(new RemoveBirthday());

        PluginCommand birthday = this.getCommand("birthday");
        if (birthday != null) birthday.setExecutor(new GeneralBirthday());

        PluginCommand birthdayGet = this.getCommand("birthdayget");
        if (birthdayGet != null) birthdayGet.setExecutor(new GetBirthday());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatAsync(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new CommandProcess(), this);
    }
}