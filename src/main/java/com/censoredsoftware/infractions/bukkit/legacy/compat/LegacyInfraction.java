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

import com.censoredsoftware.infractions.bukkit.Infraction;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.infractions.bukkit.evidence.EvidenceType;
import com.censoredsoftware.infractions.bukkit.issuer.Issuer;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataManager;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataProvider;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataSerializable;
import com.censoredsoftware.infractions.bukkit.legacy.data.IdType;
import com.censoredsoftware.infractions.bukkit.legacy.util.MiscUtil;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class LegacyInfraction implements DataSerializable<String>
{
	private String id;
	private Infraction infraction;

	private LegacyInfraction()
	{
	}

	private LegacyInfraction(Infraction infraction)
	{
		id = MiscUtil.getInfractionId(infraction);
		this.infraction = infraction;
	}

	@DataProvider(idType = IdType.STRING)
	public static LegacyInfraction of(String id, ConfigurationSection conf)
	{
		LegacyInfraction data = new LegacyInfraction();
		data.id = id;
		data.infraction = unserialize(conf.getValues(true));
		return data;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public Map<String, Object> serialize()
	{
		return serialize(infraction);
	}

	public Infraction toInfraction()
	{
		return infraction;
	}

	@SuppressWarnings("unchecked")
	public static Infraction unserialize(Map<String, Object> map)
	{
		try
		{
			UUID playerId = UUID.fromString(map.get("playerId").toString());
			Issuer issuer = ((LegacyIssuer) DataManager.getManager().getFor(LegacyIssuer.class, map.get("issuer").toString())).toIssuer();
			Long timeCreated = Long.parseLong(map.get("timeCreated").toString());
			String reason = map.get("reason").toString();
			Integer score = Integer.parseInt(map.get("score").toString());
			Set<Evidence> evidence = Sets.newHashSet();
			if(map.containsKey("evidence") && !((List<Map<String, Object>>) map.get("evidence")).isEmpty())
			{
				evidence = Sets.newHashSet(Collections2.transform((List<Map<String, Object>>) map.get("evidence"), new Function<Map<String, Object>, Evidence>()
				{
					@Override
					public Evidence apply(Map<String, Object> map)
					{
						Issuer issuer = ((LegacyIssuer) DataManager.getManager().getFor(LegacyIssuer.class, map.get("issuer").toString())).toIssuer();
						EvidenceType type = EvidenceType.valueOf(map.get("type").toString());
						Long timeCreated = Long.parseLong(map.get("timeCreated").toString());
						String data = map.get("data").toString();
						return new Evidence(issuer, type, timeCreated, data);
					}
				}));
			}
			List<String> notes = (List<String>) (map.containsKey("notes") ? map.get("notes") : Lists.newArrayList());
			Infraction infraction = new Infraction(playerId, timeCreated, reason, score, issuer, evidence);
			infraction.setNotes(notes);
			return infraction;
		}
		catch(NullPointerException ex)
		{
			return null;
		}
	}

	public static Map<String, Object> serialize(Infraction infraction)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("playerId", infraction.getPlayerId().toString());
		LegacyIssuer.of(infraction.getIssuer()).saveIfAbsent();
		map.put("issuer", infraction.getIssuer().getId());
		map.put("timeCreated", infraction.getTimeCreated());
		map.put("reason", infraction.getReason());
		map.put("score", infraction.getScore());
		map.put("evidence", Lists.newArrayList(Collections2.transform(infraction.getEvidence(), new Function<Evidence, Map<String, Object>>()
		{
			@Override
			public Map<String, Object> apply(Evidence evidence)
			{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("data", evidence.getRawData());
				LegacyIssuer.of(evidence.getIssuer()).saveIfAbsent();
				map.put("issuer", evidence.getIssuer().getId());
				map.put("timeCreated", evidence.getTimeCreated());
				map.put("type", evidence.getType().name());
				return map;
			}
		})));
		map.put("notes", infraction.getNotes());
		return map;
	}

	public static LegacyInfraction of(Infraction infraction)
	{
		return new LegacyInfraction(infraction);
	}
}
