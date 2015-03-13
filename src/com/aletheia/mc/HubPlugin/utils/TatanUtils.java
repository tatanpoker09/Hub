package com.aletheia.mc.HubPlugin.utils;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class TatanUtils {
	public static Location getLocation(String str, World world, float yaw){
		String[] arg = str.split(",");
		double[] parsed = new double[3];
		for (int a = 0; a < 3; a++) {
			parsed[a] = Double.parseDouble(arg[a]);
		}
		Location returnedLocation = new Location (world, parsed[0]+0.5, parsed[1], parsed[2]+0.5);
		returnedLocation.setYaw(yaw);;
		return returnedLocation;
	}
	public static String concatenateArgs(String[] inputString, int iNumber){
		String mapName = null;
		int i = iNumber-1;
		while(i < inputString.length) {
			mapName += " " + inputString[i];
			i++;
		}
		mapName = mapName.substring(5);
		return mapName;
	}

	public static void copyInventory(Player player, Inventory inventory){
		player.getInventory().clear();
		for(int x = 0;x<inventory.getSize()-1;x++){
			player.getInventory().setItem(x, inventory.getItem(x));
		}
	}
	public static void organizeScoreboard(List<String> scoreboardInfo, Scoreboard board){
		Collections.reverse(scoreboardInfo);
		for(int amount = scoreboardInfo.size();amount>0;amount--){
			try{
				String name = scoreboardInfo.get(amount-1);
				Score score = board.getObjective(DisplaySlot.SIDEBAR).getScore(name);
				score.setScore(amount-1);
			} catch(IllegalArgumentException e){
				Bukkit.broadcastMessage(scoreboardInfo.get(amount-1)+" IS WAY TOO LONG");
			}
		}
	}
	public static World getLowestWorld() {
		int players = 100;
		World world = null;
		for(World worlds : Bukkit.getWorlds()){
			if(!(worlds.getEnvironment().equals(Environment.NETHER) || worlds.getEnvironment().equals(Environment.THE_END))){
				if(worlds.getName().startsWith("Spawn")){
					if(worlds.getPlayers().size()<players){
						world = worlds;
						players = world.getPlayers().size();
					}
				}
			}
		}
		return world;
	}
	public static float getDirection(float yaw) {
		float yawExact;
		if (yaw <= 45F && yaw >= -45F) {
			yawExact = 0F;
		} else if(yaw <=135F  && yaw >= 45F){
			yawExact = 90F;
		} else if(yaw <=-45F  && yaw >= -135F){
			yawExact = -90F;
		} else {
			yawExact = 180F;
		}
		return yawExact;
	}
}
