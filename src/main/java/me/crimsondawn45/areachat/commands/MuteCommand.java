package me.crimsondawn45.areachat.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.crimsondawn45.areachat.AreaChat;
import me.crimsondawn45.datafileplugin.DataFilePlugin;
import net.md_5.bungee.api.ChatColor;

public class MuteCommand implements CommandExecutor {

    private AreaChat plugin;

    public MuteCommand(AreaChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {

            //Sender
            Player senderPlayer = (Player) sender;

            //Grab players
            Set<Player> onlinePlayers = Set.copyOf(this.plugin.getServer().getOnlinePlayers());
            Set<OfflinePlayer> offlinePlayers = new HashSet<>(Arrays.asList(this.plugin.getServer().getOfflinePlayers()));

            //Get string
            String playerName = args[0].strip();

            //Find player
            for(Player player : onlinePlayers) {

                if(DataFilePlugin.getPlayerName(player).startsWith(playerName)) {   //Check using custon name

                    if(this.alreadyMuted(senderPlayer, player.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "Player is already muted.");
                        return true;
                    }

                    this.MutePlayer(senderPlayer, player.getUniqueId());
                    sender.sendMessage(ChatColor.GREEN + "Muted player \"" + DataFilePlugin.getPlayerName(player) + "\".");
                    return true;

                } else if(player.getName().startsWith(playerName)) {    //Check using default name;

                    if(this.alreadyMuted(senderPlayer, player.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "Player is already muted.");
                        return true;
                    }

                    this.MutePlayer(senderPlayer, player.getUniqueId());
                    sender.sendMessage(ChatColor.GREEN + "Muted player \"" + player.getName() + "\".");
                    return true;
                }
            }

            for(OfflinePlayer offlinePlayer : offlinePlayers) {

                if(DataFilePlugin.getPlayerName(plugin, offlinePlayer.getUniqueId()).startsWith(playerName)) {  //Check using custon name

                    if(this.alreadyMuted(senderPlayer, offlinePlayer.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "Player is already muted.");
                        return true;
                    }

                    this.MutePlayer(senderPlayer, offlinePlayer.getUniqueId());
                    sender.sendMessage(ChatColor.GREEN + "Muted player \"" + DataFilePlugin.getPlayerName(plugin, offlinePlayer.getUniqueId()) + "\".");
                    return true;

                } else if(offlinePlayer.getName().startsWith(playerName)) { //Check using default name

                    if(this.alreadyMuted(senderPlayer, offlinePlayer.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "Player is already muted.");
                        return true;
                    }

                    this.MutePlayer(senderPlayer, offlinePlayer.getUniqueId());
                    sender.sendMessage(ChatColor.GREEN + "Muted player \"" + offlinePlayer.getName() + "\".");
                    return true;
                }
            }

            sender.sendMessage(ChatColor.RED + "Unable to locate player\"" + playerName + "\".");
            return true;
            
        } else {
            
            sender.sendMessage(ChatColor.RED + "This command cannot be run from the console.");
            return true;
        }
    }

    private boolean alreadyMuted(Player sender, UUID uuid) {
        //Grab data
        FileConfiguration playerData = DataFilePlugin.getPlayerData().getConfig();

        //Instantiate list
        List<String> ignored = new ArrayList<String>();

        //Check for existing list
        if(playerData.contains("player." + sender.getUniqueId().toString() + ".ignored")) {
            ignored.addAll(playerData.getStringList("player." + sender.getUniqueId().toString() + ".ignored"));
        }

        return ignored.contains(uuid.toString());
    }

    private void MutePlayer(Player sender, UUID uuid) {
        //Grab data
        FileConfiguration playerData = DataFilePlugin.getPlayerData().getConfig();

        //Instantiate list
        List<String> ignored = new ArrayList<String>();

        //Check for existing list
        if(playerData.contains("player." + sender.getUniqueId().toString() + ".ignored")) {
            ignored.addAll(playerData.getStringList("player." + sender.getUniqueId().toString() + ".ignored"));
        }

        //Add new entry
        ignored.add(uuid.toString());

        //Save
        playerData.set("player." + sender.getUniqueId().toString() + ".ignored", ignored);
        DataFilePlugin.getPlayerData().save(playerData);
    }
}