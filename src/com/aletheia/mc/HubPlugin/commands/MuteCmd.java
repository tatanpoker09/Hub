package com.aletheia.mc.HubPlugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MuteCmd implements CommandExecutor {
	private static boolean enabled = false;
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if(enabled){
			enabled = false;
			sender.sendMessage(ChatColor.GREEN+"Players have been unmuted.");
		} else {
			enabled = true;
			sender.sendMessage(ChatColor.RED+"Players have been muted.");
		}
		return true;
	}

	public static boolean isEnabled() {
		return enabled;
	}

}
