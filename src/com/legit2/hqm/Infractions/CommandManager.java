package com.legit2.hqm.Infractions;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CommandManager implements CommandExecutor, Listener {
	Infractions plugin;
	Logger log = Logger.getLogger("Minecraft");

	public CommandManager(Infractions i) {
		plugin = i;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
		Player p = null;
		if (sender instanceof Player) p = (Player)sender;
		if (p == null) {
			if (c.getName().equalsIgnoreCase("infractions")) {
				log.info("[Infractions] This is a test...");
			}
		}
		return false;
	}
}
