package com.github.lyokofirelyte.ElysianLite.Command.Internals;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import com.github.lyokofirelyte.ElysianLite.ElysianLite;
import com.github.lyokofirelyte.ElysianLite.Data.ELData;
import com.github.lyokofirelyte.ElysianLite.Data.ELObject;

public class CommandRegistry implements CommandExecutor {
	
	public ElysianLite main;
	
	public CommandRegistry(ElysianLite i){
		main = i;
	}
	
	public void registerCommands(Object... classes){
                
		Field f = null;
		CommandMap scm = null;
	                
		try {
			f = SimplePluginManager.class.getDeclaredField("commandMap");
			f.setAccessible(true);
			scm = (CommandMap) f.get(Bukkit.getPluginManager());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	                
		for (Object obj : classes){
			for (Method method : obj.getClass().getMethods()) {
				if (method.getAnnotation(ELCommand.class) != null){
					ELCommand anno = method.getAnnotation(ELCommand.class);
					try {
						Cmd command = new Cmd(anno.commands()[0]);
						command.setUsage(anno.help());
						command.setAliases(Arrays.asList(anno.commands()));
						command.setDescription(anno.desc());
						scm.register("el", command);
						command.setExecutor(this);
						main.commandMap.put(Arrays.asList(anno.commands()), obj);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
    
    @Override // so this is a hot mess...
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	ELObject gp;
    	
    	if (sender instanceof Player){
    		gp = main.playerData.get(((Player) sender).getUniqueId().toString());
    	} else {
    		return false;
    	}
    	
    	for (List<String> cmdList : main.commandMap.keySet()){
    		if (cmdList.contains(label)){
    			for (String command : cmdList){
    				if (command.equals(label)){
    					Object obj = main.commandMap.get(cmdList);
    					for (Method m : obj.getClass().getMethods()){
    						if (m.getAnnotation(ELCommand.class) != null && Arrays.asList(m.getAnnotation(ELCommand.class).commands()).contains(command)){
    							try {
    								ELCommand anno = m.getAnnotation(ELCommand.class);
    								if ((sender instanceof Player && (ELData.PERMS.getData((Player) sender, main).asListString().contains(anno.perm())) || sender instanceof Player == false || sender.isOp() || anno.perm().equals("el.command") || anno.perm().equals("wa.guest"))){
    									if (args.length > anno.max() || args.length < anno.min()){
    										s(sender, anno.help());
    										return true;
    									}
    									if (sender instanceof Player == false){
    										s(sender, "&4Console can't run this!");
    									} else { // epilepsy warning
    										if (m.getParameterTypes()[0].equals(Player.class)){
    											if (m.getParameterTypes()[1].equals(ELObject.class)){
    												m.invoke(obj, (Player) sender, gp, args);
    											} else {
    												m.invoke(obj, (Player) sender, args);
    											}
    										} else if (m.getParameterTypes()[0].equals(CommandSender.class)){
    											if (m.getParameterTypes()[1].equals(ELObject.class)){
    												m.invoke(obj, sender, gp, args);
    											} else {
    												m.invoke(obj, sender, args);
    											}
    										}
    									}
    								} else if (!sender.hasPermission(anno.perm())){
    									s(sender, "&4No permission!");
    								}
    							} catch (Exception e) {
    								e.printStackTrace();
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	return true;
    }
    
	public void registerAll(Object mainClassInstance, String packageMainDirectory, String jarName){
		  
        List<Class<?>> allClasses = new ArrayList<Class<?>>();
        
        try {
        
	        List<String> classNames = new ArrayList<String>();
	        ZipInputStream zip = new ZipInputStream(new FileInputStream("./plugins/" + jarName.replace(".jar", "") + ".jar"));
	        boolean look = false;
	        
	        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()){
	        	
	        	if (entry.isDirectory()){
	        		look = entry.getName().contains(packageMainDirectory);
	        	}
	        	
	            if (entry.getName().endsWith(".class") && !entry.isDirectory() && look) {
	            	
	                StringBuilder className = new StringBuilder();
	                
	                for (String part : entry.getName().split("/")) {
	                	
	                    if (className.length() != 0){
	                        className.append(".");
	                    }
	                    
	                    className.append(part);
	                    
	                    if (part.endsWith(".class")){
	                        className.setLength(className.length()-".class".length());
	                    }
	                }
	                
	                classNames.add(className.toString());
	            }
	        }
	        
	        for (String clazz : classNames){
	        	allClasses.add(Class.forName(clazz));
	        }
	        
        } catch (Exception e){
        	e.printStackTrace();
        }
        
        List<AutoRegister<?>> curr = new ArrayList<AutoRegister<?>>();
        
		for (Class<?> clazz : allClasses){
			
			Object obj = null;
			
			for (int i = 0; i < clazz.getConstructors().length; i++){
				try {
					Constructor<?> con = clazz.getConstructors()[i];
					con.setAccessible(true);
					obj = con.newInstance(mainClassInstance);
					break;
				} catch (Exception e){}
			}
			
			if (obj instanceof AutoRegister<?> && !clazz.toString().contains("\\$") && !main.clazzez.containsKey(clazz.toString())){
				main.clazzez.put(clazz.toString(), (AutoRegister<?>) obj);
				curr.add((AutoRegister<?>) obj);
			}
		}

		for (Object obj : curr){
			
			if (obj instanceof Listener){
				Bukkit.getPluginManager().registerEvents((Listener) obj, (Plugin) mainClassInstance);
			}
			
			registerCommands(obj);
		}
	}
    
    private void s(CommandSender sender, String msg){
    	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}