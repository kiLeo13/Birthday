package oficina.birthday.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;

public class BirthdayTest extends BukkitRunnable {
    private static String month = LocalDateTime.now().getMonth().toString().toLowerCase();
    private static int day = LocalDateTime.now().getDayOfMonth();

    @Override
    public void run() {
        System.out.println(month + " / " + day);
        runTest();
    }

    private void runTest() {
    }
}