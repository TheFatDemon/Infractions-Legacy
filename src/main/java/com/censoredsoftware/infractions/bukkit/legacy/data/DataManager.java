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

package com.censoredsoftware.infractions.bukkit.legacy.data;

import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.censoredsoftware.infractions.bukkit.legacy.data.file.FileDataManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

public abstract class DataManager
{
	static final DataManager DATA_MANAGER = findManager();

	private static DataManager findManager()
	{
		// Get the correct data manager.
		String saveMethod = InfractionsPlugin.getInst().getConfig().getString("saving.method", "file");
		if("file".equals(saveMethod.toLowerCase()))
		{
			InfractionsPlugin.getInst().getLogger().info("Enabling file save method.");
			return trainManager(FileDataManager.class);
		}
		InfractionsPlugin.getInst().getLogger().severe("\"" + saveMethod + "\" is not a valid save method.");
		InfractionsPlugin.getInst().getLogger().severe("Defaulting to file save method.");
		return trainManager(FileDataManager.class);
	}

	private static DataManager trainManager(Class<? extends DataManager> manager)
	{
		try
		{
			return manager.newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	protected abstract void init();

	protected abstract void save();

	protected abstract void flushData();

	public abstract <K, V extends DataSerializable<K>, I> I getFor(final Class<V> clazz, final K key);

	public abstract <K, V extends DataSerializable<K>, I> Collection<I> getAllOf(final Class<V> clazz);

	public abstract <K, V extends DataSerializable<K>, I> ConcurrentMap<K, I> getMapFor(final Class<V> clazz);

	public static DataManager getManager()
	{
		return DATA_MANAGER;
	}

	public static void initAllData()
	{
		DATA_MANAGER.init();
	}

	public static void saveAllData()
	{
		DATA_MANAGER.save();
	}

	public static void flushAllData()
	{
		DATA_MANAGER.flushData();
	}
}
