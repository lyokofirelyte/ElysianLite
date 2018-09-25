package com.github.lyokofirelyte.ElysianLite.Data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.json.simple.JSONArray;

import com.github.lyokofirelyte.ElysianLite.ElysianLite;

public enum ELData {

	HOMES("HOMES"),
	PERMS("PERMS"),
	NAME("NAME"),
	DISPLAY_NAME("DISPLAY_NAME");
	
	ELData(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setData(Player p, Object value, ElysianLite main){
		main.playerData.get(p.getUniqueId().toString()).put(name, value);
	}
	
	public ELData getData(Player p, ElysianLite main){
		return getData(p.getUniqueId().toString(), main);
	}
	
	public ELData getData(String uuid, ElysianLite main){
		if (main.playerData.get(uuid).containsKey(name)){
			a = main.playerData.get(uuid).get(getName());
		} else {
			a = "none";
		}
		
		return this;
	}
	
	public String asString(){
		return (String) a;
	}
	
	public boolean asBool(){
		try {
			return Boolean.parseBoolean("" + a);
		} catch (Exception e){
			return false;
		}
	}
	
	public int asInt(){
		try {
			return Integer.parseInt("" + a);
		} catch (Exception e){
			return 0;
		}
	}
	
	public float asFloat(){
		try {
			return Float.parseFloat("" + a);
		} catch (Exception e){
			return 0;
		}
	}
	
	public List<String> asListString(){
		try {
			if (a.equals("none")) {
				return new ArrayList<String>();
			}
			JSONArray array = (JSONArray) a;
			List<String> toReturn = new ArrayList<String>();
			for (Object o : array){
				toReturn.add(o.toString());
			}
			return toReturn;
		} catch (Exception e){
			try {
				List<String> toReturn = (List<String>) a;
				return toReturn;
			} catch (Exception eee){
				List<String> toReturn = new ArrayList<String>();
				toReturn.add((String) a);
				return toReturn;
			}
		}
	}
	
	public Object asObject(){
		return a;
	}
	
	private String name;
	private Object a;
}