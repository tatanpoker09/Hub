package com.aletheia.mc.HubPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import com.aletheia.mc.HubPlugin.commands.HubCommand;
import com.aletheia.mc.HubPlugin.commands.MsgCommand;
import com.aletheia.mc.HubPlugin.commands.MuteCmd;
import com.aletheia.mc.HubPlugin.commands.RCmd;
import com.aletheia.mc.HubPlugin.commands.SetSpawnCommand;
import com.aletheia.mc.HubPlugin.listeners.ChatListener;
import com.aletheia.mc.HubPlugin.listeners.CommandListener;
import com.aletheia.mc.HubPlugin.listeners.InventoryListener;
import com.aletheia.mc.HubPlugin.listeners.PlayerListener;
import com.aletheia.mc.HubPlugin.listeners.WorldListener;
import com.aletheia.mc.HubPlugin.utils.BossBarUtils;
import com.aletheia.mc.HubPlugin.utils.MetaUtils;
import com.aletheia.mc.HubPlugin.utils.TatanUtils;
import com.aletheia.mc.HubPlugin.utils.MySQL.MySQL;

public class HubPlugin extends JavaPlugin{
	/*TODO
	 * .-Items at hub functionality.
	 */
	private static Scoreboard board;
	private static Location hubSpawn;
	private static Inventory hubHotbar;
	private static Map<UUID, Boolean> magicClock = new HashMap<UUID, Boolean>();
	private static JavaPlugin plugin;
	private static Inventory hubInv;
	private static Map<Integer, World> worldNumber = new HashMap<Integer, World>();
	MySQL mySQL = new MySQL(this, "Aletheia-mc.com", "3306", "aletheia_HubShop", "aletheia_hub", "AletheiaMCDB1");
	private static Connection c = null;
	private static Statement s = null;
	
	@SuppressWarnings("deprecation")
	public void onEnable(){
		
		setPlugin(this);
		getCommand("msg").setExecutor(new MsgCommand());
		getCommand("r").setExecutor(new RCmd());
		getCommand("hub").setExecutor(new HubCommand());
		getCommand("setspawn").setExecutor(new SetSpawnCommand());
		getCommand("mute").setExecutor(new MuteCmd());
		loadDefaultConfig();
		loadConfig();
		loadListeners(new PlayerListener(), new InventoryListener(), new WorldListener(), new CommandListener(), new ChatListener());
		loadInventory();
		try {
			setC(mySQL.openConnection());
			setS(c.createStatement());
			s.executeUpdate("CREATE TABLE IF NOT EXISTS HubShop (PlayerUUID varchar(36) NOT NULL DEFAULT '', "
					+ " EXPLOSION_NORMAL BOOLEAN NOT NULL DEFAULT '0', "
					+ " CRIT BOOLEAN NOT NULL DEFAULT '0', "
					+ " CRIT_MAGIC BOOLEAN NOT NULL DEFAULT '0', "
					+ " SMOKE_LARGE BOOLEAN NOT NULL DEFAULT '0', "
					+ " SPELL BOOLEAN NOT NULL DEFAULT '0', "
					+ " SPELL_INSTANT BOOLEAN NOT NULL DEFAULT '0', "
					+ " SPELL_MOB BOOLEAN NOT NULL DEFAULT '0',"
					+ " NOTE BOOLEAN NOT NULL DEFAULT '0', "
					+ " PORTAL BOOLEAN NOT NULL DEFAULT '0', "
					+ " ENCHANTMENT_TABLE BOOLEAN NOT NULL DEFAULT '0', "
					+ " LAVA BOOLEAN NOT NULL DEFAULT '0', "
					+ " HEART BOOLEAN NOT NULL DEFAULT '0');");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getCommand("setspawn").setExecutor(new SetSpawnCommand());
		//TODO WAIT BossBarUtils.startBar(this);
		for(Player player : Bukkit.getOnlinePlayers()){
			PlayerListener.showPlayers(player);
		}
	}
	public void onDisable(){

	}

	private void loadListeners(Listener... listeners){
		PluginManager pm = Bukkit.getPluginManager();
		for(Listener listener : listeners){
			pm.registerEvents(listener, this);
		}
	}
	
	private void loadConfig() {
		int delay = getConfig().getInt("bar.delay");
		List<String> barMessages = getConfig().getStringList("bar.messages");
		BossBarUtils.setDelay(delay);
		BossBarUtils.setMessages(barMessages);

		String spawnCoords = getConfig().getString("hubspawn.coords");
		float yaw = getConfig().getInt("hubspawn.yaw");
		for(World world : Bukkit.getWorlds()) world.setGameRuleValue("doDaylightCycle", "false");
		setHubSpawn(TatanUtils.getLocation(spawnCoords, null, yaw));
	}

	private void loadDefaultConfig() {
		List<String> messages = new ArrayList<String>();
		messages.add(ChatColor.GREEN+"Welcome to the "+ChatColor.GOLD+"Aletheia Network");
		messages.add(ChatColor.GOLD+"Having fun?"+ChatColor.AQUA+"Try donating for an "+ChatColor.BOLD+"enhanced experience!");
		getConfig().addDefault("bar.messages", messages);
		getConfig().addDefault("bar.delay", 5);
		getConfig().addDefault("hubspawn.coords", (int)1189+","+(int)2+","+(int)75);
		getConfig().addDefault("hubspawn.yaw", "0");
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	private void loadInventory() {
		hubHotbar = Bukkit.createInventory(null, InventoryType.PLAYER);
		Material[] items = loadItems();
		String[] names = loadNames();
		for(int x = 0;x<9;x++){
			ItemStack item = new ItemStack(items[x]);
			if(item.getType()!=Material.AIR)
				MetaUtils.addName(item, names[x]);
			hubHotbar.setItem(x, item);
		}

		Inventory inventory = Bukkit.createInventory(null, 9, "Hubs");
		int x = 1;
		for(World world : Bukkit.getWorlds()){
			if(!(world.getEnvironment().equals(Environment.NETHER) || world.getEnvironment().equals(Environment.THE_END))){
				if(world.getName().startsWith("Spawn")){
					ItemStack item = MetaUtils.addName(new ItemStack(Material.ENCHANTED_BOOK), ChatColor.AQUA+"Hub #"+x);
					inventory.addItem(item);
					worldNumber.put(x, world);
				}
			}
		}
		hubInv = inventory;
	}

	private String[] loadNames() {
		String[] names = new String[9];
		names[0] = "";
		names[1] = "";
		names[2] = "";
		names[3] = ChatColor.GOLD+"Magic Clock";
		names[4] = "Pets";
		names[5] = "";
		names[6] = ChatColor.GOLD+"Money Conversion";
		names[7] = ChatColor.YELLOW+"Trails";
		names[8] = ChatColor.DARK_AQUA+"Hub List";
		return names;
	}
	private Material[] loadItems(){
		Material[] items = new Material[9];
		items[0] = Material.AIR;
		items[1] = Material.AIR;
		items[2] = Material.AIR;
		items[3] = Material.WATCH;
		items[4] = Material.AIR;
		items[5] = Material.AIR;
		items[6] = Material.GOLD_INGOT;
		items[7] = Material.BLAZE_ROD;
		items[8] = Material.COMPASS;
		return items;
	}

	public static Scoreboard getBoard() {
		return board;
	}
	
	public static void setBoard(Scoreboard board) {
		HubPlugin.board = board;
	}
	
	public static Location getHubSpawn(World world) {
		return new Location(world, hubSpawn.getX(), hubSpawn.getY(), hubSpawn.getZ());
	}
	
	public static void setHubSpawn(Location hubSpawn) {
		HubPlugin.hubSpawn = hubSpawn;
	}
	
	public static Inventory getHubHotbar() {
		return hubHotbar;
	}
	
	public static void setHubHotbar(Inventory hubHotbar) {
		HubPlugin.hubHotbar = hubHotbar;
	}
	
	public static Map<UUID, Boolean> getMagicClock() {
		return magicClock;
	}
	
	public static void setMagicClock(Map<UUID, Boolean> magicClock) {
		HubPlugin.magicClock = magicClock;
	}
	
	public static JavaPlugin getPlugin() {
		return plugin;
	}
	
	public static void setPlugin(JavaPlugin plugin) {
		HubPlugin.plugin = plugin;
	}
	
	public static Inventory getHubInv() {
		return hubInv;
	}
	
	public static void setHubInv(Inventory hubInv) {
		HubPlugin.hubInv = hubInv;
	}
	
	public static Map<Integer, World> getWorldNumber() {
		return worldNumber;
	}
	
	public static void setWorldNumber(Map<Integer, World> worldNumber) {
		HubPlugin.worldNumber = worldNumber;
	}
	public static Connection getC() {
		return c;
	}
	public static void setC(Connection c) {
		HubPlugin.c = c;
	}
	public static Statement getS() {
		return s;
	}
	public static void setS(Statement s) {
		HubPlugin.s = s;
	}
}
