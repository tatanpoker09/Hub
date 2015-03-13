package com.aletheia.mc.HubPlugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aletheia.mc.HubPlugin.HubPlugin;
import com.aletheia.mc.HubPlugin.utils.TatanUtils;

public class SetSpawnCommand implements CommandExecutor{
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		try{
			Player player = (Player)sender;
			Location playerLoc = player.getLocation();
			double x = playerLoc.getX();
			double y = playerLoc.getY();
			double z = playerLoc.getZ();
			float exactYaw = TatanUtils.getDirection(playerLoc.getYaw());
			HubPlugin.getPlugin().getConfig().set("hubspawn.coords", (int)x+","+(int)y+","+(int)z);
			HubPlugin.getPlugin().getConfig().set("hubspawn.yaw", exactYaw);
			HubPlugin.getPlugin().saveConfig();
			Location newHub = new Location(null, x, y, z);
			newHub.setYaw(exactYaw);
			HubPlugin.setHubSpawn(newHub);
			sender.sendMessage(ChatColor.GREEN+"The new location has been set.");
			return true;
		} catch(Exception e){
			sender.sendMessage(ChatColor.RED+"Something happened. Check console for details.");
			return false;
		}
	}
}
