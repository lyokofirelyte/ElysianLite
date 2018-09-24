package com.github.lyokofirelyte.ElysianLite.Command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.github.lyokofirelyte.ElysianLite.ElysianLite;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.AutoRegister;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.ELCommand;
import com.github.lyokofirelyte.ElysianLite.Data.ELData;

import lombok.Getter;

public class CommandHome implements Listener, AutoRegister<CommandHome> {
	
	private ElysianLite main;
	
	@Getter
	private CommandHome type = this;
	
	public CommandHome(ElysianLite w){
		main = w;
	}

	@ELCommand(commands = {"home", "h"}, desc = "Return home command", help = "/home")
	public void onHome(String[] args, Player p){
		List<String> homes = ELData.HOMES.getData(p, main).asListString();
		String home = "";
		String possibleRemove = "";
		for (String homee : homes){
			if (homee.split(" ").length < 7){
				possibleRemove = homee;
				break;
			}
		}
		if (!possibleRemove.equals("")){
			homes.remove(possibleRemove);
			homes.add(possibleRemove + " " + "default");
			ELData.HOMES.setData(p, homes, main);
		}
		if (homes.size() > 0){
			//if (main.isCooldownFinished(p, "home")){
				if (args.length > 0){
					for (String homez : homes){
						home = homez.split(" ")[6];
						if (home.equals(args[0])){
							String[] spl = homez.split(" ");
							Location homeLoc = new Location(Bukkit.getWorld(spl[0]), toInt(spl[1]), toInt(spl[2]), toInt(spl[3]), toFloat(spl[4]), toFloat(spl[5]));
							p.teleport(homeLoc);
							//main.addCooldown(p, "home", 60 * 1000L);
							return;
						}
					}
				} else {
					for (String homez : homes){
						String[] toShow = homez.split(" ");
						main.sendMessage(p, "&b" + toShow[6] + " &a> &2" + toShow[0] + " @ " + toShow[1] + "&f,&2" + toShow[2] + "&f,&2" + toShow[3]);
					}
				}
			//} else {
				//main.cooldownMessage(p, "home");
			//}
		} else {
			main.sendMessage(p, "&c&oYou have no homes.");
		}
	}
	
	@ELCommand(commands = {"sethome"}, desc = "Set home command", help = "/sethome <name>")
	public void onSetHome(String[] args, Player p){
		Location l = p.getLocation();
		List<String> homes = ELData.HOMES.getData(p, main).asListString();
		if (homes.size() < getMaxHomes(p)){
			homes.add(l.getWorld().getName() + " " + l.getBlockX() + " " + (l.getBlockY() + 1) + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch() + " " + args[0]);
			ELData.HOMES.setData(p, homes, main);
			main.sendMessage(p, "&aNew home set here! Type &6/home " + args[0] + " &ato use it!");
		} else {
			main.sendMessage(p, "&c&oYou can only have " + getMaxHomes(p) + " homes at your rank.");
		}
	}
	
	@ELCommand(commands = {"remhome", "delhome", "removehome", "deletehome"}, desc = "Remove home command", help = "/remhome <name>")
	public void onRemHome(String[] args, Player p){
		if (args.length > 0){
			List<String> homes = ELData.HOMES.getData(p, main).asListString();
			String toRemove = "";
			for (String home : homes){
				if (home.split(" ")[6].equals(args[0])){
					toRemove = home;
					break;
				}
			}
			if (!toRemove.equals("")){
				homes.remove(toRemove);
				ELData.HOMES.setData(p, homes, main);
				main.sendMessage(p, "&aHome removed.");
			} else {
				main.sendMessage(p, "&c&oYou don't have that home.");
			}
		} else {
			main.sendMessage(p, "&c&o/remhome <name>");
		}
	}
	
	private int getMaxHomes(Player p){
		//int rank = ELData.RANK.getData(p, main).asInt() + 1;
		//return 1 + (rank / 3);
		return 1;
	}
	
	private int toInt(String i){
		return Integer.parseInt(i);
	}
	
	private float toFloat(String i){
		return Float.parseFloat(i);
	}	
}