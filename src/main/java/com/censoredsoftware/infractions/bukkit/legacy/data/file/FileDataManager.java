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

package com.censoredsoftware.infractions.bukkit.legacy.data.file;

import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataManager;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataSerializable;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataType;
import com.censoredsoftware.infractions.bukkit.legacy.data.TempDataManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * This is the data management file for Demigods.
 */
@SuppressWarnings("unchecked")
public class FileDataManager extends DataManager
{
	// -- VARIABLES -- //

	// Data Folder
	public static final String SAVE_PATH = InfractionsPlugin.getInst().getDataFolder() + "/data/"; // Don't change this.

	// -- YAML FILES -- //

	ConcurrentMap<Class, InfractionsFile> yamlFiles;

	// -- UTIL METHODS -- //

	// Prevent accidental double init.
	private static boolean didInit = false;

	@Override
	public void init()
	{
		// Check if init has happened already...
		if(didInit) throw new RuntimeException("Data tried to initialize more than once.");

		// Preserve the load order.
		List<Class> fileOrder = Lists.newArrayList();

		// Create YAML files.
		yamlFiles = Maps.newConcurrentMap();
		for(DataType dataType : DataType.values())
		{
			InfractionsFile file = InfractionsFileFactory.create(dataType, SAVE_PATH);
			if(file == null) continue;
			InfractionsPlugin.getInst().getLogger().info("Marked \"" + dataType.name() + "\" for data import.");
			yamlFiles.put(dataType.getDataClass(), file);
			fileOrder.add(dataType.getDataClass());
		}

		// Load YAML files.
		for(Class clazz : fileOrder)
		{
			InfractionsFile file = yamlFiles.get(clazz);
			try
			{
				file.loadDataFromFile();
			}
			catch(Exception ex)
			{
				InfractionsPlugin.getInst().getLogger().severe("Failure to import data from \"" + file.getName() + "\" file.");
				continue;
			}
			InfractionsPlugin.getInst().getLogger().info("Data import from \"" + file.getName() + "\" file complete.");
		}

		// Let the plugin know that this has finished.
		didInit = true;
	}

	@Override
	public void save()
	{
		// Make sure data actually is loaded.
		if(!didInit) return;

		// Save all data.
		for(InfractionsFile data : yamlFiles.values())
			data.saveDataToFile();
	}

	@Override
	public void flushData()
	{
		// Make sure data actually is loaded.
		if(!didInit) return;

		// Kick everyone
		for(Player player : Bukkit.getOnlinePlayers())
			player.kickPlayer(ChatColor.GREEN + "Resetting data.");

		// Clear the data
		for(InfractionsFile data : yamlFiles.values())
			data.clear();
		TempDataManager.purge();

		save();

		// Reload the PLUGIN
		Bukkit.getServer().getPluginManager().disablePlugin(InfractionsPlugin.getInst());
		Bukkit.getServer().getPluginManager().enablePlugin(InfractionsPlugin.getInst());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V extends DataSerializable<K>, I> I getFor(final Class<V> clazz, final K key)
	{
		if(getFile(clazz).containsKey(key)) return (I) getFile(clazz).get(key);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V extends DataSerializable<K>, I> Collection<I> getAllOf(final Class<V> clazz)
	{
		return (Collection<I>) getFile(clazz).values();
	}

	@Override
	public <K, V extends DataSerializable<K>, I> ConcurrentMap<K, I> getMapFor(final Class<V> clazz)
	{
		return (ConcurrentMap<K, I>) getFile(clazz).getLoadedData();
	}

	@SuppressWarnings("unchecked")
	private <K, V extends DataSerializable<K>, I> InfractionsFile<K, V, I> getFile(Class<V> clazz)
	{
		if(yamlFiles.containsKey(clazz)) return (InfractionsFile<K, V, I>) yamlFiles.get(clazz);
		throw new UnsupportedOperationException("Infractions wants a data type that does not exist.");
	}
}
