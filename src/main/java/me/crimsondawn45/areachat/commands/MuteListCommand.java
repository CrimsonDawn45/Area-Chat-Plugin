package me.crimsondawn45.areachat.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.crimsondawn45.areachat.AreaChat;
import me.crimsondawn45.datafileplugin.DataFile;
import me.crimsondawn45.datafileplugin.DataFilePlugin;
import net.md_5.bungee.api.ChatColor;

public class MuteListCommand implements CommandExecutor {
    
    private AreaChat plugin;

    public MuteListCommand(AreaChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {

            //Grab player
            Player playerSender = (Player) sender;

            //Load Files
            FileConfiguration playerData = DataFilePlugin.getPlayerData().getConfig();
            DataFile areaChatFile = this.plugin.areachat;
            FileConfiguration areaChatData = areaChatFile.getConfig();

            //Get Strings
            String muteListHeader;
            String muteListEntry;
            String finalMsg;

            if(playerData.contains("player." + playerSender.getUniqueId().toString() + ".ignored")) {

                List<String> ignored = playerData.getStringList("player." + playerSender.getUniqueId().toString() + ".ignored");

                if(ignored.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "You haven't muted anyone.");
                    return true;
                }

                //Ensure header from file
                if(areaChatData.contains("mute-list-command-header")) {
                    muteListHeader = areaChatData.getString("mute-list-command-header");
                } else {
                    areaChatData.set("mute-list-command-header", "&aMuted Players:&r");
                    areaChatFile.save(areaChatData);
                    muteListHeader = areaChatData.getString("mute-list-command-header");
                }

                //Ensure entry from file
                if(areaChatData.contains("mute-list-command-entry")) {
                    muteListEntry = areaChatData.getString("mute-list-command-entry");
                } else {
                    areaChatData.set("mute-list-command-entry", "&d -&r %entry%");
                    areaChatFile.save(areaChatData);
                    muteListEntry = areaChatData.getString("mute-list-command-entry");
                }

                //Grab data for final message
                finalMsg = ChatColor.translateAlternateColorCodes('&', muteListHeader);
                List<String> nameList = new ArrayList<String>();
                for(String uuidString : ignored) {
                    nameList.add(DataFilePlugin.getPlayerName(plugin, UUID.fromString(uuidString)));
                }

                //Generate final message
                for(String name : nameList) {
                    finalMsg = finalMsg + "\n" + ChatColor.translateAlternateColorCodes('&', muteListEntry).replace("%entry%", name);
                }

                sender.sendMessage(finalMsg);
                return true;

            } else {
                sender.sendMessage(ChatColor.RED + "You haven't mute anyone.");
                return true;
            }

        } else {
            sender.sendMessage(ChatColor.RED + "This command cannot be run from the console.");
            return true;
        }
    }
}
