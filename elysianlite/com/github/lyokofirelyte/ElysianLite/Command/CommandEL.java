package com.github.lyokofirelyte.ElysianLite.Command;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.github.lyokofirelyte.ElysianLite.ElysianLite;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.AutoRegister;
import com.github.lyokofirelyte.ElysianLite.Command.Internals.ELCommand;

import lombok.Getter;

public class CommandEL implements Listener, AutoRegister<CommandEL> {
	
	private ElysianLite main;
	
	@Getter
	private CommandEL type = this;
	
	public CommandEL(ElysianLite w){
		main = w;
	}

	@ELCommand(commands = {"el", "help", "elysian", "elysianlite"}, perm = "el.command", help = "/el")
	public void onEly(Player p, String[] args){
		main.sendMessage(p, "&6Showing all commands&f:");
		main.sendMessage(p, "&f-------------------------");
		for (List<String> oa : main.getCommands()){
			Object o = main.commandMap.get(oa);
			for (Method m : o.getClass().getMethods()){
				if (m.getAnnotation(ELCommand.class) != null){
					ELCommand c = m.getAnnotation(ELCommand.class);
					main.sendMessage(p, "&a" + c.commands()[0] + "&f: &e" + c.desc());
				}
			}
		}
	}
}