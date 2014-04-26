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
import com.censoredsoftware.infractions.bukkit.legacy.data.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class VirtueUtil
{
	public static List<String> getVirtues()
	{
		File f = new File("plugins" + File.separator + "Infractions" + File.separator + "config.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		if(config == null)
		{
			InfractionsPlugin.getInst().getLogger().warning("Unable to load the config for VirtueUtil.");
			return null;
		}
		List<String> virtues;
		virtues = SettingUtil.fetchListString(config, "virtues");
		return virtues;
	}

	public static void rewardPlayer(String p, String reason)
	{
		Bukkit.broadcastMessage(ChatColor.GREEN + p + " has been rewarded for " + reason + "!");
		Bukkit.broadcastMessage(ChatColor.GREEN + "Everyone should congratulate " + p + ".");
		if(!Bukkit.getOfflinePlayer(p).isOnline())
			ServerData.put(p, "NEWVIRTUE", true);
	}
}
