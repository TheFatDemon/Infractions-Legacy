package com.legit2.hqm.Infractions;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CommandManager implements CommandExecutor, Listener {

	static Infractions plugin;
	static Logger log = Logger.getLogger("Minecraft");

	public CommandManager(Infractions i) {
		plugin = i;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command c, String label,
			String[] args) {
		Player p = null;
		if (sender instanceof Player)
			p = (Player) sender;
		if (p == null) {
			if (c.getName().equalsIgnoreCase("infractions")) {
				log.info("[Infractions] This is a test...");
			} else if (c.getName().equalsIgnoreCase("cite")) {
				log.info("[Infractions] You must log in to use this command.");
			}
		} else {
			if (c.getName().equalsIgnoreCase("infractions")) {
				p.sendMessage("This is a test...");
			} else if (c.getName().equalsIgnoreCase("cite")) {
				if (!Util.hasPermission(p, "infractions.mod")) {
					p.sendMessage("You do not have enough permissions.");
					return true;
				}
				if (Settings.getSettingBoolean("require_proof")) {
					if ((args.length != 3)) {
						p.sendMessage("You must provide a valid URL as proof.");
						return false;
					}
					if (!Util.isValidURL(args[2])) {
						p.sendMessage("You must provide a valid URL as proof.");
						return false;
					}
				}
				if (args.length == 0 || args.length == 1) {
					p.sendMessage("Not enough arguments.");
					return false;
				}
				// Level 1
				if (Levels.getLevel1().contains(args[1])) {
					if (args.length == 3) {
						if (!Util.isValidURL(args[2])) {
							p.sendMessage("You must provide a valid URL as proof.");
							return false;
						}
						p.sendMessage("Proof URL: " + ChatColor.GOLD
								+ URLShortenUtil.convertURL(args[2]));
						p.sendMessage(ChatColor.GOLD + "Success! "
								+ ChatColor.WHITE
								+ "The level 1 infraction has been recieved.");
						return true;
					}
					p.sendMessage(ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 1 infraction has been recieved.");
					return true;
				}
				// Level 2
				if (Levels.getLevel2().contains(args[1])) {
					if (args.length == 3) {
						if (!Util.isValidURL(args[2])) {
							p.sendMessage("You must provide a valid URL as proof.");
							return false;
						}
						p.sendMessage("Proof URL: " + ChatColor.GOLD
								+ URLShortenUtil.convertURL(args[2]));
					}
					p.sendMessage(ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 2 infraction has been recieved.");
					return true;
				}
				// Level 3
				if (Levels.getLevel3().contains(args[1])) {
					if (args.length == 3) {
						if (!Util.isValidURL(args[2])) {
							p.sendMessage("You must provide a valid URL as proof.");
							return false;
						}
						p.sendMessage("Proof URL: " + ChatColor.GOLD
								+ URLShortenUtil.convertURL(args[2]));
					}
					p.sendMessage(ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 3 infraction has been recieved.");
					return true;
				}
				// Level 4
				if (Levels.getLevel4().contains(args[1])) {
					if (args.length == 3) {
						if (!Util.isValidURL(args[2])) {
							p.sendMessage("You must provide a valid URL as proof.");
							return false;
						}
						p.sendMessage("Proof URL: " + ChatColor.GOLD
								+ URLShortenUtil.convertURL(args[2]));
					}
					p.sendMessage(ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 4 infraction has been recieved.");
					return true;
				}
				// Level 5
				if (Levels.getLevel5().contains(args[1])) {
					if (args.length == 3) {
						if (!Util.isValidURL(args[2])) {
							p.sendMessage("You must provide a valid URL as proof.");
							return false;
						}
						p.sendMessage("Proof URL: " + ChatColor.GOLD
								+ URLShortenUtil.convertURL(args[2]));
					}
					p.sendMessage(ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 5 infraction has been recieved.");
					return true;
				}
				return true;
			}
		}
		return false;
	}
}
