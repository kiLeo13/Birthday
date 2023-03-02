package oficina.birthday;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Birthday extends JavaPlugin {

    @Override
    public void onEnable() {


        saveDefaultConfig();

    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.LIGHT_PURPLE + "Birthday plugin is gone, bye bye!");
    }
}