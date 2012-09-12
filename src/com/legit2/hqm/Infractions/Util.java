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
import org.bukkit.entity.Player;

public class Util {

	private static Infractions plugin; // obviously needed
	static Logger log = Logger.getLogger("Minecraft");
	public static int SCORECAP = Settings.getSettingInt("score_cap"); // max score
	static Calendar ca1 = Calendar.getInstance();
    static int iDay = ca1.get(Calendar.DATE);
    static int iMonth = ca1.get(Calendar.MONTH) + 1;
    static int iYear = ca1.get(Calendar.YEAR);
    static String date = iYear + "," + iMonth + "," + iDay + "," + ca1.get(Calendar.HOUR_OF_DAY) + ":" + ca1.get(Calendar.MINUTE);

	/**
	 * Add a single infraction
	 * 
	 * @param target
	 * @param infraction
	 * @param proof
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean addInfraction(String target,int level, int id, String infraction, String proof) {
		if (!Save.hasData(target, "INFRACTIONS")) {
			setInfractions(target, new HashMap<String, String>());
		}
		String readableID = Integer.toString(id);
		((HashMap<String, String>)Save.getData(target, "INFRACTIONS")).put(readableID, level + ":" + infraction + " - " + proof + " - " + date);
		return true;
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
			HashMap<String, String> original = ((HashMap<String, String>)Save.getData(target, "INFRACTIONS"));
			HashMap<String, String> toreturn = new HashMap<String, String>();
			for (String s : original.keySet()) {
					toreturn.put(s, original.get(s));
			}
			setInfractions(target, toreturn); //clean original
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
		if (amt > SCORECAP)
			amt = SCORECAP;
		Save.saveData(p, "SCORE", new Integer(amt));
	}

	public Util(Infractions i) {
		plugin = i;
	}
	/**
	 * Get a player's score.
	 * @param p
	 * @return
	 */
	public static int getScore(Player p) {
		return getScore(p.getName());
	}
	/**
	 * Get a player's score.
	 * @param p
	 * @return
	 */
	public static int getScore(String p) {
		if (Save.hasData(p, "SCORE"))
			return (Integer)Save.getData(p, "SCORE");
		return 0;
	}
	/**
	 * Check a player's score to see if they should be banned.
	 * @param p
	 */
	public static void checkScore(String p) {
		if (SCORECAP <= getScore(p)) {
			getOnlinePlayer(p).kickPlayer("You have been banned.");
			Bukkit.getPlayer(p).setBanned(true);
		} else {
			Bukkit.getPlayer(p).setBanned(false);
		}
	}
	
	public static Player getOnlinePlayer(String name) {
		return plugin.getServer().getPlayer(name);
	}
}
