package me.crimsondawn45.areachat;

import org.bukkit.plugin.java.JavaPlugin;

import me.crimsondawn45.areachat.commands.MeCommand;
import me.crimsondawn45.areachat.commands.MsgCommand;
import me.crimsondawn45.areachat.commands.MuteCommand;
import me.crimsondawn45.areachat.commands.MuteListCommand;
import me.crimsondawn45.areachat.commands.UnMuteCommand;
import me.crimsondawn45.areachat.events.ChatEvent;
import me.crimsondawn45.datafileplugin.DataFile;

public class AreaChat extends JavaPlugin {

    public DataFile areachat;

    @Override
    public void onEnable() {

        //Initialize Things
        this.areachat = new DataFile("area_chat", this);

        //Register Event
        getServer().getPluginManager().registerEvents(new ChatEvent(this), this);

        //Register Commands
        this.getCommand("me").setExecutor(new MeCommand(this));

        MsgCommand msgCommand = new MsgCommand(this);   //Use one instance
        this.getCommand("msg").setExecutor(msgCommand);
        this.getCommand("message").setExecutor(msgCommand);
        this.getCommand("tell").setExecutor(msgCommand);
        this.getCommand("whisper").setExecutor(msgCommand);
        this.getCommand("m").setExecutor(msgCommand);
        this.getCommand("pm").setExecutor(msgCommand);
        this.getCommand("dm").setExecutor(msgCommand);

        this.getCommand("mute").setExecutor(new MuteCommand(this));
        this.getCommand("unmute").setExecutor(new UnMuteCommand(this));
        this.getCommand("mutelist").setExecutor(new MuteListCommand(this));
    }

    @Override
    public void onDisable() {

    }
}