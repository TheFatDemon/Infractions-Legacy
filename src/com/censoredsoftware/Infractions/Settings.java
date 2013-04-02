package com.censoredsoftware.Infractions;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Settings
{

	static Infractions plugin;

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

	public Settings(Infractions instance)
	{
		plugin = instance;
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
}
