package com.github.lyokofirelyte.ElysianLite.Command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.ElysianLite.ElysianLite;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.AutoRegister;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.ELCommand;

import lombok.Getter;

public class CommandWorld implements AutoRegister<CommandWorld> {
	
	private ElysianLite main;
	
	@Getter
	private CommandWorld type = this;
	
	public static String[] worlds = { "creative", "survival", "WAV3" };
	
	public CommandWorld(ElysianLite w){
		main = w;
	}
	
	@ELCommand(commands = {"spawn"}, help = "/spawn", desc = "Teleport to Spawn")
	public void onSpawn(Player p, String[] args){
		main.sendMessage(p, "This command isn't implemented yet.");
	}
	
	@ELCommand(commands = {"loadworld"}, help = "/loadworld <name>", desc = "Load World")
	public void onLoadWorld(Player p, String[] args){
		if (p.isOp()){
			try {
				new WorldCreator(args[0]).environment(World.Environment.NORMAL).createWorld();
				main.sendMessage(p, "That probably worked.");
			} catch (Exception e){
				main.sendMessage(p, "That failed.");
			}
		} else {
			main.sendMessage(p, "You must be OP!");
		}
	}

	@ELCommand(commands = {"world"}, help = "/world <name>", desc = "Change World")
	public void onWorld(Player p, String[] args){
		if (args.length == 0){
			main.sendMessage(p, "/world <selection>. Choose from:");
			for (String w : worlds){
				main.sendMessage(p, w);
			}
		} else {
			switch (args[0]){
				case "creative":
					if (p.getWorld().getName().equals("Creative")) {
						main.sendMessage(p, "You're already on that world.");
					} else {
						p.teleport(new Location(Bukkit.getWorld("Creative"), 100, 100, 100));
						main.broadcast("&7" + p.getDisplayName() + " &6-> &3world transfer &6-> &b" + "Creative");
					}
					break;
					
				case "survival":
					if (p.getWorld().getName().equals("world")) {
						main.sendMessage(p, "You're already on that world.");
					} else {
						p.teleport(new Location(Bukkit.getWorld("world"), 1000, 100, 1000));
						main.broadcast("&7" + p.getDisplayName() + " &6-> &3world transfer &6-> &b" + "Survival");
					}
					break;
					
				case "WAV3":
					if (p.getWorld().getName().equals("WAV3")) {
						main.sendMessage(p, "You're already on that world.");
					} else {
						p.teleport(new Location(Bukkit.getWorld("WAV3"), 1000, 100, 1000));
						main.broadcast("&7" + p.getDisplayName() + " &6-> &3world transfer &6-> &b" + "WAV3");
					}
					break;
					
				default:
					main.sendMessage(p, "That world does not exist. Yet...");
					break;
			}
		}
	}
}