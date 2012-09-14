package com.legit2.hqm.Infractions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Util {

	private static Infractions plugin; // obviously needed
	static Logger log = Logger.getLogger("Minecraft");
	static Calendar ca1 = Calendar.getInstance();
	static int iDay = ca1.get(Calendar.DATE);
	static int iMonth = ca1.get(Calendar.MONTH) + 1;
	static int iYear = ca1.get(Calendar.YEAR);
	static String date = iYear + "," + iMonth + "," + iDay + ","
			+ ca1.get(Calendar.HOUR_OF_DAY) + ":" + ca1.get(Calendar.MINUTE);

	/**
	 * Add a single infraction
	 * 
	 * @param target
	 * @param infraction
	 * @param proof
	 * @return
	 */
	
	public static int getMaxScore(Player p) {
		int maxScore = Settings.getSettingInt("ban_at_score");
		if (maxScore < 1)
			maxScore = 1;
		else if (maxScore > 20)
			maxScore = 20;
		if (p.hasPermission("infractions.maxscore.1")) {
			maxScore = 1;
		} else if (p.hasPermission("infractions.maxscore.2")) {
			maxScore = 2;
		} else if (p.hasPermission("infractions.maxscore.3")) {
			maxScore = 3;
		} else if (p.hasPermission("infractions.maxscore.4")) {
			maxScore = 4;
		} else if (p.hasPermission("infractions.maxscore.5")) {
			maxScore = 5;
		} else if (p.hasPermission("infractions.maxscore.6")) {
			maxScore = 6;
		} else if (p.hasPermission("infractions.maxscore.7")) {
			maxScore = 7;
		} else if (p.hasPermission("infractions.maxscore.8")) {
			maxScore = 8;
		} else if (p.hasPermission("infractions.maxscore.9")) {
			maxScore = 9;
		} else if (p.hasPermission("infractions.maxscore.10")) {
			maxScore = 10;
		} else if (p.hasPermission("infractions.maxscore.11")) {
			maxScore = 11;
		} else if (p.hasPermission("infractions.maxscore.12")) {
			maxScore = 12;
		} else if (p.hasPermission("infractions.maxscore.13")) {
			maxScore = 13;
		} else if (p.hasPermission("infractions.maxscore.14")) {
			maxScore = 14;
		} else if (p.hasPermission("infractions.maxscore.15")) {
			maxScore = 15;
		} else if (p.hasPermission("infractions.maxscore.16")) {
			maxScore = 16;
		} else if (p.hasPermission("infractions.maxscore.17")) {
			maxScore = 17;
		} else if (p.hasPermission("infractions.maxscore.18")) {
			maxScore = 18;
		} else if (p.hasPermission("infractions.maxscore.19")) {
			maxScore = 19;
		} else if (p.hasPermission("infractions.maxscore.20")) {
			maxScore = 20;
		}
		log.info("[Infractions] " + p.getName() + " has a max score of " + maxScore + "."); // TEMP
		log.info("[Infractions] " + p.hasPermission("infractions.maxscore.1")); // TEMP
		log.info("[Infractions] " + p.hasPermission("infractions.maxscore.2")); // TEMP
		return maxScore;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean addInfraction(String target, int level, int id,
			String infraction, String proof) {
		if (!Save.hasData(target, "INFRACTIONS")) {
			setInfractions(target, new HashMap<String, String>());
		}
		String readableID = Integer.toString(id);
		((HashMap<String, String>) Save.getData(target, "INFRACTIONS")).put(
				readableID, level + ":" + infraction + " - " + proof + " - "
						+ date);
		return true;
	}

	/**
	 * Check a player's score to see if they should be banned.
	 * 
	 * @param p
	 */
	
	public static void checkScore(Player p) {
		if (getMaxScore(p) <= getScore(p)) {
			p.kickPlayer("You have been banned.");
			try {
				p.setBanned(true);
			} catch (NullPointerException e) {
				log.info("[Infractions] Unable to set " + p.toString() + " to banned.");
			}
		} else {
			try {
				p.setBanned(false);
			} catch (NullPointerException e) {
				log.info("[Infractions] Unable to set " + p.toString() + " to unbanned.");
			}
		}
	}

	/**
	 * Returns the effects on a player that are still active
	 * 
	 * @param p
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> getInfractions(String target) {
		if (Save.hasData(target, "INFRACTIONS")) {
			HashMap<String, String> original = ((HashMap<String, String>) Save
					.getData(target, "INFRACTIONS"));
			HashMap<String, String> toreturn = new HashMap<String, String>();
			for (String s : original.keySet()) {
				toreturn.put(s, original.get(s));
			}
			setInfractions(target, toreturn); // clean original
			return toreturn;
		}
		return null;
	}

	/**
	 * Returns the infractions a player has
	 * 
	 * @param p
	 * @return
	 */
	public static ArrayList<String> getInfractionsList(String target) {
		if (Save.hasData(target, "INFRACTIONS")) {
			HashMap<String, String> original = getInfractions(target);
			ArrayList<String> toreturn = new ArrayList<String>();
			for (String s : original.keySet()) {
				toreturn.add(s);
			}
			return toreturn;
		}
		return null;
	}

	public static String getInfractionsPlayer(String name) {
		String found = null;
		String lowerName = name.toLowerCase();
		int delta = Integer.MAX_VALUE;
		for (String playername : Save.getCompleteData().keySet()) {
			if (playername.toLowerCase().startsWith(lowerName)) {
				int curDelta = playername.length() - lowerName.length();
				if (curDelta < delta) {
					found = playername;
					delta = curDelta;
				}
				if (curDelta == 0)
					break;
			}
		}
		return found;
	}

	public static Player getOnlinePlayer(String name) {
		return plugin.getServer().getPlayer(name);
	}
	
	public static OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(name);
		return target;
	}

	public static Infractions getPlugin() {
		return plugin;
	}

	/**
	 * Get a player's score.
	 * 
	 * @param p
	 * @return
	 */
	public static int getScore(Player p) {
		return getScore(p.getName());
	}

	/**
	 * Get a player's score.
	 * 
	 * @param p
	 * @return
	 */
	public static int getScore(String p) {
		if (Save.hasData(p, "SCORE"))
			return (Integer) Save.getData(p, "SCORE");
		return 0;
	}

	/**
	 * Checks is a player has the given permission.
	 * 
	 * @param p
	 * @param pe
	 * @return
	 */
	public static boolean hasPermission(Player p, String pe) { // convenience
																// method for
																// permissions
		return p.hasPermission(pe);
	}

	/**
	 * Checks if a player has the given permission or is OP.
	 * 
	 * @param p
	 * @param pe
	 * @return
	 */
	public static boolean hasPermissionOrOP(Player p, String pe) { // convenience
																	// method
																	// for
																	// permissions
		if (p == null)
			return true;
		if (p.isOp())
			return true;
		return p.hasPermission(pe);
	}

	/**
	 * Checks if a URL is valid and can be accessed.
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isValidURL(String input) {
		if (!input.startsWith("http://") && !input.startsWith("https://")) {
			input = ("http://" + input);
		}
		try {
			URI uri = new URI(input);
			URL url = uri.toURL();
			java.net.URLConnection conn = url.openConnection();
			conn.connect();
			return true;
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (URISyntaxException e) {
			return false;
		}
	}

	public static void messageSend(Player p, String str) {
		if (p == null) {
			str = "[Infractions] " + str;
			log.info(str);
		} else {
			p.sendMessage(str);
		}
	}

	/**
	 * Reloads Infractions.
	 */
	public static void reloadDemigods() {
		Bukkit.getServer().getPluginManager().disablePlugin(plugin);
		Bukkit.getServer().getPluginManager().enablePlugin(plugin);
	}

	/**
	 * Remove a single infraction
	 * 
	 * @param p
	 * @param id
	 * @return
	 */
	public static boolean removeInfraction(String target, String givenID) {
		for (String readableID : Util.getInfractionsList(target)) {
			if (readableID.equals(givenID))
				return (Util.getInfractions(target).remove(readableID) == null);
		}
		return false;
	}

	public static void setInfractions(String p, HashMap<String, String> data) {
		Save.saveData(p, "INFRACTIONS", data);
	}

	/**
	 * Set a player's score.
	 * 
	 * @param p
	 * @param amt
	 */
	public static void setScore(Player p, int amt) {
		setScore(p.getName(), amt);
	}

	/**
	 * Set a player's score.
	 * 
	 * @param p
	 * @param amt
	 */
	public static void setScore(String p, int amt) {
		Save.saveData(p, "SCORE", new Integer(amt));
	}

	public Util(Infractions i) {
		plugin = i;
	}
}
