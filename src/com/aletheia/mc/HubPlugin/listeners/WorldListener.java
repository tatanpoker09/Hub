package com.aletheia.mc.HubPlugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {
	@EventHandler
	public void onRain(WeatherChangeEvent event){
		boolean rain = event.toWeatherState();
		if(rain) event.setCancelled(true);
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(!player.hasPermission("hub.breakblocks")){
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer();
		if(!player.hasPermission("hub.placeblocks")){
			event.setCancelled(true);
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onCreatureSpawn(CreatureSpawnEvent event){
		if(!event.getSpawnReason().equals(SpawnReason.CUSTOM)) event.setCancelled(true);
	}
}
