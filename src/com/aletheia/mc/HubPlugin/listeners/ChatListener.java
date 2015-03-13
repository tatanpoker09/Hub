package com.aletheia.mc.HubPlugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.aletheia.mc.HubPlugin.commands.MuteCmd;

public class ChatListener implements Listener{
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		String message = event.getMessage();
		String wholePrefix = "";
		if(MuteCmd.isEnabled()){
			player.sendMessage(ChatColor.RED+"You have been muted.");
			event.setCancelled(true);
			return;
		}
		wholePrefix = player.getName()+": ";
		PermissionUser user = PermissionsEx.getUser(player);
		for(PermissionGroup groups : user.getParents()){
			wholePrefix = groups.getPrefix()+" "+wholePrefix;
		}
		message = wholePrefix+" "+ChatColor.RESET+message;
		if (((message.toLowerCase().contains("&1")) || (message.toLowerCase().contains("&2")) || (message.toLowerCase().contains("&3")) || 
				(message.toLowerCase().contains("&4")) || (message.toLowerCase().contains("&5")) || (message.toLowerCase().contains("&6")) || 
				(message.toLowerCase().contains("&7")) || (message.toLowerCase().contains("&8")) || (message.toLowerCase().contains("&9")) || 
				(message.toLowerCase().contains("&a")) || (message.toLowerCase().contains("&b")) || (message.toLowerCase().contains("&c")) || 
				(message.toLowerCase().contains("&d")) || (message.toLowerCase().contains("&e")) || (message.toLowerCase().contains("&f")))){
			message = ChatColor.translateAlternateColorCodes('&', message);
		}
		if (((message.toLowerCase().contains("&k")) || (message.toLowerCase().contains("&r")) || (message.toLowerCase().contains("&l")) || 
				(message.toLowerCase().contains("&m")) || (message.toLowerCase().contains("&n")) || (message.toLowerCase().contains("&o")))) {
			message = ChatColor.translateAlternateColorCodes('&', message +ChatColor.RESET);
		}
		event.setCancelled(true);
		for(Player players : player.getWorld().getPlayers()) players.sendMessage(message);
	}
}
