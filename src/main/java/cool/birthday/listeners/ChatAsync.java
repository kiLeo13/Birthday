package cool.birthday.listeners;

import cool.birthday.configuration.Birthdays;
import cool.birthday.runnables.ChatRunnable;
import cool.birthday.runnables.MainBirthday;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;
import java.util.List;

public class ChatAsync implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {

        Player player = event.getPlayer();
        String message = MiniMessage.miniMessage().serialize(event.originalMessage());
        String monthNow = LocalDateTime.now().getMonth().toString();
        int dayNow = LocalDateTime.now().getDayOfMonth();
        List<String> birthdays = Birthdays.getInstance().getBirthdaysToday(monthNow, dayNow);

        if (!isInConfirmation(player)) return;

        if (!message.equalsIgnoreCase("yes") && !message.equalsIgnoreCase("no")) {
            ChatRunnable.removeMappedSender(player);
            player.sendRichMessage("<red>Command canceled. Replied message did not match the expected.");
            return;
        }

        if (message.equalsIgnoreCase("yes")) {
            String key = ChatRunnable.getPlayerCommandArgs(player)[0];
            String realName = ChatRunnable.getPlayerCommandArgs(player)[1];
            BarColor barColor = BarColor.valueOf(ChatRunnable.getPlayerCommandArgs(player)[2].toUpperCase());
            String month = ChatRunnable.getPlayerCommandArgs(player)[3];
            byte day = Byte.parseByte(ChatRunnable.getPlayerCommandArgs(player)[4]);

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
        ChatRunnable.removeMappedSender(player);
    }

    public static boolean isInConfirmation(Player player) { return ChatRunnable.getMappedSenders().contains(player); }
}