/*
 * Copyright 2014 Alexander Chauncey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.censoredsoftware.infractions.bukkit.legacy.util;

import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class LevelUtil
{
	static Logger log = InfractionsPlugin.getInst().getLogger();
	static File f = new File("plugins" + File.separator + "Infractions" + File.separator + "config.yml");
	static FileConfiguration config = YamlConfiguration.loadConfiguration(f);

	public static Integer getLevel(String levelArg)
	{
		if(getLevel1().contains(levelArg)) return 1;
		if(getLevel2().contains(levelArg)) return 2;
		if(getLevel3().contains(levelArg)) return 3;
		if(getLevel4().contains(levelArg)) return 4;
		if(getLevel5().contains(levelArg)) return 5;

		log.warning("Unable to find level for reason '" + levelArg + "'.");
		return null;
	}

	public static List<String> getLevel1()
	{
		List<String> level1;
		level1 = SettingUtil.fetchListString(config, "level_1");
		return level1;
	}

	public static List<String> getLevel2()
	{
		List<String> level2;
		level2 = SettingUtil.fetchListString(config, "level_2");
		return level2;
	}

	public static List<String> getLevel3()
	{
		List<String> level3;
		level3 = SettingUtil.fetchListString(config, "level_3");
		return level3;
	}

	public static List<String> getLevel4()
	{
		List<String> level4;
		level4 = SettingUtil.fetchListString(config, "level_4");
		return level4;
	}

	public static List<String> getLevel5()
	{
		List<String> level5;
		level5 = SettingUtil.fetchListString(config, "level_5");
		return level5;
	}

	public LevelUtil()
	{
		InfractionsPlugin.getInst().getConfig().options().copyDefaults(true);
		InfractionsPlugin.getInst().saveConfig();
	}
}
