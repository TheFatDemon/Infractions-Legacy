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

import com.censoredsoftware.infractions.bukkit.legacy.data.DataAccess;
import com.censoredsoftware.library.serializable.yaml.TieredStringConvertableGenericYamlFile;
import com.google.common.collect.Maps;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstract class extending ConfigFile for easy yaml file creation inside of Demigods.
 *
 * @param <K> The id type.
 * @param <V> The data type.
 */
public abstract class InfractionsFile<K extends Comparable, V extends DataAccess<K, V>, I> extends TieredStringConvertableGenericYamlFile<K, I>
{
	private final String name;
	private final String fileName, fileType, savePath;
	ConcurrentMap<K, I> dataStore = Maps.newConcurrentMap();
	Method valueConstructor;

	public InfractionsFile(String fileName, String fileType, String savePath, String name, Method valueConstructor)
	{
		this.fileName = fileName;
		this.fileType = fileType;
		this.savePath = savePath;
		this.name = name;
		this.valueConstructor = valueConstructor;
	}

	public final String getName()
	{
		return name;
	}

	@Override
	public final ConcurrentMap<K, I> getLoadedData()
	{
		return dataStore;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Map<String, Object> serialize(K id)
	{
		return ((V) getLoadedData().get(id)).serialize();
	}

	@Override
	public String getDirectoryPath()
	{
		return savePath;
	}

	@Override
	public final String getFullFileName()
	{
		return fileName + fileType;
	}

	@Override
	public final void loadDataFromFile()
	{
		dataStore = getCurrentFileData();
	}

	public final boolean containsKey(K key)
	{
		return key != null && dataStore.containsKey(key);
	}

	public final I get(K key)
	{
		return dataStore.get(key);
	}

	public final void put(K key, I value)
	{
		dataStore.put(key, value);
	}

	public final void remove(K key)
	{
		dataStore.remove(key);
	}

	public final Set<Map.Entry<K, I>> entrySet()
	{
		return dataStore.entrySet();
	}

	public final Collection<I> values()
	{
		return dataStore.values();
	}

	public final void clear()
	{
		dataStore.clear();
	}
}
