package oficina.birthday.configuration;

import oficina.birthday.Birthday;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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

    public boolean addBirthDay(String key, String realName, byte month, byte day) {

    }

    public boolean removeBirthday(String key) {
        
    }
}