package cool.birthday.listeners;

import cool.birthday.configuration.Birthdays;
import cool.birthday.runnables.ChatRunnable;
import cool.birthday.runnables.MainBirthday;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;
import java.util.List;

public class ChatAsync implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {

        String monthNow = LocalDateTime.now().getMonth().toString();
        int dayNow = LocalDateTime.now().getDayOfMonth();
        List<String> birthdays = Birthdays.getInstance().getBirthdaysToday(monthNow, dayNow);

        manageCreateConfirm(event, birthdays);

        manageDeleteCofirm(event, birthdays);
    }

    public static boolean isInCreateConfirmation(Player player) { return ChatRunnable.getMappedCreatingSenders().contains(player); }

    public static boolean isInDeleteConfirmation(Player player) { return ChatRunnable.getMappedDeletingSenders().contains(player); }

    private void manageCreateConfirm(AsyncChatEvent event, List<String> birthdays) {

        Player player = event.getPlayer();
        String message = MiniMessage.miniMessage().serialize(event.originalMessage());

        if (!isInCreateConfirmation(player)) return;

        if (!message.equalsIgnoreCase("yes") && !message.equalsIgnoreCase("no")) {
            ChatRunnable.removeCreatingMappedSender(player);
            player.sendRichMessage("<red>Command canceled. Replied message did not match the expected.");
            return;
        }

        if (message.equalsIgnoreCase("yes")) {
            String key = ChatRunnable.getPlayerCreatingCommandArgs(player)[0];
            String realName = ChatRunnable.getPlayerCreatingCommandArgs(player)[1];
            BarColor barColor = BarColor.valueOf(ChatRunnable.getPlayerCreatingCommandArgs(player)[2].toUpperCase());
            String month = ChatRunnable.getPlayerCreatingCommandArgs(player)[3];
            byte day = Byte.parseByte(ChatRunnable.getPlayerCreatingCommandArgs(player)[4]);

            try {
                Birthdays.getInstance().addBirthDay(key, realName, barColor, month, day);
                MainBirthday.updateBossBar(birthdays);

                if (realName.endsWith("s")) player.sendRichMessage("<gold>==================================================</gold>\n<gray><b>╰</b></gray><dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray>\n\n<b>|</b> <gold>" + realName + "</gold><yellow>' birthday has been successfully overriden!</yellow>\n\n<gold>==================================================</gold>");
                else player.sendRichMessage("<gold>==================================================</gold>\n<gray><b>╰</b></gray><dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "</gold><yellow>'s birthday has been successfully overriden!</yellow>\n\n<gold>==================================================</gold>");
            } catch (IllegalArgumentException e) {
                player.sendRichMessage("<red>Something went wrong, are all values set properly? Check console for errors.");
                e.printStackTrace();
            }

        } else {
            player.sendRichMessage("<yellow>Okay! Birthday hasn't been added or overriden.");
        }

        event.setCancelled(true);
        ChatRunnable.removeCreatingMappedSender(player);
    }

    private void manageDeleteCofirm(AsyncChatEvent event, List<String> birthdays) {

        ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");

        Player player = event.getPlayer();
        String message = MiniMessage.miniMessage().serialize(event.originalMessage());

        if (section == null) {
            player.sendRichMessage("<red>There was a problem attempting to remove the birthday. Section 'birthdays' is null. Check <gold>birthdays.yml<red> file or delete it to generate a new one.");
            return;
        }

        if (!isInDeleteConfirmation(player)) return;

        if (!message.equalsIgnoreCase("yes") && !message.equalsIgnoreCase("no")) {
            player.sendRichMessage("<red>Command canceled.");
            ChatRunnable.removeDeletingMappedSender(player);
            return;
        }

        if (message.equalsIgnoreCase("yes")) {
            String key = ChatRunnable.getPlayerDeletingCommandArg(player);
            String realName = section.getString(key + ".name");

            if (realName == null) realName = "Unknown";

            Birthdays.getInstance().removeBirthday(key);
            MainBirthday.updateBossBar(birthdays);

            if (realName.endsWith("s")) player.sendRichMessage("<gold>==================================================</gold>\n<gray><b>╰</b></gray><dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "'</gold><green> birthday has been successfully removed!</green>\n\n<gold>==================================================</gold>");
            else player.sendRichMessage("<gold>==================================================</gold>\n<gray><b>╰</b></gray><dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "'s</gold><green> birthday has been successfully removed!</green>\n\n<gold>==================================================</gold>");
        } else {
            player.sendRichMessage("<yellow>Okay! Birthday hasn't been removed.");
        }

        ChatRunnable.removeDeletingMappedSender(player);
        event.setCancelled(true);
    }
}