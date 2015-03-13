package com.aletheia.mc.HubPlugin.utils;

import java.util.ArrayList;
import java.util.List;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BossBarUtils {
	private static int delay;
	private static List<String> messages = new ArrayList<String>();

	public static int getDelay() {
		return delay;
	}

	public static void setDelay(int delay) {
		BossBarUtils.delay = delay;
	}

	public static List<String> getMessages() {
		return messages;
	}
	
	public static void setMessages(List<String> messages) {
		BossBarUtils.messages = messages;
	}
	
	public static void startBar(JavaPlugin plugin){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int x = delay;
			int currentMessage = 0;
			@Override
			public void run() {
				if(x==-1){
					currentMessage++;
					if(currentMessage>=messages.size()) currentMessage = 0;
					x = delay;
				}
				float percent = (x*100) / delay;
				x--;
				BarAPI.setMessage(messages.get(currentMessage), percent);
			}

		}, 0L, 20L);
	}
}
