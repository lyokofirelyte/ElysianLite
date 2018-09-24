package com.github.lyokofirelyte.ElysianLite.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.ElysianLite.ElysianLite;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.AutoRegister;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.ELCommand;
import com.github.lyokofirelyte.ElysianLite.Data.ELData;

import lombok.Getter;

public class CommandNick implements AutoRegister<CommandNick> {
	
	private ElysianLite main;
	
	@Getter
	private CommandNick type = this;
	
	public CommandNick(ElysianLite w){
		main = w;
	}

	@ELCommand(commands = {"nick"}, help = "/nick <name>", desc = "Change your name!")
	public void onNick(String[] args, Player p){
		if (args.length > 0){
			if (args[0].toLowerCase().startsWith(p.getName().toLowerCase().substring(0, 3))){
				main.broadcast("&7" + p.getDisplayName() + " &6-> &7" + args[0]);
				p.setDisplayName(ChatColor.stripColor(main.AS(args[0])));
				p.setPlayerListName(ChatColor.stripColor(main.AS(args[0])));
				ELData.DISPLAY_NAME.setData(p, ChatColor.stripColor(main.AS(args[0])), main);
			} else {
				main.sendMessage(p, "&c&oYour nick must start with the first 3 letters of your name.");
			}
		} else {
			main.sendMessage(p, "&c&o/nick <name>");
		}
	}
}