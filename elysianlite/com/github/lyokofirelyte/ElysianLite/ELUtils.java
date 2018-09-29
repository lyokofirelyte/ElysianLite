package com.github.lyokofirelyte.ElysianLite;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class ELUtils {
	
	public static void specialEffects(Location ll, Material m, ElysianLite main){
		
		final List<Location> circleblocks = circle(ll, 5, 2, true, false, 0);
		
		for (Location l : circleblocks){
			 l.getWorld().playEffect(l, Effect.SMOKE, 0);
			 l.getWorld().playEffect(l, Effect.ENDER_SIGNAL, 0);
		}
		
		deployFirework(ll, Color.AQUA, Type.BALL_LARGE, true, true);
	}
	
	public static void deployFirework(Location l, Color color, Type type, boolean trail, boolean flicker){
		
        Firework fw = (Firework) l.getWorld().spawn(l, Firework.class);
        FireworkEffect effect = FireworkEffect.builder().trail(trail).flicker(flicker).withColor(color).with(type).build();
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.clearEffects();
        fwm.addEffect(effect);
       
        fw.setFireworkMeta(fwm);
	}
	
    public static List<Location> circle (Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx +r; x++)
            for (int z = cz - r; z <= cz +r; z++)
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r*r && !(hollow && dist < (r-1)*(r-1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                        }
                    }
     
        return circleblocks;
    }

	public static Location stringToLoc(String s){
		String[] split = s.split(" ");
		if (split.length == 4){
			return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
		} else {
			return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
		}
	}
	
	public static String locToString(Location l){
		return new String(
			l.getWorld().getName() + " " +
			l.getBlockX() + " " +
			l.getBlockY() + " " + 
			l.getBlockZ() + " " + 
			l.getYaw() + " " +
			l.getPitch()
		);
	}
	
	public static String locToString(Location l, int plusX, int plusY, int plusZ){
		return new String(
			l.getWorld().getName() + " " +
			(l.getBlockX()+plusX) + " " +
			(l.getBlockY()+plusY) + " " + 
			(l.getBlockZ()+plusZ) + " " + 
			l.getYaw() + " " +
			l.getPitch()
		);
	}
	
	public static String AS(String msg){
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}