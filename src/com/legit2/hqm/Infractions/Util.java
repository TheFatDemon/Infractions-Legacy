package com.legit2.hqm.Infractions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Util {

	private static Infractions plugin; // obviously needed
	static Logger log = Logger.getLogger("Minecraft");
	public static int SCORECAP = Settings.getSettingInt("score_cap"); // max
																		// score

	/**
	 * Add a single infraction
	 * 
	 * @param p
	 * @param crimename
	 * @param lengthInSeconds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean addInfraction(String p, String crimename,
			int lengthInSeconds) {
		if (!Save.hasData(p, "INFRACTIONS")) {
			setInfractions(p, new HashMap<String, Long>());
		}
		// TODO
		return true;
	}

	/**
	 * Returns the effects on a player that are still active
	 * 
	 * @param p
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, Long> getInfractions(String p) {
		if (Save.hasData(p, "INFRACTIONS")) {
			// TODO
		}
		return null;
	}

	/**
	 * Returns the infractions on a player
	 * 
	 * @param p
	 * @return
	 */
	public static ArrayList<String> getInfractionsList(String p) {
		if (Save.hasData(p, "INFRACTIONS")) {
			// TODO
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

	public static Infractions getPlugin() {
		return plugin;
	}

	/**
	 * Checks is a player has the given permission.
	 * 
	 * @param p
	 * @param pe
	 * @return
	 */
	public static boolean hasPermission(Player p, String pe) {// convenience
																// method for
																// permissions
		if (p.isOp())
			return true;
		return p.hasPermission(pe);
	}

	/**
	 * Checks if a player has the given permission or is OP.
	 * 
	 * @param p
	 * @param pe
	 * @return
	 */
	public static boolean hasPermissionOrOP(Player p, String pe) {// convenience
																	// method
																	// for
																	// permissions
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
		if (!input.startsWith("http://") || !input.startsWith("https://")) {
			input = ("http://" + input);
		}
		if (!input.endsWith("/")) {
			input = (input + "/");
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
	 * @param crimename
	 * @param lengthInSeconds
	 * @return
	 */
	public static boolean removeInfraction(String p, String crimename) {
		// TODO
		return false;
	}

	public static void setInfractions(String p, HashMap<String, Long> data) {
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
		if (amt > SCORECAP)
			amt = SCORECAP;
		Save.saveData(p, "SCORE", new Integer(amt));
	}

	public Util(Infractions i) {
		plugin = i;
	}
}
