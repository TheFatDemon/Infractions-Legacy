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

package com.censoredsoftware.infractions.bukkit.legacy;

import com.censoredsoftware.infractions.bukkit.Infractions;
import com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyData;
import com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyDatabase;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataManager;
import com.censoredsoftware.infractions.bukkit.legacy.util.MiscUtil;
import com.censoredsoftware.infractions.bukkit.legacy.util.SettingUtil;
import com.censoredsoftware.infractions.bukkit.origin.Origin;
import com.censoredsoftware.infractions.bukkit.origin.OriginType;
import com.censoredsoftware.library.mcidprovider.McIdProvider;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import java.util.Map;
import java.util.UUID;

public class InfractionsPlugin extends JavaPlugin
{
	static InfractionsPlugin inst;

	private static ImmutableMap<Integer, String> CHAT_SCORES;

	public static InfractionsPlugin getInst()
	{
		return inst;
	}

	private void initializeThreads()
	{
		int startdelay = (int) (SettingUtil.getSettingDouble("start_delay_seconds") * 20);
		int savefrequency = SettingUtil.getSettingInt("save_interval_seconds") * 20;
		if(startdelay <= 0) startdelay = 1;
		if(savefrequency <= 0) savefrequency = 300;
		// data
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				DataManager.saveAllData();
			}
		}, startdelay, savefrequency);
	}

	public void loadListeners()
	{
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new PlayerListener(), this);
	}

	public void loadCommands()
	{
		CommandHandler ce = new CommandHandler();
		// info
		getCommand("infractions").setExecutor(ce);
		getCommand("reasons").setExecutor(ce);
		getCommand("history").setExecutor(ce);
		getCommand("history").setTabCompleter(ce);
		// actions
		getCommand("cite").setExecutor(ce);
		getCommand("cite").setTabCompleter(ce);
		getCommand("uncite").setExecutor(ce);
		getCommand("uncite").setTabCompleter(ce);
		getCommand("clearhistory").setExecutor(ce);
	}

	public void loadMetrics()
	{
		try
		{
			(new MetricsLite(this)).start();
		}
		catch(Exception ignored)
		{
		}
	}

	@Override
	public void onDisable()
	{
		DataManager.saveAllData();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		message("disabled");
	}

	@Override
	public void onEnable()
	{
		inst = this;

		// Config
		getConfig().options().copyDefaults(true);
		saveConfig();

		// Setup database
		Infractions.setDatabase(new LegacyDatabase());
		Infractions.setDefaultOrigin(new Origin(Bukkit.getServerName(), Bukkit.getServerName(), OriginType.SERVER));

		getLogger().info("Initializing.");
		DataManager.initAllData();
		LegacyData.syncConvert();
		loadCommands();
		loadListeners();
		loadMetrics();
		initializeThreads();

		Map<Integer, String> scores = Maps.newHashMap();
		for(int i = 1; i < 6; i++)
			scores.put(i, ChatColor.translateAlternateColorCodes('&', SettingUtil.getSettingString("chat_score_" + i)));
		CHAT_SCORES = ImmutableMap.copyOf(scores);

		if(SettingUtil.getSettingBoolean("update"))
			new Updater(this, 44721, getFile(), Updater.UpdateType.DEFAULT, true);

		message("enabled");
	}

	/**
	 * API
	 */
	public static String getLevelForChat(Player player)
	{
		if(player.hasPermission("infractions.ignore")) return SettingUtil.getSettingString("chat_score_ignore");
		return getLevelForChat(McIdProvider.getId(player.getName()));
	}

	public static String getLevelForChat(String playerName)
	{
		UUID id = McIdProvider.getId(playerName);
		if(id != null) return getLevelForChat(id);
		return "";
	}

	public static String getLevelForChat(UUID mojangId)
	{
		Integer maxScore = MiscUtil.getMaxScore(mojangId);
		if(maxScore == null) return "";
		double scoreRange = maxScore / 5.0;
		int chatScore = (int) ((MiscUtil.getScore(mojangId) != 0 ? MiscUtil.getScore(mojangId) : 1) / scoreRange);
		if(chatScore < 1) chatScore = 1;
		if(chatScore > 5) chatScore = 5;
		return CHAT_SCORES.get(chatScore);
	}

	private void message(String status)
	{
		getLogger().info("      _      ___             __  _");
		getLogger().info("     (_)__  / _/______ _____/ /_(_)__  ___  ___");
		getLogger().info("    / / _ \\/ _/ __/ _ `/ __/ __/ / _ \\/ _ \\(_-<");
		getLogger().info("   /_/_//_/_//_/  \\_,_/\\__/\\__/_/\\___/_//_/___/");
		getLogger().info("  ");
		getLogger().info(" ...version " + getDescription().getVersion() + " has " + status + " successfully!");
	}
}
