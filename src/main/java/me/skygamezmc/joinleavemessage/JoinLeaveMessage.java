package me.skygamezmc.joinleavemessage;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class JoinLeaveMessage extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println(ChatColor.GOLD + "[JoinLeaveMessage] Starting..");
        this.saveDefaultConfig();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            System.out.println(ChatColor.GREEN + "[JoinLeaveMessage] PlaceholderAPI detected!");
        } else {
            System.out.println(ChatColor.RED + "[JoinLeaveMessage] PlaceholderAPI was not detected! PlaceholderAPI is REQUIRED for this plugin to work!");
            getServer().getPluginManager().disablePlugin(this);
        }
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (String joinbroadcast : getConfig().getStringList("join-message-broadcast")) {
            String joinbc = PlaceholderAPI.setPlaceholders(event.getPlayer(), joinbroadcast);
            event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', joinbc));
        }
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            for (String msg : getConfig().getStringList("join-message-client")) {
                String joinclient = PlaceholderAPI.setPlaceholders(event.getPlayer(), msg);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', joinclient));
            }
        }, 2L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (String leavebroadcast : getConfig().getStringList("leave-message-broadcast")) {
            String leavebc = PlaceholderAPI.setPlaceholders(event.getPlayer(), leavebroadcast);
            event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', leavebc));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equalsIgnoreCase("jlm")) {
            if (!sender.hasPermission("joinmessageleave.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
            }
            if (sender.hasPermission("joinmessageleave.admin")) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6JoinLeaveMessage&7] &fCommands:"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/jlm reload"));
                }
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        this.reloadConfig();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6JoinLeaveMessage&7] &fReloaded!"));
                    }
                }
            }
        }
        return false;
    }
}
