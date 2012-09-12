package com.legit2.hqm.Infractions;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.google.common.base.Strings;

public class CommandManager implements CommandExecutor, Listener {

	static Infractions plugin;
	static Logger log = Logger.getLogger("Minecraft");
	static Date date = new Date();

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
				log.info("[Infractions] You cannot do anything from the console yet..."); // TODO
			} else if (c.getName().equalsIgnoreCase("cite")) { // TODO
				log.info("[Infractions] You must log in to use this command."); // TODO
			} else if (c.getName().equalsIgnoreCase("uncite")) {
				log.info("[Infractions] You must log in to use this command."); // TODO
			} else if (c.getName().equalsIgnoreCase("history")) {
				log.info("[Infractions] You must log in to use this command."); // TODO
			}
			
		} else {
			if (c.getName().equalsIgnoreCase("infractions")) {
				p.sendMessage("INFRACTIONS HELP");
				p.sendMessage("----------------");
				if (Util.hasPermissionOrOP(p, "infractions.mod")) {
					p.sendMessage(ChatColor.GRAY + "/cite <player> <infraction> [proof-url]");
					p.sendMessage(ChatColor.GRAY + "/uncite <player> <key>" + ChatColor.WHITE + " - Find the key with " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + ".");
				}
				p.sendMessage(ChatColor.GRAY + "/history <player>");
				return true;
			} else if (c.getName().equalsIgnoreCase("cite")) {
				if (!Util.hasPermissionOrOP(p, "infractions.mod")) {
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
				Random generator = new Random();
				int id = generator.nextInt();
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
						Util.setScore(Util.getInfractionsPlayer(args[0]), 1+Util.getScore(Util.getInfractionsPlayer(args[0])));
						Util.addInfraction(Util.getInfractionsPlayer(args[0]), 1, id, args[1], URLShortenUtil.convertURL(args[2]));
						Util.checkScore(Util.getInfractionsPlayer(args[0]));
						Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).kickPlayer("You've been cited for " + args[1] + ".");
						return true;
					}
					p.sendMessage(ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 1 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 1+Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 1, id, args[1], "No proof.");
					Util.checkScore(Util.getInfractionsPlayer(args[0]));
					Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).kickPlayer("You've been cited for " + args[1] + ".");
					return true;
				} else if (Levels.getLevel2().contains(args[1])) {
					if (args.length == 3) {
						if (!Util.isValidURL(args[2])) {
							p.sendMessage("You must provide a valid URL as proof.");
							return false;
						}
						p.sendMessage("Proof URL: " + ChatColor.GOLD
								+ URLShortenUtil.convertURL(args[2]));
						p.sendMessage(ChatColor.GOLD + "Success! "
								+ ChatColor.WHITE
								+ "The level 2 infraction has been recieved.");
						Util.setScore(Util.getInfractionsPlayer(args[0]), 2+Util.getScore(Util.getInfractionsPlayer(args[0])));
						Util.addInfraction(Util.getInfractionsPlayer(args[0]), 2, id, args[1], URLShortenUtil.convertURL(args[2]));
						Util.checkScore(Util.getInfractionsPlayer(args[0]));
						Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).kickPlayer("You've been cited for " + args[1] + ".");
						return true;
					}
					p.sendMessage(ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 2 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 2+Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 2, id, args[1], "No proof.");
					Util.checkScore(Util.getInfractionsPlayer(args[0]));
					Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).kickPlayer("You've been cited for " + args[1] + ".");
					return true;
				} else if (Levels.getLevel3().contains(args[1])) {
					if (args.length == 3) {
						if (!Util.isValidURL(args[2])) {
							p.sendMessage("You must provide a valid URL as proof.");
							return false;
						}
						p.sendMessage("Proof URL: " + ChatColor.GOLD
								+ URLShortenUtil.convertURL(args[2]));
						p.sendMessage(ChatColor.GOLD + "Success! "
								+ ChatColor.WHITE
								+ "The level 3 infraction has been recieved.");
						Util.setScore(Util.getInfractionsPlayer(args[0]), 3+Util.getScore(Util.getInfractionsPlayer(args[0])));
						Util.addInfraction(Util.getInfractionsPlayer(args[0]), 3, id, args[1], URLShortenUtil.convertURL(args[2]));
						Util.checkScore(Util.getInfractionsPlayer(args[0]));
						Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).kickPlayer("You've been cited for " + args[1] + ".");
						return true;
					}
					p.sendMessage(ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 3 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 3+Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 3, id, args[1], "No proof.");
					Util.checkScore(Util.getInfractionsPlayer(args[0]));
					Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).kickPlayer("You've been cited for " + args[1] + ".");
					return true;
				} else if (Levels.getLevel4().contains(args[1])) {
					if (args.length == 3) {
						if (!Util.isValidURL(args[2])) {
							p.sendMessage("You must provide a valid URL as proof.");
							return false;
						}
						p.sendMessage("Proof URL: " + ChatColor.GOLD
								+ URLShortenUtil.convertURL(args[2]));
						p.sendMessage(ChatColor.GOLD + "Success! "
								+ ChatColor.WHITE
								+ "The level 4 infraction has been recieved.");
						Util.setScore(Util.getInfractionsPlayer(args[0]), 4+Util.getScore(Util.getInfractionsPlayer(args[0])));
						Util.addInfraction(Util.getInfractionsPlayer(args[0]), 4, id, args[1], URLShortenUtil.convertURL(args[2]));
						Util.checkScore(Util.getInfractionsPlayer(args[0]));
						Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).kickPlayer("You've been cited for " + args[1] + ".");
						return true;
					}
					p.sendMessage(ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 4 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 4+Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 4, id, args[1], "No proof.");
					Util.checkScore(Util.getInfractionsPlayer(args[0]));
					Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).kickPlayer("You've been cited for " + args[1] + ".");
					return true;
				} else if (Levels.getLevel5().contains(args[1])) {
					if (args.length == 3) {
						if (!Util.isValidURL(args[2])) {
							p.sendMessage("You must provide a valid URL as proof.");
							return false;
						}
						p.sendMessage("Proof URL: " + ChatColor.GOLD
								+ URLShortenUtil.convertURL(args[2]));
						p.sendMessage(ChatColor.GOLD + "Success! "
								+ ChatColor.WHITE
								+ "The level 5 infraction has been recieved.");
						Util.setScore(Util.getInfractionsPlayer(args[0]), 5+Util.getScore(Util.getInfractionsPlayer(args[0])));
						Util.addInfraction(Util.getInfractionsPlayer(args[0]), 5, id, args[1], URLShortenUtil.convertURL(args[2]));
						Util.checkScore(Util.getInfractionsPlayer(args[0]));
						Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).kickPlayer("You've been cited for " + args[1] + ".");
						return true;
					}
					p.sendMessage(ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 5 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 5+Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 5, id, args[1], "No proof.");
					Util.checkScore(Util.getInfractionsPlayer(args[0]));
					Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).kickPlayer("You've been cited for " + args[1] + ".");
					return true;
				} else {
					p.sendMessage(ChatColor.YELLOW + "Not a valid infraction type.");
				}
				return true;
			} else if (c.getName().equalsIgnoreCase("uncite")) {
				if (!(args.length == 2)) {
					p.sendMessage("Not enough arguments.");
					return false;
				}
				if (!Util.hasPermissionOrOP(p, "infractions.mod")) {
					p.sendMessage("You do not have enough permissions.");
					return true;
				}
				try {
					HashMap<String, String> infractions = Util.getInfractions(Util.getInfractionsPlayer(args[0]));
			        Set<String> set = infractions.keySet();
			        Collection<String> coll = infractions.values();
			        Iterator<String> iterKey = set.iterator();
			        Iterator<String> iterValue = coll.iterator();
			        while (iterKey.hasNext())
			        {
			            Object oK = iterKey.next();
			            Object oV = iterValue.next();
			            String key = oK.toString();
			            String value = oV.toString();
			            if (key.contains(args[1])) {
			            	if (value.startsWith("1")) {
			            		Util.setScore(Util.getInfractionsPlayer(args[0]), Util.getScore(Util.getInfractionsPlayer(args[0]))-1);
			            		Util.checkScore(Util.getInfractionsPlayer(args[0]));
			            	} else if (value.startsWith("2")) {
			            		Util.setScore(Util.getInfractionsPlayer(args[0]), Util.getScore(Util.getInfractionsPlayer(args[0]))-2);
			            		Util.checkScore(Util.getInfractionsPlayer(args[0]));
			            	} else if (value.startsWith("3")) {
			            		Util.setScore(Util.getInfractionsPlayer(args[0]), Util.getScore(Util.getInfractionsPlayer(args[0]))-3);
			            		Util.checkScore(Util.getInfractionsPlayer(args[0]));
			            	} else if (value.startsWith("4")) {
			            		Util.setScore(Util.getInfractionsPlayer(args[0]), Util.getScore(Util.getInfractionsPlayer(args[0]))-4);
			            		Util.checkScore(Util.getInfractionsPlayer(args[0]));
			            	} else if (value.startsWith("5")) {
			            		Util.setScore(Util.getInfractionsPlayer(args[0]), Util.getScore(Util.getInfractionsPlayer(args[0]))-5);
			            		Util.checkScore(Util.getInfractionsPlayer(args[0]));
			            	}
			            }
			        }
			        Util.removeInfraction(Util.getInfractionsPlayer(args[0]), args[1]);
			        p.sendMessage("Infraction removed.");
			        return true;
				} catch (NullPointerException e) {
					p.sendMessage("No such infraction exists.");
					return true;
				}
			} else if (c.getName().equalsIgnoreCase("history")) {
				if (!(args.length == 1)) {
					p.sendMessage("Not enough arguments.");
					return false;
				}
				if (Util.hasPermissionOrOP(p, "infractions.mod") || Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])).equals(p)) {
					/**
					 *  DISPLAY ALL CURRENT INFRACTIONS
					 */
					p.sendMessage(ChatColor.WHITE+ "--- " + ChatColor.YELLOW + Util.getInfractionsPlayer(args[0]) + ChatColor.WHITE + " - " + Util.getScore(Util.getInfractionsPlayer(args[0])) + " ---");
					try {
						HashMap<String, String> infractions = Util.getInfractions(Util.getInfractionsPlayer(args[0]));
				        Set<String> set = infractions.keySet();
				        Collection<String> coll = infractions.values();
				        Iterator<String> iterKey = set.iterator();
				        Iterator<String> iterValue = coll.iterator();
				        while (iterKey.hasNext())
				        {
				            Object oK = iterKey.next();
				            Object oV = iterValue.next();
				            String key = oK.toString();
				            int lineKey = (40 - key.length());
				            p.sendMessage(oV.toString().substring(2));
				            if (Util.hasPermissionOrOP(p, "infractions.mod")) {
					            key += " " + ChatColor.WHITE + Strings.repeat("-", lineKey);
					            p.sendMessage("Key: " + ChatColor.GOLD + key);
				            }
				        }
				        return true;
					} catch (NullPointerException e) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
