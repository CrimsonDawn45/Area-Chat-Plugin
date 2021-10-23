package me.crimsondawn45.areachat.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.crimsondawn45.areachat.AreaChat;
import me.crimsondawn45.areachat.util.ChatHelper;
import me.crimsondawn45.datafileplugin.DataFile;
import net.md_5.bungee.api.ChatColor;

public class MeCommand implements CommandExecutor {

    private AreaChat plugin;

    public MeCommand(AreaChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //Load data
        DataFile areaChatFile = plugin.areachat;
        FileConfiguration areaChatData = areaChatFile.getConfig();

        //Load Players
        String senderName = sender.getName();
        Set<Player> recipients = new HashSet<Player>(this.plugin.getServer().getOnlinePlayers());

        //Instantiate blacklist
        Set<Player> blacklist = new HashSet<Player>();

        //Load Message
        String msg = String.join(" ", args);
        String msgFormat;
        String finalMsg;

        if(sender instanceof Player) {  //If sender is player blacklist players that shouldn't be able to hear it.
            Player senderPlayer = (Player) sender;

            for(Player recipient : recipients) {

                if(senderPlayer.getWorld() != recipient.getWorld()) {   //Disable interdimentional communication
                    blacklist.add(recipient);
                    continue;
                }

                if(ChatHelper.isPlayerIgnoringSender(recipient, senderPlayer)) {  //Check for ignoring
                    blacklist.add(recipient);
                    continue;
                }

                if(ChatHelper.isMessageAllCaps(msg)) {  //Check for screaming

                    int screamdistance;

                    //Check for configuration
                    if(areaChatData.contains("scream-distance")) {
                        screamdistance = areaChatData.getInt("scream-distance");
                    } else {
                        areaChatData.set("scream-distance", 150);
                        areaChatFile.save(areaChatData);
                        screamdistance = areaChatData.getInt("scream-distance");
                    }

                    if(ChatHelper.isDistanceGreaterThan(recipient, senderPlayer, screamdistance)) {  //Check distance 150
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
                        areaChatFile.save(areaChatData);
                        talkdistance = areaChatData.getInt("talk-distance");
                    }

                    if(ChatHelper.isDistanceGreaterThan(recipient, senderPlayer,talkdistance)) {   //Check distance 70
                        blacklist.add(recipient);
                        continue;
                    }
                }
            }
        }

        recipients.removeAll(blacklist);    //Remove blacklisted players

        if(areaChatData.contains("me-command-format")) {        //Ensure format is from file
            msgFormat = areaChatData.getString("me-command-format");
        } else {
            areaChatData.set("me-command-format", "* %sender% %message%");
            areaChatFile.save(areaChatData);
            msgFormat = areaChatData.getString("me-command-format");
        }

        //Handle greentext
        if(msg.startsWith(">")) {
            msg = ChatColor.GREEN + msg;
        }

        //Set format
        finalMsg = ChatColor.translateAlternateColorCodes('&', msgFormat).replace("%sender%", senderName).replace("%message%", msg); //Generate message from format

        for(Player player : recipients) {
            player.sendMessage(finalMsg);
        }
        this.plugin.getServer().getConsoleSender().sendMessage(finalMsg);

        return true;
    }
}