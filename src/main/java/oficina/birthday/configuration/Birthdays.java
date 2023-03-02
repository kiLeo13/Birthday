package oficina.birthday.configuration;

import oficina.birthday.Birthday;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Birthdays {
    private static Birthdays instance;

    private final String fileName = "birthdays.yml";
    private final File dataFile = new File(Birthday.getPlugin().getDataFolder(), fileName);
    private final FileConfiguration birthdaysConfig = new YamlConfiguration();

    private Birthdays() {
        if (!dataFile.exists()) Birthday.getPlugin().saveResource(fileName, false);
        reloadBirthdaysConfig();
    }

    public static Birthdays getInstance() {
        if (instance == null) instance = new Birthdays();
        return instance;
    }

    public FileConfiguration getBirthdaysConfig() { return birthdaysConfig; }

    public boolean reloadBirthdaysConfig() {
        try {
            birthdaysConfig.load(dataFile);
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addBirthDay(String key, String realName, BarColor barcolor, String month, byte day) {
        if (!months().contains(month.toLowerCase())) throw new IllegalArgumentException("Invalid month input");
        if (!dayExists(day, month.toLowerCase())) throw new IllegalArgumentException("Invalid day input");

        ConfigurationSection section = getBirthdaysConfig().getConfigurationSection("birthdays");
    }

    public boolean removeBirthday(String key) {
        return true;
    }

    private boolean dayExists(byte day, String month) {
        if (day < 1 || day > 31) return false;

        return !(day > 30 && !months31().contains(month) || (day > 29 && month.equalsIgnoreCase("february")));
    }

    public List<String> months() {
        return new ArrayList<>() {
            {
                add("january");
                add("february");
                add("march");
                add("april");
                add("may");
                add("june");
                add("july");
                add("august");
                add("september");
                add("october");
                add("november");
                add("december");
            }
        };
    }

    public List<String> months31() {
        return new ArrayList<>() {
            {
                add("january");
                add("march");
                add("may");
                add("july");
                add("august");
                add("october");
                add("december");
            }
        };
    }

    public List<String> months30() {
        return new ArrayList<>() {
            {
                add("february");
                add("april");
                add("june");
                add("september");
                add("november");
            }
        };
    }
}