package com.censoredsoftware.infractions.util;

import com.censoredsoftware.infractions.Infractions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class LevelUtil
{

	static Infractions plugin;
	static Logger log = Logger.getLogger("Minecraft");
	static File f = new File("plugins" + File.separator + "Infractions" + File.separator + "config.yml");
	static FileConfiguration config = YamlConfiguration.loadConfiguration(f);

	public static List<String> getLevel1()
	{
		if(config == null)
		{
			log.warning("[Infractions] Unable to load the config for Level 1.");
			return null;
		}
		List<String> level1;
		level1 = SettingUtil.fetchListString(config, "level_1");
		return level1;
	}

	public static List<String> getLevel2()
	{
		if(config == null)
		{
			log.warning("[Infractions] Unable to load the config for Level 2.");
			return null;
		}
		List<String> level2;
		level2 = SettingUtil.fetchListString(config, "level_2");
		return level2;
	}

	public static List<String> getLevel3()
	{
		if(config == null)
		{
			log.warning("[Infractions] Unable to load the config for Level 3.");
			return null;
		}
		List<String> level3;
		level3 = SettingUtil.fetchListString(config, "level_3");
		return level3;
	}

	public static List<String> getLevel4()
	{
		if(config == null)
		{
			log.warning("[Infractions] Unable to load the config for Level 4.");
			return null;
		}
		List<String> level4;
		level4 = SettingUtil.fetchListString(config, "level_4");
		return level4;
	}

	public static List<String> getLevel5()
	{
		if(config == null)
		{
			log.warning("[Infractions] Unable to load the config for Level 5.");
			return null;
		}
		List<String> level5;
		level5 = SettingUtil.fetchListString(config, "level_5");
		return level5;
	}

	public LevelUtil(Infractions instance)
	{
		plugin = instance;
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
}
