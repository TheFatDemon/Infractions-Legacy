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
import com.censoredsoftware.infractions.bukkit.legacy.data.DataAccess;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataManager;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataType;
import com.censoredsoftware.infractions.bukkit.legacy.data.TempDataManager;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
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

		// Create/Load YAML files.
		yamlFiles = Maps.newConcurrentMap();
		for(Class clazz : DataType.classes())
		{
			InfractionsFile file = InfractionsFileFactory.create(DataType.typeFromClass(clazz), SAVE_PATH);
			if(file == null) continue;
			file.loadDataFromFile();
			yamlFiles.put(clazz, file);
		}

		// Let the plugin know that this has finished.
		didInit = true;
	}

	@Override
	public void save()
	{
		for(InfractionsFile data : yamlFiles.values())
			data.saveDataToFile();
	}

	@Override
	public void flushData()
	{
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
	public <K extends Comparable, V extends DataAccess<K, V>> V getFor(final Class<V> clazz, final K key)
	{
		if(getFile(clazz).containsKey(key)) return getFile(clazz).get(key);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K extends Comparable, V extends DataAccess<K, V>> Collection<V> getAllOf(final Class<V> clazz)
	{
		return getFile(clazz).values();
	}

	@Override
	public <K extends Comparable, V extends DataAccess<K, V>> ConcurrentMap<K, V> getMapFor(final Class<V> clazz)
	{
		return getFile(clazz).getLoadedData();
	}

	@Override
	public <K extends Comparable, V extends DataAccess<K, V>> void putFor(final Class<V> clazz, final K key, final V value)
	{
		getFile(clazz).put(key, value);
	}

	@Override
	public <K extends Comparable, V extends DataAccess<K, V>> void removeFor(final Class<V> clazz, final K key)
	{
		getFile(clazz).remove(key);
	}

	@SuppressWarnings("unchecked")
	private <K extends Comparable, V extends DataAccess<K, V>> InfractionsFile<K, V> getFile(Class<V> clazz)
	{
		if(yamlFiles.containsKey(clazz)) return (InfractionsFile<K, V>) yamlFiles.get(clazz);
		throw new UnsupportedOperationException("Infractions wants a data type that does not exist.");
	}
}
