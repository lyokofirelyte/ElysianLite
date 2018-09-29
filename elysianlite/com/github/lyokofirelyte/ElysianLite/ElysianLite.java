package com.github.lyokofirelyte.ElysianLite;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.github.lyokofirelyte.ElysianLite.Command.CommandHome;
import com.github.lyokofirelyte.ElysianLite.Command.CommandWorld;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.CommandRegistry;
import com.github.lyokofirelyte.ElysianLite.Data.ELData;
import com.github.lyokofirelyte.ElysianLite.Data.ELObject;

import lombok.SneakyThrows;

public class ElysianLite extends JavaPlugin implements Listener {
	public Map<String, Object> clazzez = new HashMap<>();
	public Map<String, ELObject> playerData = new HashMap<>();
	public Map<String, Integer> tasks = new HashMap<>();
	public Map<List<String>, Object> commandMap = new HashMap<>();
	
	private CommandRegistry reg;

	@Override
	public void onEnable(){
		
		Bukkit.getPluginManager().registerEvents(this, this);
		load();
		
		reg = new CommandRegistry(this);
		reg.registerAll(this, "ElysianLite", "ElysianLite-1.0.jar");
		
		for (Player p : Bukkit.getOnlinePlayers()){
			motd(p);
		}
		
		Bukkit.getLogger().log(Level.INFO, "Online. Loading extra worlds...");
		int errors = 0;
		
		for (String world : CommandWorld.worlds){
			if (new File("./" + world).exists()){
				try {
					new WorldCreator(world).environment(World.Environment.NORMAL).createWorld();
				} catch (Exception e){
					e.printStackTrace();
					errors++;
				}
			}
		}
		
		Bukkit.getLogger().log(Level.INFO, "Done. Encountered " + errors + " error(s).");
	}
	
	@EventHandler
	public void onPreProcess(PlayerCommandPreprocessEvent e){
		if (e.getMessage().startsWith("//") || e.getMessage().startsWith("///") || e.getMessage().startsWith("////")){
			if (!e.getPlayer().getWorld().getName().equals("Creative")){
				e.setCancelled(true);
				sendMessage(e.getPlayer(), "Please move to the creative world to use this command.");
				return;
			}
		}
	}

	@SneakyThrows
	private void load(){
		
		JSONParser parser = new JSONParser();
		File path = new File("./plugins/ElysianLite/players/");
		path.mkdirs();
		
		for (String file : path.list()){
	        try {
	            Object obj = parser.parse(new FileReader("./plugins/ElysianLite/players/" + file));
	            JSONObject jsonObject = (JSONObject) obj;
	            playerData.put(file.replace(".json", ""), new ELObject(jsonObject));
	        } catch (Exception e){
	        	e.printStackTrace();
	        }
		}
		
		for (Player p : Bukkit.getOnlinePlayers()){
			if (!playerData.containsKey(p.getUniqueId().toString())){
				playerData.put(p.getUniqueId().toString(), new ELObject());
			}
		}
	}
	
	@EventHandler @SneakyThrows
	public void onJoin(PlayerJoinEvent e){
		
		Player p = e.getPlayer();
		p.setInvulnerable(false);
		e.setJoinMessage(null);
		
		if (!playerData.containsKey(p.getUniqueId().toString())){
			playerData.put(p.getUniqueId().toString(), new ELObject());
		}
		
		if (ELData.DISPLAY_NAME.getData(p, this).asString().equals("none")){
			ELData.DISPLAY_NAME.setData(p, "&7" + e.getPlayer().getName(), this);
		}

		p.setDisplayName(ELData.DISPLAY_NAME.getData(e.getPlayer(), this).asString());
		p.setPlayerListName(ChatColor.stripColor((e.getPlayer().getDisplayName())));
		
		for (Player z : Bukkit.getOnlinePlayers()){
			sendMessage(z, new String[]{"&a" + p.getDisplayName() + " has arrived!"});
		}
		
		switch (p.getWorld().getName()){
			case "Creative":
				e.getPlayer().setGameMode(GameMode.CREATIVE);
				break;
			case "world": case "world_the_end": case "world_nether":
				p.setGameMode(GameMode.SURVIVAL);
				p.setAllowFlight(false);
				p.setFlying(false);
				break;
			default:
				p.setGameMode(GameMode.ADVENTURE);
				p.setAllowFlight(true);
				p.setFlying(true);
				p.setFlySpeed(10);
				break;
		}
		
		motd(p);
		
		if (ELData.IS_TELEPORTING_HOME.getData(p, this).asBool()) {
			List<String> homes = ELData.HOMES.getData(p, this).asListString();
			if (homes.size() > 0){
				String[] spl = homes.get(0).split(" ");
				Location homeLoc = new Location(Bukkit.getWorld(spl[0]), toInt(spl[1]), toInt(spl[2]), toInt(spl[3]), toFloat(spl[4]), toFloat(spl[5]));
				p.teleport(homeLoc);
				ELData.IS_TELEPORTING_HOME.setData(p, false, this);
			}
		}
	}
	
	private int toInt(String i){
		return Integer.parseInt(i);
	}
	
	private float toFloat(String i) {
		return Float.parseFloat(i);
	}
	
	@SneakyThrows
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		
		e.setQuitMessage(null);
		
		for (Player p : Bukkit.getOnlinePlayers()){
			sendMessage(p, new String[]{"&c" + e.getPlayer().getDisplayName() + " has fled!"});
			tasks.remove(p.getUniqueId().toString());
		}
		
		FileWriter file = new FileWriter("./plugins/ElysianLite/players/" + e.getPlayer().getUniqueId().toString() + ".json");
		file.write(playerData.get(e.getPlayer().getUniqueId().toString()).toJSONString());
		file.close();
	}
	
	@Override
	public void onDisable(){
		Bukkit.getLogger().log(Level.INFO, "Saving...");
		save();
		Bukkit.getLogger().log(Level.INFO, "Done.");
	}
	
	public void broadcast(String message){
		for (Player p : Bukkit.getOnlinePlayers()){
			sendMessage(p, new String[]{message});
		}
	}
	
	@SneakyThrows
	private void save(){
		
		FileWriter file = null;
		
		for (String player : playerData.keySet()){
			try {
				file = new FileWriter("./plugins/ElysianLite/players/" + player + ".json");
				file.write(playerData.get(player).toJSONString());
				file.close();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e){
		motd(e.getPlayer());
	}
	
	public void motd(Player p){
		sendMessage(p, new String[]{
			"&2Welcome! We're running &bElysian Lite v1.1. &6[ 9.26.18 ]",
			"&2Type &3/el &2for a list of commands!"
		});
	}
	
	public void broadcast(String... message){
		for (Player p : Bukkit.getOnlinePlayers()){
			sendMessage(p, message);
		}
	}
	
	public void sendMessage(Player p, String... message){
		for (String m : message){
			p.sendMessage(AS("&c\u1440 &7" + m));
		}
	}
	
	public String AS(String message){
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public Set<List<String>> getCommands(){
		return commandMap.keySet();
	}
}