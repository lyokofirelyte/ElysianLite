package com.github.lyokofirelyte.ElysianLite.Command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.ElysianLite.ELUtils;
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
	
	private void calculate(final Player p, final Location currentRing, final Location destRing){
		final Vector v = destRing.toVector().subtract(currentRing.toVector());
		Location pLoc = p.getLocation();
		final int destX = destRing.getBlockX();
		final int destY = destRing.getBlockY();
		final int destZ = destRing.getBlockZ();
		final boolean greaterX = pLoc.getBlockX() < destX;
		final boolean greaterZ = pLoc.getBlockZ() < destZ;
		int checkingX = greaterX ? destX - currentRing.getBlockX() : currentRing.getBlockX() - destX;
		int checkingZ = greaterZ ? destZ - currentRing.getBlockZ() : currentRing.getBlockZ() - destZ;
		
		if (checkingX > 500 || checkingZ > 500) {
			p.teleport(destRing);
			main.sendMessage(p, "You are too far away to fly, so you were teleported.");
			return;
		}
		
		ELData.IS_TELEPORTING_HOME.setData(p, true, main);
		main.sendMessage(p, "You are close enough to fly. Here we go!");
		p.setFlySpeed(0);
		p.setGameMode(GameMode.SPECTATOR);
		int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
			boolean cont1 = false;
			boolean cont2 = false;
			
			p.setVelocity(v.clone().multiply(0.01));
			
			if (greaterX){
				if (p.getLocation().getBlockX() >= destX || destX - p.getLocation().getBlockX() <= 7){
					cont1 = true;
				}
			} else {
				if (p.getLocation().getBlockX() <= destX || p.getLocation().getBlockX() - destX <= 7){
					cont1 = true;
				}
			}
			
			if (greaterZ){
				if (p.getLocation().getBlockZ() >= destZ || destZ - p.getLocation().getBlockZ() <= 7){
					cont2 = true;
				}
			} else {
				if (p.getLocation().getBlockZ() <= destZ || p.getLocation().getBlockZ() - destZ <= 7){
					cont2 = true;
				}
			}
			
			if (cont1 && cont2){
				Bukkit.getScheduler().cancelTask(main.tasks.get(p.getUniqueId().toString()));
				main.sendMessage(p, "Thank you for flying Air Elysian.");
				p.setGameMode(GameMode.SURVIVAL);
				p.teleport(new Location(p.getWorld(), destX, destY+1, destZ, p.getLocation().getYaw(), p.getLocation().getPitch()));
				p.setVelocity(new Vector(0, 0, 0));
				p.setFlySpeed(0.2f);
				ELData.IS_TELEPORTING_HOME.setData(p, false, main);
			}
		}, 0, 5);
		
		main.tasks.put(p.getUniqueId().toString(), task);
	}

	@ELCommand(commands = {"home", "h"}, desc = "Return home command", help = "/home")
	public void onHome(Player p, String[] args){
		List<String> homes = ELData.HOMES.getData(p, main).asListString();
		if (homes.size() > 0){
			String[] spl = homes.get(0).split(" ");
			Location homeLoc = new Location(Bukkit.getWorld(spl[0]), toInt(spl[1]), toInt(spl[2]), toInt(spl[3]), toFloat(spl[4]), toFloat(spl[5]));
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5));
			p.teleport(homeLoc);
			ELUtils.specialEffects(homeLoc, Material.NETHER_BRICK, main);
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
			main.sendMessage(p, "&aNew home set here! Type &6/home &ato use it!");
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