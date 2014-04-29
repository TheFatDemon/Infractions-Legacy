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

package com.censoredsoftware.infractions.bukkit.legacy.compat;

import com.censoredsoftware.infractions.bukkit.issuer.Issuer;
import com.censoredsoftware.infractions.bukkit.issuer.IssuerType;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataManager;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataProvider;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataSerializable;
import com.censoredsoftware.infractions.bukkit.legacy.data.IdType;
import com.censoredsoftware.infractions.bukkit.origin.Origin;
import com.censoredsoftware.infractions.bukkit.origin.OriginType;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// FIXME Origin is ignored in version 0.5.

public class LegacyIssuer implements DataSerializable<String>
{
	private Issuer issuer;

	private LegacyIssuer()
	{
	}

	private LegacyIssuer(Issuer issuer)
	{
		this.issuer = issuer;
	}

	@DataProvider(idType = IdType.STRING)
	public static LegacyIssuer of(String ignored, ConfigurationSection conf)
	{
		LegacyIssuer data = new LegacyIssuer();
		data.issuer = unserialize(conf.getValues(true));
		return data;
	}

	@Override
	public String getId()
	{
		return issuer.getId();
	}

	@Override
	public Map<String, Object> serialize()
	{
		return serialize(issuer);
	}

	public Issuer toIssuer()
	{
		return issuer;
	}

	@SuppressWarnings("unchecked")
	public static Issuer unserialize(Map<String, Object> map)
	{
		IssuerType type = IssuerType.valueOf(map.get("type").toString());
		String id = map.get("id").toString();
		Origin origin = unpackOrigin(((MemorySection) map.get("origin")).getValues(true));
		return new Issuer(type, id, origin);
	}

	public static Origin unpackOrigin(Map<String, Object> map)
	{
		String id = map.get("id").toString();
		String name = map.get("name").toString();
		OriginType type = OriginType.valueOf(map.get("type").toString());
		return new Origin(id, name, type);
	}

	public static Map<String, Object> serialize(Issuer issuer)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", issuer.getType().name());
		map.put("id", issuer.getId());
		map.put("origin", new Function<Origin, Map<String, Object>>()
		{
			@Override public Map<String, Object> apply(Origin origin)
			{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", origin.getId());
				map.put("name", origin.getName());
				map.put("type", origin.getType().name());
				return map;
			}
		}.apply(issuer.getOrigin()));
		return map;
	}

	@SuppressWarnings("unchecked")
	public static Issuer of(final String id)
	{
		LegacyIssuer issuer = Iterables.find((Collection<LegacyIssuer>) (Collection) DataManager.getManager().getAllOf(LegacyIssuer.class), new Predicate<LegacyIssuer>()
		{
			@Override
			public boolean apply(LegacyIssuer legacyIssuer)
			{
				return id.equals(legacyIssuer.getId());
			}
		}, null);
		return issuer != null ? issuer.toIssuer() : null;
	}

	public static LegacyIssuer of(Issuer issuer)
	{
		LegacyIssuer legacyIssuer = new LegacyIssuer(issuer);
		return (LegacyIssuer) DataManager.getManager().getMapFor(LegacyIssuer.class).putIfAbsent(issuer.getId(), legacyIssuer);
	}

	public void saveIfAbsent()
	{
		DataManager.getManager().getMapFor(LegacyIssuer.class).putIfAbsent(getId(), this);
	}
}
