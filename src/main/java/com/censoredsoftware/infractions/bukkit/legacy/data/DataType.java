package com.censoredsoftware.infractions.bukkit.legacy.data;

import com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyDossier;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Meta data for each data type.
 */
@SuppressWarnings("unchecked")
public enum DataType
{
	/**
	 * DemigodsPlayer.
	 */
	DOSSIER(LegacyDossier.class, IdType.UUID, "do"),
	/**
	 * TimedServerData.
	 */
	TIMED(TimedServerData.class, IdType.UUID, "ti"),
	/**
	 * ServerData.
	 */
	SERVER(ServerData.class, IdType.UUID, "srv"),
	/**
	 * Returned when no valid type can be found.
	 */
	INVALID(Invalid.class, IdType.VOID, "IF_YOU_SEE_THIS_PLEASE_TELL_US_ON_THE_SITE_YOU_DOWNLOADED_INFRACTIONS_FROM");

	private Class clazz;
	private IdType idType;
	private String abbr;

	/**
	 * Meta data for a data type.
	 *
	 * @param clazz  The object class that holds the data.
	 * @param idType The id type this data type uses.
	 * @param abbr   The abbreviation for use in certain data managers.
	 */
	private <V extends DataAccess<?, V>> DataType(Class<V> clazz, IdType idType, String abbr)
	{
		this.clazz = clazz;
		this.idType = idType;
		this.abbr = abbr;
	}

	@Override
	public String toString()
	{
		return name();
	}

	public Class getDataClass()
	{
		return clazz;
	}

	public IdType getIdType()
	{
		return idType;
	}

	public String getAbbreviation()
	{
		return abbr;
	}

	public <K extends Comparable, V extends DataAccess<K, V>> V cast(Object o)
	{
		return (V) clazz.cast(o);
	}

	public static String[] names()
	{
		String[] names = new String[values().length];
		for(int i = 0; i < values().length; i++)
			names[i] = values()[i].name();
		return names;
	}

	public static <V extends DataAccess> Class<V>[] classes()
	{
		Class<V>[] classes = new Class[values().length];
		for(int i = 0; i < values().length; i++)
			classes[i] = values()[i].clazz;
		return classes;
	}

	public static <V extends DataAccess> String nameFromClass(final Class<V> clazz)
	{
		return typeFromClass(clazz).name();
	}

	public static <V extends DataAccess> DataType typeFromClass(final Class<V> clazz)
	{
		return Iterables.find(Arrays.asList(values()), new Predicate<DataType>()
		{
			@Override
			public boolean apply(DataType dataType)
			{
				return clazz.equals(dataType.clazz);
			}
		}, INVALID);
	}

	public static <V extends DataAccess> Class<V> classFromName(final String name)
	{
		try
		{
			return Iterables.find(Arrays.asList(values()), new Predicate<DataType>()
			{
				@Override
				public boolean apply(DataType dataType)
				{
					return dataType.clazz != null && dataType.name().equals(name);
				}
			}).clazz;
		}
		catch(NoSuchElementException noElement)
		{
			throw new UnsupportedOperationException("Plugin tried accessing non-existent data type.", noElement);
		}
	}
}
