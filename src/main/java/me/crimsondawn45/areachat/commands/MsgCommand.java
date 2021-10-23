package me.crimsondawn45.areachat.commands;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.crimsondawn45.areachat.AreaChat;
import me.crimsondawn45.areachat.util.ChatHelper;
import me.crimsondawn45.datafileplugin.DataFile;
import me.crimsondawn45.datafileplugin.DataFilePlugin;
import net.md_5.bungee.api.ChatColor;

public class MsgCommand implements CommandExecutor {

    private AreaChat plugin;

    public MsgCommand(AreaChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //Load Datafiles
        DataFile areaChatFile = this.plugin.areachat;
        FileConfiguration areaChatData = areaChatFile.getConfig();

        //Load players
        String senderName = sender.getName();
        Set<Player> onlinePlayers = Set.copyOf(this.plugin.getServer().getOnlinePlayers());

        //Load Message
        String recipientName = args[0].strip();
        String msg = String.join(" ", args).replace(args[0], "");
        String msgFormatSender;
        String msgFormatRecipient;
        String finalMsgSender;
        String finalMsgRecipient;

        //Instantiate recipient
        Player recipient = null;

        //Find player
        for(Player player : onlinePlayers) {
            if(DataFilePlugin.getPlayerName(player).startsWith(recipientName)) {    //Search using custom name
                recipient = player;
            } else if(player.getName().startsWith(recipientName)) {    //Search with default name
                recipient = player;
            }
        }

        //Check if player not found
        if(recipient == null) {
            sender.sendMessage(ChatColor.RED + "Unable to find player\"" + recipientName + "\".");
            return true;
        }

        boolean tooFar = false;
        boolean isIgnoring = false;

        //Check distance/ignore for player
        if(sender instanceof Player) {
            Player senderPlayer = (Player) sender;

            if(ChatHelper.isPlayerIgnoringSender(recipient, senderPlayer)) {
               isIgnoring = true;
            }

            if(ChatHelper.isMessageAllCaps(msg)) {

                int screamdistance;
    
                //Check for configuration
                if(areaChatData.contains("scream-distance")) {
                    screamdistance = areaChatData.getInt("scream-distance");
                } else {
                    areaChatData.set("scream-distance", 150);
                    areaChatFile.save(areaChatData);
                    screamdistance = areaChatData.getInt("scream-distance");
                }
    
                tooFar = ChatHelper.isDistanceGreaterThan(recipient, senderPlayer, screamdistance);
    
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

                tooFar = ChatHelper.isDistanceGreaterThan(recipient, senderPlayer, talkdistance);
            }
        }
        

        //Ensure sender-format is from file
        if(areaChatData.contains("msg-command-format-sender")) {
            msgFormatSender = areaChatData.getString("msg-command-format-sender");
        } else {
            areaChatData.set("msg-command-format-sender", "&dYou -> %recipient%: %message%&r");
            areaChatFile.save(areaChatData);
            msgFormatSender = areaChatData.getString("msg-command-format-sender");
        }

        //Ensure recipient-format is from file
        if(areaChatData.contains("msg-command-format-recipient")) {
            msgFormatRecipient = areaChatData.getString("msg-command-format-recipient");
        } else {
            areaChatData.set("msg-command-format-recipient", "&d%sender% -> You: %message%&r");
            areaChatFile.save(areaChatData);
            msgFormatRecipient = areaChatData.getString("msg-command-format-recipient");
        }

        //Generate final message
        finalMsgSender = ChatColor.translateAlternateColorCodes('&', msgFormatSender.replace("%recipient%", DataFilePlugin.getPlayerName(recipient)).replace("%sender%", senderName)).replace("%message%", msg);
        finalMsgRecipient = ChatColor.translateAlternateColorCodes('&', msgFormatRecipient.replace("%recipient", DataFilePlugin.getPlayerName(recipient)).replace("%sender%", senderName)).replace("%message%", msg);

        //Send Message
        sender.sendMessage(finalMsgSender);
        if(!tooFar && !isIgnoring) {
            recipient.sendMessage(finalMsgRecipient);
        }

        return true;
    }
}