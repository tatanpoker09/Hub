package com.aletheia.mc.HubPlugin.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aletheia.mc.HubPlugin.utils.TatanUtils;

public class MsgCommand implements CommandExecutor{
	private static Map<UUID, UUID> messages = new HashMap<UUID, UUID>();
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if(args.length>1){
			Player player = (Player) sender;
			Player playerTo = Bukkit.getPlayer(args[0]);
			if(playerTo!=null){
				String message = TatanUtils.concatenateArgs(args, 2);
				messages.put(playerTo.getUniqueId(), player.getUniqueId());
				messages.put(player.getUniqueId(), playerTo.getUniqueId());
				playerTo.sendMessage(ChatColor.DARK_AQUA+"["+ChatColor.GOLD+player.getName()+ChatColor.DARK_AQUA+"-> "+ChatColor.GOLD+playerTo.getName()+ChatColor.DARK_AQUA+"]: "+ChatColor.RESET+message);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED+"That player isn't online!");
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED+"Usage: /msg <Player> <Message>");
			return false;
		}
	}
	public static Map<UUID, UUID> getMessages() {
		return messages;
	}
}
