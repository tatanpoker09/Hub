package com.aletheia.mc.HubPlugin.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aletheia.mc.HubPlugin.HubPlugin;
import com.aletheia.mc.HubPlugin.utils.TatanUtils;

public class HubCommand implements CommandExecutor{
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		Player player = (Player)sender;
		World lowestPlayers = TatanUtils.getLowestWorld();
		Location hubSpawn = HubPlugin.getHubSpawn(lowestPlayers);
		player.teleport(hubSpawn);
		return true;
	}
}
