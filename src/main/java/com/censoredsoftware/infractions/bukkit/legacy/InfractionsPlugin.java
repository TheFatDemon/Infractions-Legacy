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

import com.censoredsoftware.infractions.bukkit.legacy.data.DataManager;
import com.censoredsoftware.infractions.bukkit.legacy.util.MiscUtil;
import com.censoredsoftware.infractions.bukkit.legacy.util.SettingUtil;
import com.censoredsoftware.infractions.bukkit.legacy.util.Updater;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import java.util.Map;

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

	public void loadCommands()
	{
		CommandHandler ce = new CommandHandler();
		// info
		getCommand("infractions").setExecutor(ce);
		getCommand("virtues").setExecutor(ce);
		getCommand("history").setExecutor(ce);
		// actions
		getCommand("cite").setExecutor(ce);
		getCommand("uncite").setExecutor(ce);
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
		getLogger().info("Disabled cleanly.");
	}

	@Override
	public void onEnable()
	{
		inst = this;

		long firstTime = System.currentTimeMillis();
		getLogger().info("Initializing.");
		loadCommands();
		loadMetrics();
		initializeThreads();

		Map<Integer, String> scores = Maps.newHashMap();
		for(int i = 1; i < 6; i++)
			scores.put(i, ChatColor.translateAlternateColorCodes('&', SettingUtil.getSettingString("chat_score_" + i)));
		CHAT_SCORES = ImmutableMap.copyOf(scores);

		if(SettingUtil.getSettingBoolean("update"))
			new Updater(this, 44721, getFile(), Updater.UpdateType.DEFAULT, true);

		getLogger().info("Preparation completed in " + ((double) (System.currentTimeMillis() - firstTime) / 1000) + " seconds.");
	}

	/**
	 * API
	 */
	public static String getLevelForChat(Player player)
	{
		if(player.hasPermission("infractions.ignore")) return SettingUtil.getSettingString("chat_score_ignore");
		double scoreRange = MiscUtil.getMaxScore(player) / 5.0;
		int chatScore = (int) ((MiscUtil.getScore(player) != 0 ? MiscUtil.getScore(player) : 1) / scoreRange);
		if(chatScore < 1) chatScore = 1;
		if(chatScore > 5) chatScore = 5;
		return CHAT_SCORES.get(chatScore);
	}
}
