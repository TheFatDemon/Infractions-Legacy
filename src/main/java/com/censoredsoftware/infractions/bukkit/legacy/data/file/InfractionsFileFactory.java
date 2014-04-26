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
import com.censoredsoftware.infractions.bukkit.legacy.data.DataType;
import com.censoredsoftware.infractions.bukkit.legacy.data.IdType;
import com.censoredsoftware.infractions.bukkit.legacy.data.Register;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Factory for constructing DemigodsFiles in a safe and generic way.
 */
@SuppressWarnings("unchecked")
public class InfractionsFileFactory
{
	/**
	 * Private constructor for this factory.
	 */
	private InfractionsFileFactory()
	{
	}

	/**
	 * Create a Demigods File from just the DataType and file path.
	 *
	 * @param type     The DataType this file will be persisting.
	 * @param filePath The path to the file directory.
	 * @return A new DemigodsFile object.
	 */
	public static InfractionsFile create(DataType type, String filePath)
	{
		return create(type.getIdType(), type.getDataClass(), type.getAbbreviation(), filePath);
	}

	/**
	 * Create a Demigods File from the IdType, data class, abbreviation, and file path.
	 *
	 * @param idType    The IdType this file will be using.
	 * @param dataClass The class this file will be persisting data for.
	 * @param abbr      The abbreviation of the data type.
	 * @param filePath  The path to the file directory.
	 * @return A new DemigodsFile object.
	 */
	public static <K extends Comparable, V extends DataAccess<K, V>> InfractionsFile<K, V> create(final IdType idType, final Class<V> dataClass, String abbr, String filePath)
	{
		// Check for void type.
		if(IdType.VOID.equals(idType)) return null;

		// Construct a new Infractions File from the abbreviation, file extension, and file directory path.
		return new InfractionsFile<K, V>(abbr, ".know", filePath)
		{
			// Overridden method to create an new data object from the file data.
			@Override
			public V valueFromData(String stringId, ConfigurationSection conf)
			{
				try
				{
					// Look over all constructors in the data class.
					for(Method method : dataClass.getMethods())
					{
						// Attempt to find a registered constructor.
						Register methodConstructor = method.getAnnotation(Register.class);

						// Is the constructor suitable for use?
						if(methodConstructor == null || !Modifier.isStatic(method.getModifiers()) || !idType.equals(methodConstructor.idType())) continue;

						// So far so good, now we double check the params.
						Class<?>[] params = method.getParameterTypes();
						if(params.length < 2 || !params[0].equals(idType.getCastClass()) || !params[1].equals(ConfigurationSection.class))
							// No good.
							throw new RuntimeException("The defined constructor for a data file is invalid.");

						// Everything looks perfect so far. Last thing to do is construct a new instance.
						return (V) method.invoke(null, keyFromString(stringId), conf);
					}
					throw new RuntimeException("Demigods was unable to find a constructor for one of its data types.");
				}
				catch(Exception e)
				{
					throw new RuntimeException("Demigods can't manage it's own data for some reason.", e);
				}
			}

			@Override
			public K keyFromString(String stringId)
			{
				return idType.fromString(stringId);
			}
		};
	}
}
