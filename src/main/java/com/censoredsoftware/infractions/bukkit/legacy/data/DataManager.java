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

	protected abstract <K extends Comparable, V extends DataAccess<K, V>> V getFor(Class<V> clazz, K key);

	protected abstract <K extends Comparable, V extends DataAccess<K, V>> Collection<V> getAllOf(Class<V> clazz);

	protected abstract <K extends Comparable, V extends DataAccess<K, V>> ConcurrentMap<K, V> getMapFor(Class<V> clazz);

	protected abstract <K extends Comparable, V extends DataAccess<K, V>> void putFor(Class<V> clazz, K key, V value);

	protected abstract <K extends Comparable, V extends DataAccess<K, V>> void removeFor(Class<V> clazz, K key);

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
