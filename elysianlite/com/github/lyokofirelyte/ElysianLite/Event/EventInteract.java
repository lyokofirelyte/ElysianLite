package com.github.lyokofirelyte.ElysianLite.Event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import com.github.lyokofirelyte.ElysianLite.ElysianLite;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.AutoRegister;

import lombok.Getter;

public class EventInteract implements Listener, AutoRegister<EventInteract> {

	@Getter
	private EventInteract type = this;
	
	private ElysianLite main;
	
	public EventInteract(ElysianLite main) {
		this.main = main;
	}
	
	@EventHandler
	public void onGetTheIllegalXp(PlayerExpChangeEvent e) {
		if (e.getPlayer().getWorld().getName().equalsIgnoreCase("Creative")) {
			e.setAmount(0);
		}
	}
}