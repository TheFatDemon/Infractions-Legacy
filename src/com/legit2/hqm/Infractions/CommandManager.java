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
		if (p == null) { // TODO
			log.info("[Infractions] Console commands not yet complete...");
			log.info("[Infractions] For now, just use in-game commands.");
			return true;
		}
		if (c.getName().equalsIgnoreCase("infractions")) {
			if (args.length == 0) {
				Util.messageSend(p, "---------------");
				Util.messageSend(p, "INFRACTIONS HELP");
				Util.messageSend(p, "---------------");
				if (Util.hasPermissionOrOP(p, "infractions.mod")) {
					Util.messageSend(p, ChatColor.GRAY
							+ "/cite <player> <infraction> [proof-url]");
					Util.messageSend(p, ChatColor.GRAY + "/uncite <player> <key>"
							+ ChatColor.WHITE + " - Find the key with "
							+ ChatColor.YELLOW + "/history" + ChatColor.WHITE + ".");
				}
				Util.messageSend(p, ChatColor.GRAY + "/history [player]");
				Util.messageSend(p, ChatColor.GRAY + "/infractions types " + ChatColor.WHITE + "- Shows all valid infraction types.");
				Util.messageSend(p, ChatColor.AQUA + "Using the GNU "
						+ ChatColor.DARK_AQUA + "Affero" + ChatColor.AQUA
						+ " General Public License.");
				Util.messageSend(p, ChatColor.AQUA + "Read the AGPL at "
						+ ChatColor.YELLOW + "http://bit.ly/TLY1xB");
				Util.messageSend(p, ChatColor.AQUA + "Source: " + ChatColor.YELLOW
						+ "https://github.com/HmmmQuestionMark/Infractions");
				return true;
			} else if (args[0].equalsIgnoreCase("types")) {
				Util.messageSend(p, "----------------");
				Util.messageSend(p, "INFRACTIONS TYPES");
				Util.messageSend(p, "----------------");
				Util.messageSend(p, ChatColor.GREEN + "Level 1:");
				for(int i=0; i< Levels.getLevel1().size(); i++){
					Util.messageSend(p, Levels.getLevel1().get(i));
				}
				Util.messageSend(p, ChatColor.YELLOW + "Level 2:");
				for(int i=0; i< Levels.getLevel2().size(); i++){
					Util.messageSend(p, Levels.getLevel2().get(i));
		        }
				Util.messageSend(p, ChatColor.GOLD + "Level 3:");
				for(int i=0; i< Levels.getLevel3().size(); i++){
					Util.messageSend(p, Levels.getLevel3().get(i));
		        }
				Util.messageSend(p, ChatColor.RED + "Level 4:");
				for(int i=0; i< Levels.getLevel4().size(); i++){
					Util.messageSend(p, Levels.getLevel4().get(i));
		        }
				Util.messageSend(p, ChatColor.DARK_RED + "Level 5:");
				for(int i=0; i< Levels.getLevel5().size(); i++){
					Util.messageSend(p, Levels.getLevel5().get(i));
		        }
				return true;
			} else {
				Util.messageSend(p, "Too many arguments.");
				return false;
			}
		} else if (c.getName().equalsIgnoreCase("cite")) {
			if (!Util.hasPermissionOrOP(p, "infractions.mod")) {
				Util.messageSend(p, "You do not have enough permissions.");
				return true;
			}
			if (Settings.getSettingBoolean("require_proof")) {
				if ((args.length != 3)) {
					Util.messageSend(p,
							"You must provide a valid URL as proof.");
					return false;
				}
				if (!Util.isValidURL(args[2])) {
					Util.messageSend(p,
							"You must provide a valid URL as proof.");
					return false;
				}
			}
			if (args.length == 0 || args.length == 1) {
				Util.messageSend(p, "Not enough arguments.");
				return false;
			}
			
			if (Util.getInfractionsPlayer(args[0]) == null) {
				Util.messageSend(p, "This player hasn't joined yet.");
				return true;
			}
			Random generator = new Random();
			int id = generator.nextInt();
			// Level 1
			if (Levels.getLevel1().contains(args[1])) {
				if (args.length == 3) {
					if (!Util.isValidURL(args[2])) {
						Util.messageSend(p,
								"You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + ChatColor.GOLD
							+ URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 1 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 1 + Util
							.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 1,
							id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, ChatColor.GOLD + "Success! "
						+ ChatColor.WHITE
						+ "The level 1 infraction has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]),
						1 + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), 1, id,
						args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			} else if (Levels.getLevel2().contains(args[1])) {
				if (args.length == 3) {
					if (!Util.isValidURL(args[2])) {
						Util.messageSend(p,
								"You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + ChatColor.GOLD
							+ URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 2 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 2 + Util
							.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 2,
							id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, ChatColor.GOLD + "Success! "
						+ ChatColor.WHITE
						+ "The level 2 infraction has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]),
						2 + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), 2, id,
						args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			} else if (Levels.getLevel3().contains(args[1])) {
				if (args.length == 3) {
					if (!Util.isValidURL(args[2])) {
						Util.messageSend(p,
								"You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + ChatColor.GOLD
							+ URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 3 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 3 + Util
							.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 3,
							id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, ChatColor.GOLD + "Success! "
						+ ChatColor.WHITE
						+ "The level 3 infraction has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]),
						3 + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), 3, id,
						args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			} else if (Levels.getLevel4().contains(args[1])) {
				if (args.length == 3) {
					if (!Util.isValidURL(args[2])) {
						Util.messageSend(p,
								"You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + ChatColor.GOLD
							+ URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 4 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 4 + Util
							.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 4,
							id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, ChatColor.GOLD + "Success! "
						+ ChatColor.WHITE
						+ "The level 4 infraction has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]),
						4 + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), 4, id,
						args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			} else if (Levels.getLevel5().contains(args[1])) {
				if (args.length == 3) {
					if (!Util.isValidURL(args[2])) {
						Util.messageSend(p,
								"You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + ChatColor.GOLD
							+ URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, ChatColor.GOLD + "Success! "
							+ ChatColor.WHITE
							+ "The level 5 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 5 + Util
							.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 5,
							id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, ChatColor.GOLD + "Success! "
						+ ChatColor.WHITE
						+ "The level 5 infraction has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]),
						5 + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), 5, id,
						args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			} else {
				Util.messageSend(p, ChatColor.YELLOW
						+ "Not a valid infraction type.");
			}
			return true;
		} else if (c.getName().equalsIgnoreCase("uncite")) {
			if (!(args.length == 2)) {
				Util.messageSend(p, "Not enough arguments.");
				return false;
			}
			if (!Util.hasPermissionOrOP(p, "infractions.mod")) {
				Util.messageSend(p, "You do not have enough permissions.");
				return true;
			}
			HashMap<String, String> infractions = Util.getInfractions(Util
					.getInfractionsPlayer(args[0]));
			Set<String> set = infractions.keySet();
			Collection<String> coll = infractions.values();
			Iterator<String> iterKey = set.iterator();
			Iterator<String> iterValue = coll.iterator();
			while (iterKey.hasNext()) {
				Object oK = iterKey.next();
				Object oV = iterValue.next();
				String key = oK.toString();
				String value = oV.toString();
				if (key.contains(args[1])) {
					if (value.startsWith("1")) {
						Util.setScore(Util.getInfractionsPlayer(args[0]),
								Util.getScore(Util
										.getInfractionsPlayer(args[0])) - 1);
						try {
							Util.getOnlinePlayer(
									Util.getInfractionsPlayer(args[0]))
									.sendMessage(
											"Removed an infraction!");
						} catch (NullPointerException e) {
							// Do Nothing
						}
						try {
							Util.getOnlinePlayer(
									Util.getInfractionsPlayer(args[0])).setBanned(false);
						} catch (NullPointerException e) {
							Util.getOfflinePlayer(
									Util.getInfractionsPlayer(args[0])).setBanned(false);
						}
					} else if (value.startsWith("2")) {
						Util.setScore(Util.getInfractionsPlayer(args[0]),
								Util.getScore(Util
										.getInfractionsPlayer(args[0])) - 2);
						try {
							Util.getOnlinePlayer(
									Util.getInfractionsPlayer(args[0]))
									.sendMessage(
											"Removed an infraction!");
						} catch (NullPointerException e) {
							// Do Nothing
						}
						try {
							Util.getOnlinePlayer(
									Util.getInfractionsPlayer(args[0])).setBanned(false);
						} catch (NullPointerException e) {
							Util.getOfflinePlayer(
									Util.getInfractionsPlayer(args[0])).setBanned(false);
						}
					} else if (value.startsWith("3")) {
						Util.setScore(Util.getInfractionsPlayer(args[0]),
								Util.getScore(Util
										.getInfractionsPlayer(args[0])) - 3);
						try {
							Util.getOnlinePlayer(
									Util.getInfractionsPlayer(args[0]))
									.sendMessage(
											"Removed an infraction!");
						} catch (NullPointerException e) {
							// Do Nothing
						}
						try {
							Util.getOnlinePlayer(
									Util.getInfractionsPlayer(args[0])).setBanned(false);
						} catch (NullPointerException e) {
							Util.getOfflinePlayer(
									Util.getInfractionsPlayer(args[0])).setBanned(false);
						}
					} else if (value.startsWith("4")) {
						Util.setScore(Util.getInfractionsPlayer(args[0]),
								Util.getScore(Util
										.getInfractionsPlayer(args[0])) - 4);
						try {
							Util.getOnlinePlayer(
									Util.getInfractionsPlayer(args[0]))
									.sendMessage(
											"Removed an infraction!");
						} catch (NullPointerException e) {
							// Do Nothing
						}
						try {
							Util.getOnlinePlayer(
									Util.getInfractionsPlayer(args[0])).setBanned(false);
						} catch (NullPointerException e) {
							Util.getOfflinePlayer(
									Util.getInfractionsPlayer(args[0])).setBanned(false);
						}
					} else if (value.startsWith("5")) {
						Util.setScore(Util.getInfractionsPlayer(args[0]),
								Util.getScore(Util
										.getInfractionsPlayer(args[0])) - 5);
						try {
							Util.getOnlinePlayer(
									Util.getInfractionsPlayer(args[0]))
									.sendMessage(
											"Removed an infraction!");
						} catch (NullPointerException e) {
							// Do Nothing
						}
						try {
							Util.getOnlinePlayer(
									Util.getInfractionsPlayer(args[0])).setBanned(false);
						} catch (NullPointerException e) {
							Util.getOfflinePlayer(
									Util.getInfractionsPlayer(args[0])).setBanned(false);
						}
					} else {
						Util.messageSend(p, "No such infraction exists.");
						return true;
					}
				}
			}
			Util.removeInfraction(Util.getInfractionsPlayer(args[0]),
					args[1]);
			Util.messageSend(p, "Infraction removed.");
			return true;
		} else if (c.getName().equalsIgnoreCase("history")) {
			if (!(args.length == 1)) {
				Util.messageSend(
						p,
						ChatColor.WHITE
								+ "--- "
								+ ChatColor.YELLOW
								+ Util.getInfractionsPlayer(p.getName())
								+ ChatColor.WHITE
								+ " - "
								+ Util.getScore(Util
										.getInfractionsPlayer(p.getName()))
								+ "/"
								+ Util.getMaxScore(p)
								+ " ---");
				try {
					HashMap<String, String> infractions = Util
							.getInfractions(Util.getInfractionsPlayer(p.getName()));
					Set<String> set = infractions.keySet();
					Collection<String> coll = infractions.values();
					Iterator<String> iterKey = set.iterator();
					Iterator<String> iterValue = coll.iterator();
					while (iterKey.hasNext()) {
						Object oK = iterKey.next();
						Object oV = iterValue.next();
						String key = oK.toString();
						int lineKey = (40 - key.length());
						Util.messageSend(p, oV.toString().substring(2));
						if (Util.hasPermissionOrOP(p, "infractions.mod")) {
							key += " " + ChatColor.WHITE
									+ Strings.repeat("-", lineKey);
							Util.messageSend(p, "Key: " + ChatColor.GOLD + key);
						}
					}
					return true;
				} catch (NullPointerException e) {
					return true;
				}
			}
			if (Util.hasPermissionOrOP(p, "infractions.mod")) {					
				/**
				 * DISPLAY ALL CURRENT INFRACTIONS
				 */
				try {
				Util.messageSend(
						p,
						ChatColor.WHITE
								+ "--- "
								+ ChatColor.YELLOW
								+ Util.getInfractionsPlayer(args[0])
								+ ChatColor.WHITE
								+ " - "
								+ Util.getScore(Util
										.getInfractionsPlayer(args[0]))
								+ "/"
									+ Util.getMaxScore(Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])))
								+ " ---");
				} catch (NullPointerException e) {
					Util.messageSend(
							p,
							ChatColor.WHITE
									+ "--- "
									+ ChatColor.YELLOW
									+ Util.getInfractionsPlayer(args[0])
									+ ChatColor.WHITE
									+ " - "
									+ Util.getScore(Util
											.getInfractionsPlayer(args[0]))
									+ " ---");
				}
				try {
					HashMap<String, String> infractions = Util
							.getInfractions(Util.getInfractionsPlayer(args[0]));
					Set<String> set = infractions.keySet();
					Collection<String> coll = infractions.values();
					Iterator<String> iterKey = set.iterator();
					Iterator<String> iterValue = coll.iterator();
					while (iterKey.hasNext()) {
						Object oK = iterKey.next();
						Object oV = iterValue.next();
						String key = oK.toString();
						int lineKey = (40 - key.length());
						Util.messageSend(p, oV.toString().substring(2));
						if (Util.hasPermissionOrOP(p, "infractions.mod")) {
							key += " " + ChatColor.WHITE
									+ Strings.repeat("-", lineKey);
							Util.messageSend(p, "Key: " + ChatColor.GOLD + key);
						}
					}
					return true;
				} catch (NullPointerException e) {
					return true;
				}
			}
		}
		return false;
	}
}
