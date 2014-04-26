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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SettingUtil
{
	static InfractionsPlugin plugin;

	static
	{
		plugin = InfractionsPlugin.getInst();
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}

	public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c)
	{
		if(c == null)
		{
			return new ArrayList<T>();
		}
		List<T> r = new ArrayList<T>(c.size());
		for(Object o : c)
			r.add(clazz.cast(o));
		return r;
	}

	public static List<String> fetchListString(FileConfiguration config, String path)
	{
		List<String> b = castList(String.class, config.getList(path));
		config.set(path, b);
		return b;
	}

	public static boolean getSettingBoolean(String id)
	{
		return !plugin.getConfig().isBoolean(id) || plugin.getConfig().getBoolean(id);
	}

	public static double getSettingDouble(String id)
	{
		if(plugin.getConfig().isDouble(id)) return plugin.getConfig().getDouble(id);
		else return -1;
	}

	public static int getSettingInt(String id)
	{
		if(plugin.getConfig().isInt(id)) return plugin.getConfig().getInt(id);
		else return -1;
	}

	public static String getSettingString(String id)
	{
		if(plugin.getConfig().isString(id)) return plugin.getConfig().getString(id);
		else return null;
	}
}
