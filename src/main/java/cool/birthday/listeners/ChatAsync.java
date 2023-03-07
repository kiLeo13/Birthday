package cool.birthday.listeners;

import cool.birthday.configuration.Birthdays;
import cool.birthday.runnables.ChatRunnable;
import cool.birthday.runnables.MainBossBar;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatAsync implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {
        manageCreateConfirm(event);

        manageDeleteCofirm(event);
    }

    public static boolean isInCreateConfirmation(Player player) { return ChatRunnable.getMappedCreatingSenders().contains(player); }

    public static boolean isInDeleteConfirmation(Player player) { return ChatRunnable.getMappedDeletingSenders().contains(player); }

    private void manageCreateConfirm(AsyncChatEvent event) {

        Player player = event.getPlayer();
        String message = MiniMessage.miniMessage().serialize(event.originalMessage());

        if (!isInCreateConfirmation(player)) return;

        if (!message.equalsIgnoreCase("yes") && !message.equalsIgnoreCase("no")) {
            ChatRunnable.removeCreatingMappedSender(player);
            player.sendRichMessage("\n<gray><b>|</b></gray> <red>Command canceled. Replied message did not match the expected.\n");
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

                if (realName.endsWith("s")) player.sendRichMessage("\n<dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray>\n\n<b>|</b> <gold>" + realName + "</gold><yellow>' birthday has been successfully overridden!</yellow>\n");
                else player.sendRichMessage("\n<dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "</gold><yellow>'s birthday has been successfully overridden!</yellow>\n");
            } catch (IllegalArgumentException e) {
                player.sendRichMessage("\n<gray><b>|</b></gray> <red>Something went wrong, are all values set properly? Check console for errors.\n");
                e.printStackTrace();
            }

        } else {
            player.sendRichMessage("\n<gray><b>|</b></gray> <yellow>Okay! Birthday hasn't been added or overridden.\n");
        }

        MainBossBar.smartBarSetVisibility();

        event.setCancelled(true);
        ChatRunnable.removeCreatingMappedSender(player);
    }

    private void manageDeleteCofirm(AsyncChatEvent event) {

        ConfigurationSection section = Birthdays.getInstance().getBirthdaysConfig().getConfigurationSection("birthdays");

        Player player = event.getPlayer();
        String message = MiniMessage.miniMessage().serialize(event.originalMessage());

        if (section == null) {
            player.sendRichMessage("\n<gray><b>|</b></gray> <red>There was a problem attempting to remove the birthday. Section 'birthdays' is null. Check <gold>birthdays.yml<red> file or delete it to generate a new one.\n");
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

            if (realName.endsWith("s")) player.sendRichMessage("\n<dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "'</gold><green> birthday has been successfully removed!</green>\n");
            else player.sendRichMessage("\n<dark_gray>[<light_purple>" + key + "</light_purple>]</dark_gray>\n\n<gray><b>|</b></gray> <gold>" + realName + "'s</gold><green> birthday has been successfully removed!</green>\n");
        } else {
            player.sendRichMessage("\n<gray><b>|</b></gray> <yellow>Okay! Birthday will not be deleted.\n");
        }

        MainBossBar.smartBarSetVisibility();

        ChatRunnable.removeDeletingMappedSender(player);
        event.setCancelled(true);
    }
}