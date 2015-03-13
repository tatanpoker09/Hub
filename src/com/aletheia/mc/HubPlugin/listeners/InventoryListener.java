package com.aletheia.mc.HubPlugin.listeners;

import java.sql.SQLException;
import java.sql.Statement;

import me.josh.networkcoins.API;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.melonbrew.fe.Fe;
import org.melonbrew.fe.database.Account;

import com.aletheia.mc.HubPlugin.HubPlugin;
import com.aletheia.mc.HubPlugin.utils.ParticleEffect;

public class InventoryListener implements Listener{
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryItemMove(InventoryMoveItemEvent event){
		event.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onItemDrop(PlayerDropItemEvent event){
		event.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onItemMove(InventoryClickEvent event){
		if(event.getClickedInventory()!=null){
			if(event.getClickedInventory().getName()!=null){
				event.setCancelled(true);
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event){
		ItemStack item = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		if(item!=null){
			if(event.getClickedInventory().getName().equals("Trails")){
				for(ParticleEffect pe : ParticleEffect.values()){
					if(pe.getItem()!=null){
						if(item.hasItemMeta()){
							if(item.getType().equals(Material.PAPER)){
								if(PlayerListener.getTrails().containsKey(player.getUniqueId())){
									PlayerListener.getTrails().remove(player.getUniqueId());
									player.sendMessage(ChatColor.RED+"Removed Trail: "+pe.getDisplayName());
									break;
								}
							} else {
								if(ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(pe.getDisplayName())){
									if(!item.getItemMeta().hasLore()){
										PlayerListener.getTrails().put(player.getUniqueId(), pe);
										player.sendMessage(ChatColor.GREEN+"Selected Trail: "+pe.getDisplayName());
										break;
									} else {
										if(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).startsWith("Click to buy this for: ")){
											Fe fe = (Fe) Bukkit.getPluginManager().getPlugin("Fe");
											Account ac = fe.getAPI().getAccount(player.getName(), player.getUniqueId().toString());
											if(ac.getMoney()<pe.getPrice()){
												player.sendMessage(ChatColor.RED+"Not Enough Money");
												return;
											}
											ac.setMoney(ac.getMoney()-pe.getPrice());
											try {
												Statement statement = HubPlugin.getC().createStatement();
												String sql = "UPDATE HubShop " +
										                   "SET "+pe.toString()+" = 1 WHERE PlayerUUID = '"+player.getUniqueId()+"'";
										      statement.executeUpdate(sql);
												player.sendMessage(ChatColor.GREEN+"Bought!");
												player.closeInventory();
												PlayerListener.loadBoard(player);
											} catch (SQLException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
								} 
							}
						}
					}
				}
			} else if(event.getClickedInventory().getName().equals("Money")){
				if(item.hasItemMeta()){
					Fe fe = (Fe) Bukkit.getPluginManager().getPlugin("Fe");
					int coins = API.getCoins(player);
					Account ac = fe.getAPI().getAccount(player.getName(), player.getUniqueId().toString());
					if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN+"Trade "+ChatColor.GOLD+"200"+" Athos"+ChatColor.GREEN+" for "+ChatColor.GOLD+10+" Coins")){
						if(coins>=200){
							API.takeCoins(player, 200);
							ac.deposit(10);
							PlayerListener.loadBoard(player);
							player.sendMessage(ChatColor.GREEN+"Traded!");
						} else {
							player.sendMessage(ChatColor.RED+"Not enough Athos!");
						}
					} else if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN+"Trade "+ChatColor.GOLD+"2000"+" Athos"+ChatColor.GREEN+" for "+ChatColor.GOLD+100+" Coins")){
						if(coins>=2000){
							API.takeCoins(player, 2000);
							ac.deposit(100);
							PlayerListener.loadBoard(player);
							player.sendMessage(ChatColor.GREEN+"Traded!");
						} else {
							player.sendMessage(ChatColor.RED+"Not enough Athos!");
						}
					} else if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN+"Trade "+ChatColor.GOLD+"10"+" Coins"+ChatColor.GREEN+" for "+ChatColor.GOLD+200+" Athos")){
						if(ac.getMoney()>=10){
							ac.setMoney(ac.getMoney()-10);
							API.addCoins(player, 200);
							PlayerListener.loadBoard(player);
							player.sendMessage(ChatColor.GREEN+"Traded!");
						}
					} else if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN+"Trade "+ChatColor.GOLD+"100"+" Coins"+ChatColor.GREEN+" for "+ChatColor.GOLD+2000+" Athos")){
						if(ac.getMoney()>=100){
							ac.setMoney(ac.getMoney()-100);
							API.addCoins(player, 2000);
							PlayerListener.loadBoard(player);
							player.sendMessage(ChatColor.GREEN+"Traded!");
						}
					} else if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN+"Trade "+ChatColor.GOLD+"1000"+" Coins"+ChatColor.GREEN+" for "+ChatColor.GOLD+20000+" Athos")){
						if(ac.getMoney()>=1000){
							ac.setMoney(ac.getMoney()-1000);
							API.addCoins(player, 20000);
							PlayerListener.loadBoard(player);
							player.sendMessage(ChatColor.GREEN+"Traded!");
						}
					}else {
						if(coins>=20000){
							API.takeCoins(player, 20000);
							ac.deposit(1000);
							PlayerListener.loadBoard(player);
							player.sendMessage(ChatColor.GREEN+"Traded!");
						} else {
							player.sendMessage(ChatColor.RED+"Not enough Athos!");
						}
					}
				}
			}
		}
	}
}
