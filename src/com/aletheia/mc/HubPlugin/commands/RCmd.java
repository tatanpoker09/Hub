package com.aletheia.mc.HubPlugin.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aletheia.mc.HubPlugin.utils.TatanUtils;

public class RCmd implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if(args.length<1){
			sender.sendMessage(ChatColor.RED+"Usage: /r <Message>");
			return false;
		}
		Player player = (Player)sender;
		if(MsgCommand.getMessages().get(player.getUniqueId())!=null){
			UUID playerUid = MsgCommand.getMessages().get(player.getUniqueId());
			String message = TatanUtils.concatenateArgs(args, 1);
			Player playerTo = Bukkit.getPlayer(playerUid);
			//If there are bugs add player, playerto to hashmap in here.
			playerTo.sendMessage(ChatColor.DARK_AQUA+"["+ChatColor.GOLD+player.getName()+ChatColor.DARK_AQUA+"-> "+ChatColor.GOLD+playerTo.getName()+ChatColor.DARK_AQUA+"]: "+ChatColor.RESET+message);
		} else {
			sender.sendMessage(ChatColor.RED+"You must first use /msg <Player> to start a conversation.");
		}
		return true;
	}
}
