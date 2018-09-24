package com.github.lyokofirelyte.ElysianLite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;

public class ELUtils {

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
	
	public static void updateDisplayBar(Player p, String msg){
		IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + AS(msg) + "\"}");
		PacketPlayOutChat chat = new PacketPlayOutChat(cbc);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(chat);
	}
	
	public static String AS(String msg){
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}