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

import com.censoredsoftware.library.serializable.DataSerializable;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unchecked")
public abstract class DataAccess<K extends Comparable, V extends DataAccess<K, V>> implements DataSerializable
{
	@SuppressWarnings("RedundantCast")
	private final Class<V> clazz = (Class<V>) ((V) this).getClass();

	/*
	 * Access to Data Object Classes from Data Manager.
	 */

	protected abstract K getId();

	/*
	 * Direct access to Data Manager from Data Object Classes.
	 */

	public V getDirect(K key)
	{
		return DataManager.DATA_MANAGER.getFor(clazz, key);
	}

	public Collection<V> allDirect()
	{
		return DataManager.DATA_MANAGER.getAllOf(clazz);
	}

	public Collection<V> allDirectWith(Predicate<V> predicate)
	{
		return Collections2.filter(allDirect(), predicate);
	}

	public ConcurrentMap<K, V> mapDirect()
	{
		return DataManager.DATA_MANAGER.getMapFor(clazz);
	}

	public void putDirect(K key, V value)
	{
		DataManager.DATA_MANAGER.putFor(clazz, key, value);
	}

	public void removeDirect(K key)
	{
		DataManager.DATA_MANAGER.removeFor(clazz, key);
	}

	/*
	 * Convenience methods for Data Object Classes.
	 */

	public void save()
	{
		putDirect(getId(), (V) this);
	}

	public void remove()
	{
		removeDirect(getId());
	}
}
