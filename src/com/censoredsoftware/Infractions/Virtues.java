package com.censoredsoftware.Infractions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class Virtues
{
	static Infractions plugin;
	static Logger log = Logger.getLogger("Minecraft");

	public static List<String> getVirtues()
	{
		File f = new File("plugins" + File.separator + "Infractions" + File.separator + "config.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		if(config == null)
		{
			log.warning("[Infractions] Unable to load the config for Virtues.");
			return null;
		}
		List<String> virtues;
		virtues = Settings.fetchListString(config, "virtues");
		return virtues;
	}

	public static void rewardPlayer(String p, String reason)
	{
		Bukkit.broadcastMessage(ChatColor.GREEN + p + " has been rewarded for " + reason + "!");
		Bukkit.broadcastMessage(ChatColor.GREEN + "Everyone should congratulate " + p + ".");
		try
		{
			Util.getOnlinePlayer(p); // Check if player is online.
		}
		catch(NullPointerException e)
		{
			Save.saveData(p, "NEWVIRTUE", true);
		}
	}
}
