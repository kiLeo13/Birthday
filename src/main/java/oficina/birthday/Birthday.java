package oficina.birthday;

import oficina.birthday.runnables.BirthdayTest;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Birthday extends JavaPlugin {
    private static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        startRunnable();

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.LIGHT_PURPLE + "Birthday plugin is gone, bye bye!");
    }

    public static Plugin getPlugin() { return plugin; }

    private void startRunnable() {
        BirthdayTest test = new BirthdayTest(this);
        test.runTaskTimer(this, 0, 1200);
    }
}