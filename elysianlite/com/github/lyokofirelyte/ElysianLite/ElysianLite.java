package com.github.lyokofirelyte.ElysianLite;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.github.lyokofirelyte.ElysianLite.Command.Internals.CommandRegistry;
import com.github.lyokofirelyte.ElysianLite.Data.ELData;
import com.github.lyokofirelyte.ElysianLite.Data.ELObject;

import lombok.SneakyThrows;

public class ElysianLite extends JavaPlugin implements Listener {
	private List<Object> commands = new ArrayList<>();
	private List<Object> listeners = new ArrayList<>();
	
	public Map<String, Object> clazzez = new HashMap<>();
	public Map<String, ELObject> playerData = new HashMap<>();
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
		
		Bukkit.getLogger().log(Level.INFO, "Online.");
	}
	
	@SneakyThrows
	private void registerListeners(Class<?>... clazzez){
		for (Class<?> clazz : clazzez){
			Object o = clazz.getConstructor(ElysianLite.class).newInstance(this);
			if (o instanceof Listener){
				Bukkit.getPluginManager().registerEvents((Listener) o, this);
				listeners.add(o);
			}
		}
	}
	
	@SneakyThrows
	private void registerClasses(Class<?>... clazzez){
		for (Class<?> clazz : clazzez){
			commands.add(clazz.getConstructor(ElysianLite.class).newInstance(this));
		}
		reg.registerCommands(commands);
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
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		
		Player p = e.getPlayer();
		e.setJoinMessage(null);
		
		if (!playerData.containsKey(p.getUniqueId().toString())){
			playerData.put(p.getUniqueId().toString(), new ELObject());
		}
		
		if (!ELData.DISPLAY_NAME.getData(p, this).asString().equals("none")){
			p.setDisplayName(ELData.DISPLAY_NAME.getData(p, this).asString());
			p.setPlayerListName(p.getDisplayName());
		}
		
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
	}
	
	@SneakyThrows
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		
		e.setQuitMessage(null);
		
		for (Player p : Bukkit.getOnlinePlayers()){
			sendMessage(p, new String[]{"&c" + e.getPlayer().getDisplayName() + " has fled!"});
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
			"&2Welcome! We're running &bElysian Lite v1.0.",
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
	
	public List<Object> getCommands(){
		return commands;
	}
}