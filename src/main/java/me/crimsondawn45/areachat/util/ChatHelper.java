package me.crimsondawn45.areachat.util;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.crimsondawn45.datafileplugin.DataFilePlugin;

public class ChatHelper {

    public static boolean isPlayerIgnoringSender(Player recipient, Player sender) {

        //Grab player data
        FileConfiguration playerDatafile = DataFilePlugin.getPlayerData().getConfig();

        //Grab uuid's
        String recipientUuid = recipient.getUniqueId().toString();
        String senderUuid = sender.getUniqueId().toString();

        if(playerDatafile.contains("player." + recipientUuid + ".ignored")) {

            List<String> ignoredList = playerDatafile.getStringList("player." + recipientUuid + ".ignored");

            if(ignoredList.isEmpty()) {
                return false;
            }
            
            return ignoredList.contains(senderUuid);

        } else {
            return false;
        }
    }

    public static boolean isDistanceGreaterThan(Player recipient, Player sender, int limit) {
        return (recipient.getLocation().distance(sender.getLocation()) >= limit);
    }

    public static boolean isMessageAllCaps(String msg) {
        return (msg.toUpperCase() == msg);
    }
}
