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

import org.bukkit.configuration.ConfigurationSection;

/**
 * A yaml file that has convertible key-get types.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public abstract class YamlConvertible<K, V>
{
	/**
	 * Convert a key from a string.
	 *
	 * @param stringKey The provided string.
	 * @return The converted key.
	 */
	public abstract K keyFromString(String stringKey);

	/**
	 * Convert to a get from a number of objects representing the data.
	 *
	 * @param stringKey The string key for the data.
	 * @param data      The provided data object.
	 * @return The converted get.
	 */
	public abstract V valueFromData(String stringKey, ConfigurationSection data);
}
