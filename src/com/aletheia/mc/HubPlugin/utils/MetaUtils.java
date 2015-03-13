package com.aletheia.mc.HubPlugin.utils;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MetaUtils {
	
	public static ItemStack addName(ItemStack item, String name){
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		item.setItemMeta(itemMeta);
		return item;
	}
	public static ItemStack addLore(ItemStack item, List<String> lores){
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setLore(lores);
		item.setItemMeta(itemMeta);
		return item;
	}
	public static ItemStack addNameLore(ItemStack item, String name, List<String>lores){
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(lores);
		item.setItemMeta(itemMeta);
		return item;
	}
}
