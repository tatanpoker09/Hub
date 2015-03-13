package com.aletheia.mc.HubPlugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener{
	@EventHandler
	public void onCommandUse(PlayerCommandPreprocessEvent event){
		boolean cancel = false;
		String command = event.getMessage();
		command.toLowerCase();
		if(command.startsWith("/kill")){
			cancel = true;
		}
		if(cancel){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED+"You can't use that command in the hub!");
		}
		if(command.equals("/plugins") || command.equals("/pl")){
			Player player = event.getPlayer();
			if(!player.hasPermission("plugins.see")){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED+"If you want to know how this network running, it's mainly a bunch of faith, trust and pixie dust.");
			}
		}
	}
}
