package com.github.lyokofirelyte.ElysianLite.Command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.ElysianLite.ElysianLite;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.AutoRegister;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.ELCommand;
import com.github.lyokofirelyte.ElysianLite.Data.ELData;

import lombok.Getter;

public class CommandHome implements AutoRegister<CommandHome> {
	
	private ElysianLite main;
	
	@Getter
	private CommandHome type = this;
	
	public CommandHome(ElysianLite w){
		main = w;
	}

	@ELCommand(commands = {"home", "h"}, desc = "Return home command", help = "/home")
	public void onHome(Player p, String[] args){
		List<String> homes = ELData.HOMES.getData(p, main).asListString();
		if (homes.size() > 0){
			String[] spl = homes.get(0).split(" ");
			Location homeLoc = new Location(Bukkit.getWorld(spl[0]), toInt(spl[1]), toInt(spl[2]), toInt(spl[3]), toFloat(spl[4]), toFloat(spl[5]));
			p.teleport(homeLoc);
		} else {
			main.sendMessage(p, "&c&oYou're homeless.");
		}
	}
	
	@ELCommand(commands = {"sethome"}, desc = "Set home command", help = "/sethome")
	public void onSetHome(Player p, String[] args){
		Location l = p.getLocation();
		List<String> homes = ELData.HOMES.getData(p, main).asListString();
		if (homes.size() < getMaxHomes(p)){
			homes.add(l.getWorld().getName() + " " + l.getBlockX() + " " + (l.getBlockY() + 1) + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch() + " " + "default");
			ELData.HOMES.setData(p, homes, main);
			main.sendMessage(p, "&aNew home set here! Type &6/home " + args[0] + " &ato use it!");
		} else {
			main.sendMessage(p, "&c&oYou can only have " + getMaxHomes(p) + " home.");
		}
	}
	
	@ELCommand(commands = {"remhome", "delhome", "removehome", "deletehome"}, desc = "Remove home command", help = "/remhome")
	public void onRemHome(Player p, String[] args){
		List<String> homes = ELData.HOMES.getData(p, main).asListString();
		if (homes.size() > 0){
			homes.remove(homes.get(0));
			ELData.HOMES.setData(p, homes, main);
			main.sendMessage(p, "&aHome removed.");
		} else {
			main.sendMessage(p, "You're homeless.");
		}
	}
	
	private int getMaxHomes(Player p){
		return 1;
	}
	
	private int toInt(String i){
		return Integer.parseInt(i);
	}
	
	private float toFloat(String i){
		return Float.parseFloat(i);
	}	
}