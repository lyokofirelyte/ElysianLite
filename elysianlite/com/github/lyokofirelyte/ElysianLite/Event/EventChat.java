package com.github.lyokofirelyte.ElysianLite.Event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.lyokofirelyte.ElysianLite.ElysianLite;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.AutoRegister;

import lombok.Getter;

public class EventChat implements Listener, AutoRegister<EventChat> {
	
	private ElysianLite main;
	
	@Getter
	private EventChat type = this;
	
	public EventChat(ElysianLite i){
		this.main = i;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		e.setCancelled(true);
		Player p = e.getPlayer();
		Location l = p.getLocation();
		e.setMessage(ChatColor.stripColor(main.AS(e.getMessage())));
		e.setMessage(e.getMessage().replace("%c", "&6" + l.getWorld().getName() + " &f@&6 " + l.getBlockX() + "&f, &6" + l.getBlockY() + "&f, &6" + l.getBlockZ() + "&f"));
		
		boolean official = e.getMessage().startsWith("@");
		String rankColor = official || p.getGameMode().equals(GameMode.CREATIVE) ? "&b" : "&7";
		String rank = official ? "&aAdmin &b" : p.getGameMode().equals(GameMode.CREATIVE) ? "&bCreative " : "&7";
		String message = rankColor + rank + "\u1440 &7" + e.getPlayer().getDisplayName() + " &7> &f" + main.AS((official ? "&b" + e.getMessage().substring(1) : e.getMessage()));
		for (Player po : Bukkit.getOnlinePlayers()){
			po.sendMessage(main.AS(message));
		}
	}
}
