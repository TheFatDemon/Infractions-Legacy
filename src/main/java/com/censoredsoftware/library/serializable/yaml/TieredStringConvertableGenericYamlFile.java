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

package com.censoredsoftware.library.serializable.yaml;

import com.censoredsoftware.library.serializable.DataSerializable;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A key-get (generic-generic) tiered collection of data meant for one type of child class for DataSerializable.
 * Example layout of a Yaml file created by this class:
 * --------------------------------
 * key:
 * - value_data1: data
 * - value_data2:
 * - list_item1
 * - list_item2
 * - dummy
 * 4:
 * - another_value_data
 * --------------------------------
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
@SuppressWarnings({ "unchecked", "SuspiciousMethodCalls" })
public abstract class TieredStringConvertableGenericYamlFile<K extends Comparable, V extends DataSerializable> extends YamlConvertible<K, V> implements YamlFile
{
	/**
	 * Serialize the data for a specific key (from the loaded data).
	 *
	 * @param key The key.
	 * @return Map of the data from the get.
	 */
	public abstract Map<String, Object> serialize(K key);

	/**
	 * Get the data being held in the child class extending TieredGenericYamlFile.
	 *
	 * @return A ConcurrentMap of the data.
	 */
	public abstract ConcurrentMap<K, V> getLoadedData();

	@Override
	public ConcurrentMap<K, V> getCurrentFileData()
	{
		// Grab the current file.
		FileConfiguration data = YamlFileUtil.getConfiguration(getDirectoryPath(), getFullFileName());

		// Convert the raw file data into more usable data, in map form.
		ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>();
		for(String stringId : data.getKeys(false))
		{
			try
			{
				map.put(keyFromString(stringId), valueFromData(stringId, data.getConfigurationSection(stringId)));
			}
			catch(Exception ignored)
			{
				ignored.printStackTrace();
			}
		}
		return map;
	}

	@Override
	public boolean saveDataToFile()
	{
		// Grab the current file, and its data as a usable map.
		FileConfiguration currentFile = YamlFileUtil.getConfiguration(getDirectoryPath(), getFullFileName());
		final Map<K, V> currentFileMap = getCurrentFileData();

		// Create/overwrite a configuration section if new data exists.
		for(K key : Collections2.filter(getLoadedData().keySet(), new Predicate<K>()
		{
			@Override
			public boolean apply(K key)
			{
				return !currentFileMap.containsKey(key) || !currentFileMap.get(key).equals(getLoadedData().get(key));
			}
		}))
			currentFile.createSection(key.toString(), serialize(key));

		// Remove old unneeded data.
		for(K key : Collections2.filter(currentFileMap.keySet(), new Predicate<K>()
		{
			@Override
			public boolean apply(K key)
			{
				return !getLoadedData().keySet().contains(key);
			}
		}))
			currentFile.set(key.toString(), null);

		// Save the file!
		return YamlFileUtil.saveFile(getDirectoryPath(), getFullFileName(), currentFile);
	}
}
