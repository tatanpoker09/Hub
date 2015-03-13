package com.aletheia.mc.HubPlugin.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.josh.networkcoins.API;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.melonbrew.fe.Fe;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.aletheia.mc.HubPlugin.HubPlugin;
import com.aletheia.mc.HubPlugin.utils.MetaUtils;
import com.aletheia.mc.HubPlugin.utils.ParticleEffect;
import com.aletheia.mc.HubPlugin.utils.TatanUtils;

public class PlayerListener implements Listener{
	private static List<Player> onlineDevs = new ArrayList<Player>();
	private static Map<UUID, ParticleEffect> trails = new HashMap<UUID, ParticleEffect>();

	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		player.setAllowFlight(true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0), true);
		TatanUtils.copyInventory(player, HubPlugin.getHubHotbar());
		loadMagicClock(player);
		World lowestPlayers = TatanUtils.getLowestWorld();
		Location hubSpawn = HubPlugin.getHubSpawn(lowestPlayers);
		PermissionUser pexUser = PermissionsEx.getUser(player);
		if(pexUser.inGroup("Dev") || pexUser.inGroup("Mod") || pexUser.inGroup("Admin")){
			onlineDevs.add(player);
			for(Player players : Bukkit.getOnlinePlayers()){
				loadBoard(players);
			}
		}
		if(hubSpawn!=null){
			player.teleport(hubSpawn);
		} else {
			player.kickPlayer("Server is full!");
		}
		try {

			Statement statement = HubPlugin.getC().createStatement();
			statement.executeUpdate("INSERT INTO HubShop (`PlayerUUID`) VALUES ('"+player.getUniqueId()+"');");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadBoard(player);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event){
		if(event.getCause().equals(DamageCause.VOID)){
			event.getEntity().teleport(HubPlugin.getHubSpawn(TatanUtils.getLowestWorld()));
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void doubleJump(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		if(player.getAllowFlight()){
			if(event.isFlying() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				player.setFlying(false);
				player.setAllowFlight(false);
				event.setCancelled(true);
				player.setAllowFlight(false);
				player.setFlying(false);
				player.setVelocity(new Vector(player.getLocation().getX(), 1.0D, player.getLocation().getZ()));
				player.setVelocity(player.getLocation().getDirection().multiply(2.5));
				player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 0.2F, 1.0F);
				event.setCancelled(true);
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!player.getAllowFlight()) {
			boolean canFly = ((Entity)player).isOnGround();
			if (canFly) {
				player.setAllowFlight(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	protected static void loadBoard(Player player) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("scoreboard", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.RED+""+ChatColor.BOLD+"Aletheia Network");
		List<String> boardInfo = new ArrayList<String>();
		boardInfo.add(ChatColor.AQUA+""+ChatColor.BOLD+"Athos");
		int athos = API.getCoins(player);
		boardInfo.add(""+athos);
		boardInfo.add("   ");
		boardInfo.add(ChatColor.GREEN+""+ChatColor.BOLD+"Coins");
		Fe fe = (Fe) Bukkit.getPluginManager().getPlugin("Fe");
		double money = fe.getAPI().getAccount(player.getName(), player.getUniqueId().toString()).getMoney();
		boardInfo.add(money+"");
		//TODO get coins from database.
		boardInfo.add(" ");
		boardInfo.add(ChatColor.AQUA+""+ChatColor.BOLD+"Your Rank:");
		for(String groups : PermissionsEx.getUser(player).getGroupNames()){
			boardInfo.add(groups);
		}
		boardInfo.add("  ");
		boardInfo.add(ChatColor.GREEN+""+ChatColor.BOLD+"Staff Online");
		for(Player players : onlineDevs){
			boardInfo.add(players.getName());
		}
		boardInfo.add("");
		boardInfo.add(ChatColor.AQUA+""+ChatColor.BOLD+"Website");
		boardInfo.add("Aletheia-mc.com");
		TatanUtils.organizeScoreboard(boardInfo, board);
		player.setScoreboard(board);
	}

	private void loadMagicClock(Player player) {
		if(HubPlugin.getMagicClock().get(player.getUniqueId())==null){
			HubPlugin.getMagicClock().put(player.getUniqueId(), false);
		} else {
			if(HubPlugin.getMagicClock().get(player.getUniqueId())){
				hidePlayers(player);
			} else {
				showPlayers(player);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void showPlayers(Player player){
		HubPlugin.getMagicClock().put(player.getUniqueId(), false);
		for(Player player2 : Bukkit.getOnlinePlayers()){
			if(!onlineDevs.contains(player2)) player.showPlayer(player2);
		}
	}
	@SuppressWarnings("deprecation")
	public static void hidePlayers(Player player){
		HubPlugin.getMagicClock().put(player.getUniqueId(), true);
		for(Player players : Bukkit.getOnlinePlayers()){
			if(!onlineDevs.contains(players)) player.hidePlayer(players);
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			Player player = event.getPlayer();
			ItemStack itemStack = event.getItem();
			if(itemStack!=null){
				Material item = itemStack.getType();
				if(item.equals(Material.WATCH)){
					if(HubPlugin.getMagicClock().get(player.getUniqueId())){
						HubPlugin.getMagicClock().put(player.getUniqueId(), false);
						showPlayers(player);
						player.sendMessage(ChatColor.GREEN+"Players are now shown.");
					} else {
						HubPlugin.getMagicClock().put(player.getUniqueId(), true);
						hidePlayers(player);
						player.sendMessage(ChatColor.GREEN+"Players are now hidden.");
					}
				} else if(item.equals(Material.COMPASS)){
					compassFunction(player);
				} else if(item.equals(Material.BLAZE_ROD)){
					player.openInventory(loadTrailsInventory(player));
				} else if(item.equals(Material.GOLD_INGOT)){
					player.openInventory(loadMoneyConversion(player));
				} else {
					if(player.getGameMode()!=GameMode.CREATIVE)
						player.sendMessage(ChatColor.RED+"That function is still under development.");
				}
			}
		}
	}
	private Inventory loadMoneyConversion(Player player) {
		Inventory moneyConversion = Bukkit.createInventory(null, 27, "Money");
		for(int x = 0;x<3;x++){
			String number = "";
			for(int y = 0;y<x;y++){
				number+="0";
			}
			ItemStack item = MetaUtils.addName(new ItemStack(Material.GOLD_INGOT), ChatColor.GREEN+"Trade "+ChatColor.GOLD+"200"+number+" Athos"+ChatColor.GREEN+" for "+ChatColor.GOLD+10+number+" Coins");
			moneyConversion.addItem(item);
			item = MetaUtils.addName(new ItemStack(Material.GOLD_INGOT), ChatColor.GREEN+"Trade "+ChatColor.GOLD+"10"+number+" Coins"+ChatColor.GREEN+" for "+ChatColor.GOLD+200+number+" Athos");
			moneyConversion.setItem(18+x, item);
		}
		return moneyConversion;
	}

	@EventHandler
	public void onPlayerWalk(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(event.getTo().getBlock().getRelative(BlockFace.SELF).getType() == Material.STONE_PLATE){
			player.setVelocity(new Vector(player.getVelocity().getX(), 1.0D, player.getVelocity().getZ()));
			player.setVelocity(player.getLocation().getDirection().setY(0.2D).multiply(10.0D));
			player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1.0F, 1.0F);
			return;
		}
		Location from = event.getFrom();
		if(getTrails().containsKey(player.getUniqueId())){
			ParticleEffect pe = getTrails().get(player.getUniqueId());
			pe.display(0, 0, 0, 0, 3, from, 15);
		}
	}
//TODO Format this, code too ugly.
	private static Inventory loadTrailsInventory(Player player){
		Inventory trails = Bukkit.createInventory(null, 27, "Trails");
		int x = 0;
		int y = 9;
		int z = 18;
		for(ParticleEffect pe : ParticleEffect.values()){
			Set<String> loreSet = new HashSet<String>();
			if(pe!=ParticleEffect.EXPLOSION_NORMAL &&
					pe!= ParticleEffect.CRIT &&
					pe!= ParticleEffect.CRIT_MAGIC &&
					pe!= ParticleEffect.SMOKE_LARGE &&
					pe!= ParticleEffect.SPELL &&
					pe!= ParticleEffect.SPELL_INSTANT &&
					pe!= ParticleEffect.SPELL_MOB &&
					pe!= ParticleEffect.NOTE &&
					pe!= ParticleEffect.PORTAL &&
					pe!= ParticleEffect.ENCHANTMENT_TABLE &&
					pe!= ParticleEffect.LAVA &&
					pe!= ParticleEffect.HEART)	continue;
			if(pe==ParticleEffect.CRIT_MAGIC ||
					pe==ParticleEffect.SPELL_MOB ||
					pe==ParticleEffect.ENCHANTMENT_TABLE ||
					pe==ParticleEffect.SPELL_INSTANT){
				if(!player.hasPermission("hub.tier1")){
					loreSet.add(ChatColor.RED+"Upgrade to "+ChatColor.GOLD+"Metics "+ChatColor.RED+"to unlock this Trail.");
				} else {
					try {
						Statement statement = HubPlugin.getC().createStatement();
						ResultSet res = statement.executeQuery("SELECT * FROM HubShop WHERE PlayerUUID = '" + player.getUniqueId() + "';");
						if(res.next()){
							boolean bought = false;
							if(res.getString("PlayerUUID") != null) {
								bought = res.getBoolean(pe.toString());
							}
							if(!bought)
								loreSet.add(ChatColor.GREEN+"Click to buy this for: "+pe.getPrice());
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				List<String> lores = new ArrayList<String>();
				lores.addAll(loreSet);
				ItemStack item = MetaUtils.addNameLore(new ItemStack(pe.getItem()), ChatColor.DARK_PURPLE+pe.getDisplayName(), lores);
				trails.setItem(y, item);
				y++;
			} else if(pe==ParticleEffect.LAVA ||
					pe==ParticleEffect.HEART ||
					pe==ParticleEffect.SPELL ||
					pe==ParticleEffect.NOTE){
				if(!player.hasPermission("hub.tier2")){
					loreSet.add(ChatColor.RED+"Upgrade to "+ChatColor.GOLD+"Athens "+ChatColor.RED+"to unlock this Trail.");;
				} else {
					try {
						Statement statement = HubPlugin.getS();
						ResultSet res = statement.executeQuery("SELECT * FROM HubShop WHERE PlayerUUID = '" + player.getUniqueId() + "';");
						res.next();
						boolean bought = false;
						if(res.getString("PlayerUUID") != null) {
							bought = res.getBoolean(pe.getDisplayName());
						}
						if(!bought){
							loreSet.add(ChatColor.GREEN+"Click to buy this for: "+pe.getPrice());
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				List<String> lores = new ArrayList<String>();
				lores.addAll(loreSet);
				ItemStack item = MetaUtils.addNameLore(new ItemStack(pe.getItem()), ChatColor.DARK_PURPLE+pe.getDisplayName(), lores);
				trails.setItem(z, item);
				z++;
			} else {
				try {
					Statement statement = HubPlugin.getS();
					ResultSet res = statement.executeQuery("SELECT * FROM HubShop WHERE PlayerUUID = '" + player.getUniqueId() + "';");
					if(res.next()){
						boolean bought = false;
						if(res.getString("PlayerUUID") != null) {
							bought = res.getBoolean(pe.toString());
						}
						if(!bought){
							loreSet.add(ChatColor.GREEN+"Click to buy this for: "+pe.getPrice());
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<String> lores = new ArrayList<String>();
				lores.addAll(loreSet);
				ItemStack item = MetaUtils.addNameLore(new ItemStack(pe.getItem()), ChatColor.DARK_PURPLE+pe.getDisplayName(), lores);
				trails.setItem(x, item);
				x++;
			}
		}
		trails.setItem(26, MetaUtils.addName(new ItemStack(Material.PAPER), ChatColor.RED+"Remove Trail"));
		return trails;
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event){
		event.setCancelled(true);
	}

	private void compassFunction(Player player) {
		Inventory hubInv = HubPlugin.getHubInv();
		player.openInventory(hubInv);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event){
		loadBoard(event.getPlayer());
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		PermissionUser pexUser = PermissionsEx.getUser(player);
		if(pexUser.inGroup("Dev") || pexUser.inGroup("Mod") || pexUser.inGroup("Admin")){
			onlineDevs.remove(player);
			for(Player players : Bukkit.getOnlinePlayers()){
				loadBoard(players);
			}
		}
	}
	public static List<Player> getOnlineDevs() {
		return onlineDevs;
	}
	public static void setOnlineDevs(List<Player> onlineDevs) {
		PlayerListener.onlineDevs = onlineDevs;
	}

	public static Map<UUID, ParticleEffect> getTrails() {
		return trails;
	}

	public static void setTrails(Map<UUID, ParticleEffect> trails) {
		PlayerListener.trails = trails;
	}
}