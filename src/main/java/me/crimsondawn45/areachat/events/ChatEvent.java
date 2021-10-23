package me.crimsondawn45.areachat.events;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.crimsondawn45.areachat.AreaChat;
import me.crimsondawn45.areachat.util.ChatHelper;
import me.crimsondawn45.datafileplugin.DataFile;
import me.crimsondawn45.datafileplugin.DataFilePlugin;
import net.md_5.bungee.api.ChatColor;

public class ChatEvent implements Listener {

    private AreaChat plugin;

    public ChatEvent(AreaChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        //Load Data Files
        DataFile areaChatDataFile = this.plugin.areachat;
        FileConfiguration areaChatData = areaChatDataFile.getConfig();

        //Load Players
        Player sender = event.getPlayer();
        Set<Player> recipients = event.getRecipients();

        //Instantiate blacklist
        Set<Player> blacklist = new HashSet<Player>();

        for(Player recipient : recipients) {
            if(recipient.getWorld() != sender.getWorld()) { //Disable Interdimensional communication
                blacklist.add(recipient);
                continue;
            }

            if(ChatHelper.isPlayerIgnoringSender(recipient, sender)) {  //Check for ignoring
                blacklist.add(recipient);
                continue;
            }

            if(ChatHelper.isMessageAllCaps(event.getMessage())) {   //Check for screaming

                int screamdistance;

                //Check for configuration
                if(areaChatData.contains("scream-distance")) {
                    screamdistance = areaChatData.getInt("scream-distance");
                } else {
                    areaChatData.set("scream-distance", 150);
                    areaChatDataFile.save(areaChatData);
                    screamdistance = areaChatData.getInt("scream-distance");
                }

                if(ChatHelper.isDistanceGreaterThan(recipient, sender, screamdistance)) {  //Check distance 150
                    blacklist.add(recipient);
                    continue;
                }

            } else {

                int talkdistance;

                //Check for configuration
                if(areaChatData.contains("talk-distance")) {
                    talkdistance = areaChatData.getInt("talk-distance");
                } else {
                    areaChatData.set("talk-distance", 70);
                    areaChatDataFile.save(areaChatData);
                    talkdistance = areaChatData.getInt("talk-distance");
                }

                if(ChatHelper.isDistanceGreaterThan(recipient, sender,talkdistance)) {   //Check distance 70
                    blacklist.add(recipient);
                    continue;
                }
            }
        }

        recipients.removeAll(blacklist);    //Remove blacklisted players

        //Check for greentext
        if(event.getMessage().startsWith(">")) {
            event.setMessage(ChatColor.GREEN + event.getMessage());
        }

        if(areaChatData.contains("chat-format")) {  //Ensure chat formatting is set using datafile
            event.setFormat(ChatColor.translateAlternateColorCodes('&', areaChatData.getString("chat-format").replace("%player%", DataFilePlugin.getPlayerName(sender)).replace("%message%", event.getMessage())));
        } else {
            areaChatData.set("chat-format", "<%player%> %message%");
            areaChatDataFile.save(areaChatData);
            event.setFormat(ChatColor.translateAlternateColorCodes('&', areaChatData.getString("chat-format").replace("%player%", DataFilePlugin.getPlayerName(sender)).replace("%message%", event.getMessage())));
        }
    }
}