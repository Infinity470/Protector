package com.arctic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Protector extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("protector").setExecutor(this::reloadCommand);
        saveDefaultConfig();
        getLogger().info(ChatColor.GREEN + "Protector has been enabled");
    }



    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "Protector has been disabled");
    }


    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String worldSeed = Objects.requireNonNull(player.getWorld().getSeed()).toString();

        String cleanedInput = message.replaceAll("[^0-9]", "");

        Pattern pattern = Pattern.compile("-?\\d+");
        Matcher matcher = pattern.matcher(cleanedInput);
        while (matcher.find()) {
            String num = matcher.group();
            if (num.equals(worldSeed)) {
                event.setCancelled(true);
                punishPlayer(player);
                return;
            }
        }
    }

    private void punishPlayer(Player player) {
        FileConfiguration config = getConfig();
        String Command = config.getString("command");
        if (Command != null) {
            String command = Command.replace("%player%", player.getName());
            getServer().getScheduler().runTask(this, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            });
        }
    }

    public boolean reloadCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                getConfig().getString("command");
                reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Protector config.yml reloaded successfully.");
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Usage: /protector reload");
        return false;
    }
}

