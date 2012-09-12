package com.legit2.hqm.Infractions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Levels {

	static Infractions plugin;
	static Logger log = Logger.getLogger("Minecraft");
	static File f = new File("plugins" + File.separator + "Infractions"
			+ File.separator + "config.yml");
	static FileConfiguration config = YamlConfiguration.loadConfiguration(f);

	public static List<String> getLevel1() {
		if (config == null) {
			log.warning("[Infractions] Unable to load the config for Level 1.");
			return null;
		}
		List<String> level1 = new ArrayList<String>();
		level1 = Settings.fetchListString(config, "level_1");
		return level1;
	}

	public static List<String> getLevel2() {
		if (config == null) {
			log.warning("[Infractions] Unable to load the config for Level 2.");
			return null;
		}
		List<String> level2 = new ArrayList<String>();
		level2 = Settings.fetchListString(config, "level_2");
		return level2;
	}

	public static List<String> getLevel3() {
		if (config == null) {
			log.warning("[Infractions] Unable to load the config for Level 3.");
			return null;
		}
		List<String> level3 = new ArrayList<String>();
		level3 = Settings.fetchListString(config, "level_3");
		return level3;
	}

	public static List<String> getLevel4() {
		if (config == null) {
			log.warning("[Infractions] Unable to load the config for Level 4.");
			return null;
		}
		List<String> level4 = new ArrayList<String>();
		level4 = Settings.fetchListString(config, "level_4");
		return level4;
	}

	public static List<String> getLevel5() {
		if (config == null) {
			log.warning("[Infractions] Unable to load the config for Level 5.");
			return null;
		}
		List<String> level5 = new ArrayList<String>();
		level5 = Settings.fetchListString(config, "level_5");
		return level5;
	}

	public Levels(Infractions instance) {
		plugin = instance;
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
}