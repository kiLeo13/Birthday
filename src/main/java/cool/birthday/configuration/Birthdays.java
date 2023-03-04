package cool.birthday.configuration;

import cool.birthday.Birthday;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void addBirthDay(String key, String realName, BarColor barcolor, String month, byte day) throws IllegalArgumentException {
        if (!months().contains(month.toLowerCase())) throw new IllegalArgumentException("Invalid month input");
        if (!dayExists(day, month.toLowerCase())) throw new IllegalArgumentException("Invalid day input");

        ConfigurationSection section = getBirthdaysConfig().getConfigurationSection("birthdays");

        if (section == null) section = getBirthdaysConfig().createSection("birthdays");

        section.set(key + ".name", realName);
        section.set(key + ".barcolor", barcolor.toString());
        section.set(key + ".month", month);
        section.set(key + ".day", day);

        saveConfig();
    }

    public void removeBirthday(String key) {
        ConfigurationSection section = getBirthdaysConfig().getConfigurationSection("birthdays");

        if (section != null) section.set(key, null);

        saveConfig();
    }

    public boolean birthdayExists(String arg) {
        ConfigurationSection section = getBirthdaysConfig().getConfigurationSection("birthdays." + arg);

        return section != null;
    }

    public List<String> getBirthdays(String arg) {
        ConfigurationSection section = getBirthdaysConfig().getConfigurationSection("birthdays");
        String args = arg.toLowerCase();

        if (section != null) return section.getKeys(false).stream()
                .filter(name -> name.toLowerCase().startsWith(args))
                .collect(Collectors.toList());

        return new ArrayList<>();
    }

    /* Birthday Getters */

    public String getBirthdayName(String key) {
        ConfigurationSection section = getBirthdaysConfig().getConfigurationSection("birthdays");

        if (section != null) return String.valueOf(section.get(key + ".name"));
        return null;
    }

    public BarColor getBirthdayBarColor(String key) {
        ConfigurationSection section = getBirthdaysConfig().getConfigurationSection("birthdays");

        if (section != null) return BarColor.valueOf(String.valueOf(section.get(key + ".barcolor")));
        return null;
    }

    public List<String> getBirthdaysToday(String todayMonth, int todayDay) {
        if (!months().contains(todayMonth) || !dayExists(todayDay, todayMonth)) throw new IllegalArgumentException("Improper date");

        ConfigurationSection section = getBirthdaysConfig().getConfigurationSection("birthdays");
        List<String> birthdays = new ArrayList<>();

        if (section != null) {
            Set<String> keys = section.getKeys(false);

            for (String key : keys) {
                String month = section.getString(key + ".month");
                int day = section.getInt(key + ".day");
                if (todayMonth.equalsIgnoreCase(month) && todayDay == day) birthdays.add(key);
            }
        }
        return birthdays;
    }

    private void saveConfig() {
        try {
            getBirthdaysConfig().save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Resources */

    private boolean dayExists(int day, String month) {
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