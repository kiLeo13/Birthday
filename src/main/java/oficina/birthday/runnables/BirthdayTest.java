package oficina.birthday.runnables;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BirthdayTest extends BukkitRunnable {
    private final Plugin plugin;

    public BirthdayTest(Plugin plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        runTest();
    }

    private void runTest() {

    }
}